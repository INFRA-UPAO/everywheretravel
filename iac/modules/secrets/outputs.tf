output "rds_secret_arn" {
  description = "ARN del secret rds-credentials"
  value       = aws_secretsmanager_secret.rds_credentials.arn
}

output "rds_secret_name" {
  description = "Nombre del secret rds-credentials"
  value       = aws_secretsmanager_secret.rds_credentials.name
}

output "rds_secret_id" {
  description = "ID del secret (mismo que el ARN)"
  value       = aws_secretsmanager_secret.rds_credentials.id
}
