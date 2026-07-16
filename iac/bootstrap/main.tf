terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.49.0"
    }
    tls = {
      source = "hashicorp/tls"
    }
  }

  backend "s3" {
    bucket       = "everywhere-travel-tfstate"
    key          = "bootstrap/terraform.tfstate"
    region       = "us-east-2"
    use_lockfile = true
    encrypt      = true
  }
}

provider "aws" {
  region = "us-east-2"
}

resource "aws_s3_bucket" "tfstate" {
  #checkov:skip=CKV2_AWS_62: Bucket de state management no requiere event notifications
  #checkov:skip=CKV_AWS_144:Cross-region replication no aplica para bucket de tfstate con versionado habilitado
  #checkov:skip=CKV2_AWS_62:Event notifications no requeridas para bucket de gestion de estado
  #checkov:skip=CKV_AWS_18:Access logging omitido, no existe bucket de logs en bootstrap
  bucket        = "everywhere-travel-tfstate"
  force_destroy = false

  tags = {
    Name      = "everywhere-travel-tfstate"
    ManagedBy = "terraform-bootstrap"
  }
}

resource "aws_s3_bucket_versioning" "tfstate" {
  bucket = aws_s3_bucket.tfstate.id
  versioning_configuration {
    status = "Enabled"
  }
}

# Fix CKV_AWS_145: cifrado KMS en lugar de AES256.
# Usa la llave AWS-managed (alias/aws/s3) para evitar costo de CMK.
resource "aws_s3_bucket_server_side_encryption_configuration" "tfstate" {
  bucket = aws_s3_bucket.tfstate.id
  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm     = "aws:kms"
      kms_master_key_id = "alias/aws/s3"
    }
    bucket_key_enabled = true
  }
}

# Fix CKV2_AWS_61: lifecycle configuration para gestionar versiones antiguas del state.
resource "aws_s3_bucket_lifecycle_configuration" "tfstate" {
  bucket = aws_s3_bucket.tfstate.id

  rule {
    id     = "cleanup-old-state-versions"
    status = "Enabled"

    filter {}

    noncurrent_version_expiration {
      noncurrent_days = 90
    }

    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }

  depends_on = [aws_s3_bucket_versioning.tfstate]
}

resource "aws_s3_bucket_public_access_block" "tfstate" {
  bucket                  = aws_s3_bucket.tfstate.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

data "tls_certificate" "github_actions" {
  url = "https://token.actions.githubusercontent.com"
}

resource "aws_iam_openid_connect_provider" "github" {
  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.github_actions.certificates[0].sha1_fingerprint]

  tags = {
    Name      = "github-actions-oidc"
    ManagedBy = "terraform-bootstrap"
  }
}

data "aws_iam_policy_document" "plan_trust" {
  statement {
    sid     = "AllowGithubActionsPullRequestAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github.arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values   = ["repo:INFRA-UPAO/everywheretravel:pull_request"]
    }
  }
}

resource "aws_iam_role" "plan" {
  name               = "everywhere-travel-github-plan-role"
  assume_role_policy = data.aws_iam_policy_document.plan_trust.json

  tags = {
    Name      = "everywhere-travel-github-plan-role"
    ManagedBy = "terraform-bootstrap"
  }
}

data "aws_iam_policy_document" "plan_permissions" {
  #checkov:skip=CKV_AWS_108:Rol de solo lectura (Describe/Get/List); la mayoria de esas acciones no admite Resource a nivel de ARN
  #checkov:skip=CKV_AWS_356:Rol de solo lectura (Describe/Get/List); la mayoria de esas acciones no admite Resource a nivel de ARN
  statement {
    sid    = "ReadOnlyForPlan"
    effect = "Allow"
    actions = [
      "ec2:Describe*",
      "ecs:Describe*",
      "ecs:List*",
      "ecr:Describe*",
      "ecr:GetAuthorizationToken",
      "rds:Describe*",
      "rds:ListTagsForResource",
      "lambda:Get*",
      "lambda:List*",
      "s3:GetBucket*",
      "s3:GetAccelerateConfiguration",
      "s3:ListBucket",
      "s3:ListAllMyBuckets",
      "s3:GetObject",
      "cloudfront:Get*",
      "cloudfront:List*",
      "route53:Get*",
      "route53:List*",
      "cognito-idp:Describe*",
      "cognito-idp:List*",
      "cognito-idp:GetUserPoolMfaConfig",
      "apigateway:GET",
      "sqs:Get*",
      "sqs:List*",
      "sns:Get*",
      "sns:List*",
      "kms:Describe*",
      "kms:List*",
      "kms:GetKeyPolicy",
      "kms:GetKeyRotationStatus",
      "cloudformation:Describe*",
      "iam:Get*",
      "iam:List*",
      "backup:Describe*",
      "backup:List*",
      "wafv2:Get*",
      "wafv2:List*",
      "acm:Describe*",
      "acm:List*",
      "logs:Describe*",
      "cloudwatch:Describe*",
      "cloudwatch:List*",
      "sts:GetCallerIdentity",
    ]
    resources = ["*"]
  }

  statement {
    sid    = "S3TfstateLock"
    effect = "Allow"
    actions = [
      "s3:PutObject",
      "s3:DeleteObject",
    ]
    resources = [
      "${aws_s3_bucket.tfstate.arn}/*",
    ]
  }
}

