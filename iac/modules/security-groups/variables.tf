variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "vpc_id" {
  description = "ID del VPC donde se crean los Security Groups"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block del VPC (para reglas de SG)"
  type        = string
}