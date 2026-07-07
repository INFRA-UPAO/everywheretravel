data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

data "tls_certificate" "github_actions" {
  url = "https://token.actions.githubusercontent.com"
}

locals {
  account_id                = data.aws_caller_identity.current.account_id
  region                    = data.aws_region.current.region
  oidc_provider_arn         = "arn:aws:iam::${local.account_id}:oidc-provider/token.actions.githubusercontent.com"
  tfstate_bucket_arn        = "arn:aws:s3:::${var.tfstate_bucket_name}"
  ecr_repo_arn              = "arn:aws:ecr:${local.region}:${local.account_id}:repository/${var.prefix}-monolito"
  ecs_cluster_arn           = "arn:aws:ecs:${local.region}:${local.account_id}:cluster/${var.prefix}-*"
  ecs_service_arn           = "arn:aws:ecs:${local.region}:${local.account_id}:service/${var.prefix}-*/*"
  ecs_task_definition_arn   = "arn:aws:ecs:${local.region}:${local.account_id}:task-definition/${var.prefix}-*"
  lambda_function_arn       = "arn:aws:lambda:${local.region}:${local.account_id}:function:${var.prefix}-*"
  iam_role_arn_pattern      = "arn:aws:iam::${local.account_id}:role/${var.prefix}-*"
  iam_policy_arn_pattern    = "arn:aws:iam::${local.account_id}:policy/${var.prefix}-*"
  iam_oidc_role_arn_pattern = "arn:aws:iam::${local.account_id}:role/${var.prefix}-github-*"
}
