output "deploy_role_arn" {
  description = "ARN del rol que asume GitHub Actions para desplegar en este workspace"
  value       = aws_iam_role.deploy.arn
}

output "plan_role_arn" {
  description = "ARN del rol de solo lectura para terraform plan en Pull Requests (null si create_plan_role=false)"
  value       = var.create_plan_role ? aws_iam_role.plan[0].arn : null
}
