output "kms_s3_frontend_arn" {
  description = "ARN de la llave KMS para S3 Frontend"
  value       = aws_kms_key.s3_frontend.arn
}

output "kms_s3_frontend_id" {
  description = "Key ID de la llave KMS para S3 Frontend"
  value       = aws_kms_key.s3_frontend.key_id
}

output "kms_s3_docs_arn" {
  description = "ARN de la llave KMS para S3 Documentos"
  value       = aws_kms_key.s3_docs.arn
}

output "kms_s3_docs_id" {
  description = "Key ID de la llave KMS para S3 Documentos"
  value       = aws_kms_key.s3_docs.key_id
}

output "kms_rds_arn" {
  description = "ARN de la llave KMS para RDS PostgreSQL"
  value       = aws_kms_key.rds.arn
}

output "kms_rds_id" {
  description = "Key ID de la llave KMS para RDS"
  value       = aws_kms_key.rds.key_id
}

output "kms_secrets_arn" {
  description = "ARN de la llave KMS para Secrets Manager"
  value       = aws_kms_key.secrets.arn
}

output "kms_secrets_id" {
  description = "Key ID de la llave KMS para Secrets Manager"
  value       = aws_kms_key.secrets.key_id
}

output "kms_sqs_arn" {
  description = "ARN de la llave KMS para SQS"
  value       = aws_kms_key.sqs.arn
}

output "kms_sqs_id" {
  description = "Key ID de la llave KMS para SQS"
  value       = aws_kms_key.sqs.key_id
}

output "kms_logs_arn" {
  description = "ARN de la llave KMS para CloudWatch Logs y Flow Logs"
  value       = aws_kms_key.logs.arn
}

output "kms_logs_id" {
  description = "Key ID de la llave KMS para Logs"
  value       = aws_kms_key.logs.key_id
}

output "kms_ecr_arn" {
  description = "ARN de la llave KMS para ECR"
  value       = aws_kms_key.ecr.arn
}

output "kms_ecr_id" {
  description = "Key ID de la llave KMS para ECR"
  value       = aws_kms_key.ecr.key_id
}

output "kms_backups_arn" {
  description = "ARN de la llave KMS para AWS Backup"
  value       = aws_kms_key.backups.arn
}

output "kms_backups_id" {
  description = "Key ID de la llave KMS para Backup"
  value       = aws_kms_key.backups.key_id
}

output "kms_route53_logs_arn" {
  description = "ARN de la llave KMS para Route53 query logs (us-east-1)"
  value       = aws_kms_key.route53_logs.arn
}

output "kms_dnssec_arn" {
  description = "ARN de la llave KMS asimetrica para Route53 DNSSEC (us-east-1)"
  value       = aws_kms_key.dnssec.arn
}
