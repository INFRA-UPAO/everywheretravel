variable "project_name" {
  description = "Nombre del proyecto"
  type        = string
  default     = "everywhere-travel"
}

variable "aws_region" {
  description = "Región AWS principal"
  type        = string
  default     = "us-east-2"
}

variable "vpc_cidr" {
  description = "CIDR block del VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "domain_name" {
  description = "Dominio principal de la aplicación"
  type        = string
}

variable "alert_email" {
  description = "Email para recibir alertas de infraestructura"
  type        = string
}

variable "db_name" {
  description = "Nombre de la base de datos PostgreSQL"
  type        = string
  default     = "everywhere_travel"
}

variable "db_username" {
  description = "Usuario de la base de datos"
  type        = string
  default     = "app_user"
}

variable "ecs_app_port" {
  description = "Puerto donde escucha la aplicación"
  type        = number
  default     = 8080
}

variable "lambda_memory" {
  description = "Memoria en MB para Lambda"
  type        = number
  default     = 256
}

variable "lambda_timeout" {
  description = "Timeout en segundos para Lambda"
  type        = number
  default     = 30
}

variable "zoho_verification_token" {
  description = "Token TXT de verificación de dominio Zoho"
  type        = string
  default     = ""
}

variable "zoho_dkim_cname_value" {
  description = "Valor CNAME para DKIM de Zoho"
  type        = string
  default     = ""
}
