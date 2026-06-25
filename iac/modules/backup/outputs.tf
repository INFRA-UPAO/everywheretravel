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

output "cross_region_vault_arn" {
    description = "ARN del vault cross-region (solo prod, null en dev)"
    value       = var.is_prod ? aws_backup_vault.cross_region[0].arn : null
}