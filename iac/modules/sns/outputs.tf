output "sns_alerts_arn" {
  description = "ARN del SNS Topic de alertas generales de CloudWatch"
  value       = aws_sns_topic.alerts.arn
}

output "sns_alerts_name" {
  description = "Nombre del SNS Topic de alertas"
  value       = aws_sns_topic.alerts.name
}

output "sns_backup_arn" {
  description = "ARN del SNS Topic de alertas de AWS Backup"
  value       = aws_sns_topic.backup_alerts.arn
}

output "sns_backup_name" {
  description = "Nombre del SNS Topic de backup alerts"
  value       = aws_sns_topic.backup_alerts.name
}