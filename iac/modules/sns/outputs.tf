output "sns_alerts_arn" {
  description = "ARN del SNS Topic de alertas generales de CloudWatch"
  value       = aws_sns_topic.alerts.arn
}

output "sns_alerts_name" {
  description = "Nombre del SNS Topic de alertas"
  value       = aws_sns_topic.alerts.name
}