resource "aws_iam_role_policy" "plan" {
  name   = "everywhere-travel-github-plan-policy"
  role   = aws_iam_role.plan.id
  policy = data.aws_iam_policy_document.plan_permissions.json
}

resource "aws_iam_role_policies_exclusive" "plan" {
  role_name    = aws_iam_role.plan.name
  policy_names = [aws_iam_role_policy.plan.name]
}

data "aws_caller_identity" "current" {}

locals {
  account_id   = data.aws_caller_identity.current.account_id
  environments = ["dev", "prod"]
}

data "aws_iam_policy_document" "deploy_trust" {
  for_each = toset(local.environments)

  statement {
    sid     = "AllowGithubActionsAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github.arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values   = ["repo:INFRA-UPAO/everywheretravel:environment:${each.key}"]
    }
  }
}

resource "aws_iam_role" "deploy" {
  for_each = toset(local.environments)

  name               = "everywhere-travel-${each.key}-github-deploy-role"
  assume_role_policy = data.aws_iam_policy_document.deploy_trust[each.key].json

  tags = {
    Name      = "everywhere-travel-${each.key}-github-deploy-role"
    ManagedBy = "terraform-bootstrap"
  }
}

data "aws_iam_policy_document" "deploy_broad_services" {
  #checkov:skip=CKV_AWS_107:Amplio por servicio, acordado para el proyecto
  #checkov:skip=CKV_AWS_108:Amplio por servicio, acordado para el proyecto
  #checkov:skip=CKV_AWS_109:Amplio por servicio, acordado para el proyecto
  #checkov:skip=CKV_AWS_110:Amplio por servicio, acordado para el proyecto
  #checkov:skip=CKV_AWS_111:Amplio por servicio, acordado para el proyecto
  #checkov:skip=CKV_AWS_356:Amplio por servicio, acordado para el proyecto
  statement {
    sid    = "BroadServiceAccess"
    effect = "Allow"
    actions = [
      "ec2:*",
      "elasticloadbalancing:*",
      "rds:*",
      "ecs:*",
      "ecr:*",
      "application-autoscaling:*",
      "lambda:*",
      "cloudformation:*",
      "serverlessrepo:*",
      "sqs:*",
      "sns:*",
      "secretsmanager:*",
      "backup:*",
      "backup-storage:*",
      "cloudwatch:*",
      "logs:*",
      "cognito-idp:*",
      "cognito-identity:*",
      "apigateway:*",
      "cloudfront:*",
      "acm:*",
      "wafv2:*",
      "waf:*",
      "waf-regional:*",
      "route53:*",
    ]
    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "deploy_broad_services" {
  for_each = toset(local.environments)

  name   = "everywhere-travel-${each.key}-github-deploy-broad-services-policy"
  role   = aws_iam_role.deploy[each.key].id
  policy = data.aws_iam_policy_document.deploy_broad_services.json
}

data "aws_iam_policy_document" "deploy_permissions" {
  #checkov:skip=CKV_AWS_109:KMS/Route53/STS sin ARN previsible por nombre, resto de la policy ya esta acotado por recurso
  #checkov:skip=CKV_AWS_111:KMS/Route53/STS sin ARN previsible por nombre, resto de la policy ya esta acotado por recurso
  #checkov:skip=CKV_AWS_356:KMS/Route53/STS sin ARN previsible por nombre, resto de la policy ya esta acotado por recurso
  for_each = toset(local.environments)

  statement {
    sid    = "S3ProjectBuckets"
    effect = "Allow"
    actions = [
      "s3:*",
    ]
    resources = [
      "arn:aws:s3:::everywhere-travel-${each.key}-*",
      "arn:aws:s3:::everywhere-travel-${each.key}-*/*",
      "arn:aws:s3:::aws-waf-logs-everywhere-travel-${each.key}",
      "arn:aws:s3:::aws-waf-logs-everywhere-travel-${each.key}/*",
    ]
  }

  statement {
    sid    = "S3TfstateBackend"
    effect = "Allow"
    actions = [
      "s3:GetObject",
      "s3:PutObject",
      "s3:DeleteObject",
      "s3:ListBucket",
    ]
    resources = [
      aws_s3_bucket.tfstate.arn,
      "${aws_s3_bucket.tfstate.arn}/*",
    ]
  }

  statement {
    sid    = "IAMProjectRolesAndPolicies"
    effect = "Allow"
    actions = [
      "iam:CreateRole",
      "iam:DeleteRole",
      "iam:GetRole",
      "iam:UpdateRole",
      "iam:TagRole",
      "iam:UntagRole",
      "iam:PutRolePolicy",
      "iam:DeleteRolePolicy",
      "iam:GetRolePolicy",
      "iam:ListRolePolicies",
      "iam:AttachRolePolicy",
      "iam:DetachRolePolicy",
      "iam:ListAttachedRolePolicies",
      "iam:ListInstanceProfilesForRole",
      "iam:PassRole",
    ]
    resources = ["arn:aws:iam::${local.account_id}:role/everywhere-travel-${each.key}-*"]
  }

  statement {
    sid       = "IAMOIDCRoleSelfManage"
    effect    = "Allow"
    actions   = ["iam:GetRole", "iam:ListRolePolicies", "iam:ListAttachedRolePolicies"]
    resources = ["arn:aws:iam::${local.account_id}:role/everywhere-travel-${each.key}-github-*"]
  }

  statement {
    sid       = "IAMOpenIDConnectProvider"
    effect    = "Allow"
    actions   = ["iam:GetOpenIDConnectProvider", "iam:ListOpenIDConnectProviders"]
    resources = ["*"]
  }

  statement {
    sid    = "KMSUsage"
    effect = "Allow"
    actions = [
      "kms:CreateKey",
      "kms:DescribeKey",
      "kms:ListKeys",
      "kms:ListAliases",
      "kms:ListResourceTags",
      "kms:CreateAlias",
      "kms:DeleteAlias",
      "kms:UpdateAlias",
      "kms:PutKeyPolicy",
      "kms:GetKeyPolicy",
      "kms:TagResource",
      "kms:UntagResource",
      "kms:EnableKeyRotation",
      "kms:GetKeyRotationStatus",
      "kms:ScheduleKeyDeletion",
      "kms:Decrypt",
      "kms:Encrypt",
      "kms:GenerateDataKey",
      "kms:CreateGrant",
      "kms:ListGrants",
      "kms:RevokeGrant",
      "kms:GetPublicKey",
      "kms:Sign",
    ]
    resources = ["*"]
  }

  statement {
    sid    = "IAMServerlessRepoRoles"
    effect = "Allow"
    actions = [
      "iam:CreateRole",
      "iam:DeleteRole",
      "iam:GetRole",
      "iam:UpdateRole",
      "iam:TagRole",
      "iam:UntagRole",
      "iam:PutRolePolicy",
      "iam:DeleteRolePolicy",
      "iam:GetRolePolicy",
      "iam:ListRolePolicies",
      "iam:AttachRolePolicy",
      "iam:DetachRolePolicy",
      "iam:ListAttachedRolePolicies",
      "iam:PassRole",
    ]
    resources = ["arn:aws:iam::${local.account_id}:role/serverlessrepo-*"]
  }

  statement {
    sid    = "ServerlessAppRepoDeploymentBucket"
    effect = "Allow"
    actions = [
      "s3:GetObject",
    ]
    resources = [
      "arn:aws:s3:::awsserverlessrepo-changesets-*/*",
    ]
  }

  statement {
    sid    = "Route53DomainVerification"
    effect = "Allow"
    actions = [
      "ses:GetIdentityVerificationAttributes",
      "route53domains:*",
    ]
    resources = ["*"]
  }

  statement {
    sid       = "STSCallerIdentity"
    effect    = "Allow"
    actions   = ["sts:GetCallerIdentity"]
    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "deploy" {
  for_each = toset(local.environments)

  name   = "everywhere-travel-${each.key}-github-deploy-policy"
  role   = aws_iam_role.deploy[each.key].id
  policy = data.aws_iam_policy_document.deploy_permissions[each.key].json
}

resource "aws_iam_role_policies_exclusive" "deploy" {
  for_each = toset(local.environments)

  role_name = aws_iam_role.deploy[each.key].name
  policy_names = [
    aws_iam_role_policy.deploy[each.key].name,
    aws_iam_role_policy.deploy_broad_services[each.key].name,
  ]
}
