variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "alert_email" {
  description = "Email que recibe las alertas de infraestructura"
  type        = string
}
