data "aws_iam_policy_document" "plan_trust" {
  count = var.create_plan_role ? 1 : 0

  statement {
    sid     = "AllowGithubActionsPullRequestAssume"
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
      values   = ["repo:${var.github_repo}:pull_request"]
    }
  }
}

resource "aws_iam_role" "plan" {
  count = var.create_plan_role ? 1 : 0

  name               = "${var.prefix}-github-plan-role"
  assume_role_policy = data.aws_iam_policy_document.plan_trust[0].json

  tags = {
    Name = "${var.prefix}-github-plan-role"
  }
}

data "aws_iam_policy_document" "plan_permissions" {
  count = var.create_plan_role ? 1 : 0

  # checkov:skip=CKV_AWS_108:Rol de solo lectura (Describe/Get/List); la mayoria de esas acciones no admite Resource a nivel de ARN
  # checkov:skip=CKV_AWS_356:Rol de solo lectura (Describe/Get/List); la mayoria de esas acciones no admite Resource a nivel de ARN
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
      "s3:ListBucket",
      "s3:ListAllMyBuckets",
      "s3:GetObject",
      "cloudfront:Get*",
      "cloudfront:List*",
      "route53:Get*",
      "route53:List*",
      "cognito-idp:Describe*",
      "cognito-idp:List*",
      "apigateway:GET",
      "sqs:Get*",
      "sqs:List*",
      "sns:Get*",
      "sns:List*",
      "kms:Describe*",
      "kms:List*",
      "kms:GetKeyPolicy",
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
}

resource "aws_iam_role_policy" "plan" {
  count = var.create_plan_role ? 1 : 0

  name   = "${var.prefix}-github-plan-policy"
  role   = aws_iam_role.plan[0].id
  policy = data.aws_iam_policy_document.plan_permissions[0].json
}

resource "aws_iam_role_policies_exclusive" "plan" {
  count = var.create_plan_role ? 1 : 0

  role_name    = aws_iam_role.plan[0].name
  policy_names = [aws_iam_role_policy.plan[0].name]
}
