output "backup_vault_arn" {
  description = "ARN del vault primario de backup"
  value       = aws_backup_vault.primary.arn
}

output "backup_vault_name" {
  description = "Nombre del vault primario de backup"
  value       = aws_backup_vault.primary.name
}

output "backup_plan_id" {
  description = "ID del plan de backup"
  value       = aws_backup_plan.main.id
}
