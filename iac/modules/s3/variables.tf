variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "kms_s3_frontend_arn" {
  description = "ARN de la llave KMS para S3 Frontend"
  type        = string
}

variable "kms_s3_frontend_id" {
  description = "Key ID de la llave KMS para S3 Frontend"
  type        = string
}

variable "kms_s3_docs_arn" {
  description = "ARN de la llave KMS para S3 Documentos"
  type        = string
}

variable "kms_s3_docs_id" {
  description = "Key ID de la llave KMS para S3 Documentos"
  type        = string
}

variable "kms_logs_arn" {
  description = "ARN de la llave KMS para WAF Logs"
  type        = string
}

variable "kms_logs_id" {
  description = "Key ID de la llave KMS para Logs"
  type        = string
}

variable "ecs_task_role_arn" {
  description = "ARN del ECS Task Role (accede al bucket de docs)"
  type        = string
}

variable "lambda_docgen_role_arn" {
  description = "ARN del Lambda doc-generante Role (escribe PDFs)"
  type        = string
}

variable "backup_role_arn" {
  description = "ARN del AWS Backup Role (necesita acceso para backups)"
  type        = string
}
