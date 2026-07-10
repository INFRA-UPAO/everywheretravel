output "dashboard_name" {
  description = "Nombre del CloudWatch Dashboard"
  value       = aws_cloudwatch_dashboard.main.dashboard_name
}

output "dashboard_arn" {
  description = "ARN del CloudWatch Dashboard"
  value       = aws_cloudwatch_dashboard.main.dashboard_arn
}
