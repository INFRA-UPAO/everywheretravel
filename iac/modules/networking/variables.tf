variable "prefix" {
    description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
    type        = string
}

variable "vpc_cidr" {
    description = "CIDR block del VPC"
    type        = string
    default     = "10.0.0.0/16"
}

variable "nat_gateway_count" {
    description = "Número de NAT Gateways: 1 (dev) o 2 (prod)"
    type        = number
    default     = 1

validation {
    condition     = contains([1, 2], var.nat_gateway_count)
    error_message = "nat_gateway_count debe ser 1 (dev) o 2 (prod)."
    }
}