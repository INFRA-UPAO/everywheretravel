output "rds_endpoint" {
  description = "Endpoint de conexión a RDS (host:port)"
  value       = aws_db_instance.main.endpoint
}

output "rds_address" {
  description = "Hostname del endpoint de RDS"
  value       = aws_db_instance.main.address
}

output "rds_port" {
  description = "Puerto de RDS (5432)"
  value       = aws_db_instance.main.port
}

output "rds_identifier" {
  description = "Identificador de la instancia RDS"
  value       = aws_db_instance.main.identifier
}

output "rds_arn" {
  description = "ARN de la instancia RDS"
  value       = aws_db_instance.main.arn
}

output "rds_db_name" {
  description = "Nombre de la base de datos"
  value       = aws_db_instance.main.db_name
}

output "rds_password" {
  description = "Password generado para RDS"
  value       = random_password.db.result
  sensitive   = true
}
