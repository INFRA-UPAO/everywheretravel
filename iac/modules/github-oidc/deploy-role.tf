data "aws_iam_policy_document" "deploy_trust" {
  statement {
    sid     = "AllowGithubActionsAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]

    principals {
      type        = "Federated"
      identifiers = [local.oidc_provider_arn]
    }

    condition {
      test     = "StringEquals"
      variable = "token.actions.githubusercontent.com:aud"
      values   = ["sts.amazonaws.com"]
    }

    condition {
      test     = "StringLike"
      variable = "token.actions.githubusercontent.com:sub"
      values   = ["repo:${var.github_repo}:environment:${var.env}"]
    }
  }
}

resource "aws_iam_role" "deploy" {
  name               = "${var.prefix}-github-deploy-role"
  assume_role_policy = data.aws_iam_policy_document.deploy_trust.json

  tags = {
    Name = "${var.prefix}-github-deploy-role"
  }
}

data "aws_iam_policy_document" "deploy_broad_services" {
  # checkov:skip=CKV_AWS_107:Amplio por servicio, acordado para el proyecto
  # checkov:skip=CKV_AWS_108:Amplio por servicio, acordado para el proyecto
  # checkov:skip=CKV_AWS_109:Amplio por servicio, acordado para el proyecto
  # checkov:skip=CKV_AWS_110:Amplio por servicio, acordado para el proyecto
  # checkov:skip=CKV_AWS_111:Amplio por servicio, acordado para el proyecto
  # checkov:skip=CKV_AWS_356:Amplio por servicio, acordado para el proyecto
  statement {
    sid    = "BroadServiceAccess"
    effect = "Allow"
    actions = [
      "ec2:*",
      "elasticloadbalancing:*",
      "rds:*",
      "ecs:*",
      "ecr:*",
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
  name   = "${var.prefix}-github-deploy-broad-services-policy"
  role   = aws_iam_role.deploy.id
  policy = data.aws_iam_policy_document.deploy_broad_services.json
}

data "aws_iam_policy_document" "deploy_permissions" {
  # checkov:skip=CKV_AWS_109:KMS/Route53/STS sin ARN previsible por nombre, resto de la policy ya esta acotado por recurso
  # checkov:skip=CKV_AWS_111:KMS/Route53/STS sin ARN previsible por nombre, resto de la policy ya esta acotado por recurso
  # checkov:skip=CKV_AWS_356:KMS/Route53/STS sin ARN previsible por nombre, resto de la policy ya esta acotado por recurso
  statement {
    sid    = "S3ProjectBuckets"
    effect = "Allow"
    actions = [
      "s3:*",
    ]
    resources = [
      "arn:aws:s3:::${var.prefix}-*",
      "arn:aws:s3:::${var.prefix}-*/*",
      "arn:aws:s3:::aws-waf-logs-${var.prefix}",
      "arn:aws:s3:::aws-waf-logs-${var.prefix}/*",
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
      local.tfstate_bucket_arn,
      "${local.tfstate_bucket_arn}/*",
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
    resources = [local.iam_role_arn_pattern]
  }

  statement {
    sid       = "IAMOIDCRoleSelfManage"
    effect    = "Allow"
    actions   = ["iam:GetRole", "iam:ListRolePolicies", "iam:ListAttachedRolePolicies"]
    resources = [local.iam_oidc_role_arn_pattern]
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
    ]
    resources = ["*"]
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
  name   = "${var.prefix}-github-deploy-policy"
  role   = aws_iam_role.deploy.id
  policy = data.aws_iam_policy_document.deploy_permissions.json
}

resource "aws_iam_role_policies_exclusive" "deploy" {
  role_name = aws_iam_role.deploy.name
  policy_names = [
    aws_iam_role_policy.deploy.name,
    aws_iam_role_policy.deploy_broad_services.name,
  ]
}
