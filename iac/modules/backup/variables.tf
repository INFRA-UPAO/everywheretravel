variable "prefix" {
    description = "Prefijo único por workspace"
    type        = string
}

variable "is_prod" {
    description = "true en prod → crea vault cross-region en us-east-1"
    type        = bool
}

variable "kms_backups_arn" {
    description = "ARN de la llave KMS para el vault primario"
    type        = string
}

variable "backup_role_arn" {
    description = "ARN del AWS Backup Role"
    type        = string
}

variable "rds_arn" {
    description = "ARN de la instancia RDS a respaldar"
    type        = string
}

variable "s3_docs_bucket_arn" {
    description = "ARN del bucket S3 de documentos a respaldar"
    type        = string
}

variable "sns_backup_arn" {
    description = "ARN del SNS Topic backup-alerts"
    type        = string
}