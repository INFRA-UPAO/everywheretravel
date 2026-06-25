variable "prefix" {
    description = "Prefijo único por workspace"
    type        = string
}

variable "private_app_subnet_ids" {
    description = "IDs de las subnets privadas APP"
    type        = list(string)
}

variable "sg_alb_id" {
    description = "ID del Security Group del ALB"
    type        = string
}

variable "sg_ecs_task_id" {
    description = "ID del Security Group de ECS Tasks"
    type        = string
}

variable "s3_access_logs_bucket" {
    description = "Nombre del bucket S3 para access logs del ALB"
    type        = string
}

variable "ecs_cpu" {
    description = "CPU units para ECS Task"
    type        = number
}

variable "ecs_memory" {
    description = "Memoria en MB para ECS Task"
    type        = number
}

variable "ecs_app_port" {
    description = "Puerto donde escucha Spring Boot"
    type        = number
}

variable "ecs_min_tasks" {
    description = "Número mínimo de Tasks ECS"
    type        = number
}

variable "ecs_max_tasks" {
    description = "Número máximo de Tasks ECS"
    type        = number
}

variable "ecs_execution_role_arn" {
    description = "ARN del ECS Task Execution Role"
    type        = string
}

variable "ecs_task_role_arn" {
    description = "ARN del ECS Task Role"
    type        = string
}

variable "ecr_repo_url" {
    description = "URL del repositorio ECR"
    type        = string
}

variable "ecr_image_tag" {
    description = "Tag de la imagen Docker a desplegar"
    type        = string
    default     = "initial"
}

variable "sqs_queue_url" {
    description = "URL de la SQS Queue principal"
    type        = string
}

variable "s3_docs_bucket" {
    description = "Nombre del bucket S3 de documentos"
    type        = string
}

variable "rds_secret_arn" {
    description = "ARN del secret rds-credentials"
    type        = string
}

variable "kms_logs_arn" {
    description = "ARN de la llave KMS para CloudWatch Logs"
    type        = string
}


