output "proxy_endpoint" {
  description = "Endpoint del RDS Proxy (null si is_prod=false, no se crea en dev)"
  value       = one(aws_db_proxy.main[*].endpoint)
}