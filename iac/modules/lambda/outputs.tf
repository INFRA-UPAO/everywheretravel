output "lambda_function_arn" {
  description = "ARN de la función Lambda doc-generante"
  value       = aws_lambda_function.doc_generante.arn
}

output "lambda_function_name" {
  description = "Nombre de la función Lambda (para CloudWatch alarms)"
  value       = aws_lambda_function.doc_generante.function_name
}

output "lambda_log_group_name" {
  description = "Nombre del CloudWatch Log Group de Lambda"
  value       = aws_cloudwatch_log_group.lambda.name
}
