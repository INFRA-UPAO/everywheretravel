output "ecs_execution_role_arn" {
  description = "ARN del ECS Task Execution Role"
  value       = aws_iam_role.ecs_task_execution.arn
}

output "ecs_execution_role_name" {
  description = "Nombre del ECS Task Execution Role"
  value       = aws_iam_role.ecs_task_execution.name
}

output "ecs_task_role_arn" {
  description = "ARN del ECS Task Role"
  value       = aws_iam_role.ecs_task.arn
}

output "ecs_task_role_name" {
  description = "Nombre del ECS Task Role"
  value       = aws_iam_role.ecs_task.name
}

output "lambda_docgen_role_arn" {
  description = "ARN del Lambda doc-generante Role"
  value       = aws_iam_role.lambda_docgen.arn
}

output "lambda_docgen_role_name" {
  description = "Nombre del Lambda doc-generante Role"
  value       = aws_iam_role.lambda_docgen.name
}

output "backup_role_arn" {
  description = "ARN del AWS Backup Role"
  value       = aws_iam_role.backup.arn
}

output "backup_role_name" {
  description = "Nombre del AWS Backup Role"
  value       = aws_iam_role.backup.name
}

output "lambda_edge_role_arn" {
  description = "ARN del Lambda@Edge Role"
  value       = aws_iam_role.lambda_edge.arn
}

output "lambda_edge_role_name" {
  description = "Nombre del Lambda@Edge Role"
  value       = aws_iam_role.lambda_edge.name
}
