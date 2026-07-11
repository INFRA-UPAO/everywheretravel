output "tfstate_bucket" {
  value = aws_s3_bucket.tfstate.bucket
}

output "github_oidc_provider_arn" {
  value = aws_iam_openid_connect_provider.github.arn
}

output "github_plan_role_arn" {
  value = aws_iam_role.plan.arn
}

output "github_deploy_role_arn_dev" {
  value = aws_iam_role.deploy["dev"].arn
}

output "github_deploy_role_arn_prod" {
  value = aws_iam_role.deploy["prod"].arn
}
