variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "is_prod" {
  description = "Booleano para saber si es prod o dev"
  type        = bool
}

variable "kms_ecr_arn" {
  description = "ARN de la llave KMS para ECR"
  type        = string
}

variable "ecs_execution_role_arn" {
  description = "ARN del ECS Task Execution Role (pull de imágenes)"
  type        = string
}
