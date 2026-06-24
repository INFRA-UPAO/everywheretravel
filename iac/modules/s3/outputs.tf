output "s3_frontend_bucket" {
  description = "Nombre del bucket S3 Frontend"
  value       = aws_s3_bucket.frontend.bucket
}

output "s3_frontend_bucket_arn" {
  description = "ARN del bucket S3 Frontend"
  value       = aws_s3_bucket.frontend.arn
}

output "s3_frontend_bucket_id" {
  description = "ID del bucket S3 Frontend (mismo que el nombre)"
  value       = aws_s3_bucket.frontend.id
}

# Documentos
output "s3_docs_bucket" {
  description = "Nombre del bucket S3 Documentos"
  value       = aws_s3_bucket.docs.bucket
}

output "s3_docs_bucket_arn" {
  description = "ARN del bucket S3 Documentos"
  value       = aws_s3_bucket.docs.arn
}

output "s3_docs_bucket_id" {
  description = "ID del bucket S3 Documentos"
  value       = aws_s3_bucket.docs.id
}

# WAF Logs
output "s3_waf_logs_bucket" {
  description = "Nombre del bucket S3 WAF Logs"
  value       = aws_s3_bucket.waf_logs.bucket
}

output "s3_waf_logs_bucket_arn" {
  description = "ARN del bucket S3 WAF Logs"
  value       = aws_s3_bucket.waf_logs.arn
}

# Access Logs
output "s3_access_logs_bucket" {
  description = "Nombre del bucket S3 Access Logs"
  value       = aws_s3_bucket.access_logs.bucket
}

output "s3_access_logs_bucket_arn" {
  description = "ARN del bucket S3 Access Logs"
  value       = aws_s3_bucket.access_logs.arn
}

output "s3_access_logs_bucket_id" {
  description = "ID del bucket S3 Access Logs"
  value       = aws_s3_bucket.access_logs.id
}
