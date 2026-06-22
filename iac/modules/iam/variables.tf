variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "kms_s3_docs_arn" {
  description = "ARN de la llave KMS para S3 Documentos"
  type        = string
}

variable "kms_sqs_arn" {
  description = "ARN de la llave KMS para SQS"
  type        = string
}

variable "kms_secrets_arn" {
  description = "ARN de la llave KMS para Secrets Manager"
  type        = string
}

variable "kms_logs_arn" {
  description = "ARN de la llave KMS para CloudWatch Logs"
  type        = string
}

variable "kms_ecr_arn" {
  description = "ARN de la llave KMS para ECR"
  type        = string
}

variable "kms_backups_arn" {
  description = "ARN de la llave KMS para AWS Backup"
  type        = string
}
