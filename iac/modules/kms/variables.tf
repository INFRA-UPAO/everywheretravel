variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "env" {
  description = "Nombre del workspace actual: dev o prod"
  type        = string
}
