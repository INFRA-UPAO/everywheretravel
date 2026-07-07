variable "prefix" {
  description = "Prefijo único por workspace. Ej: everywhere-travel-dev"
  type        = string
}

variable "env" {
  description = "Nombre del workspace/entorno (dev, prod). Debe coincidir con el nombre del GitHub Environment."
  type        = string
}

variable "github_repo" {
  description = "Repositorio de GitHub en formato owner/repo, usado en la trust policy del OIDC"
  type        = string
  default     = "INFRA-UPAO/everywheretravel"
}

variable "create_provider" {
  description = "Si es true, crea el aws_iam_openid_connect_provider (recurso único por cuenta AWS). Solo debe ser true en un workspace."
  type        = bool
  default     = false
}

variable "create_plan_role" {
  description = "Si es true, crea el rol de solo lectura usado por terraform plan en Pull Requests. Solo debe ser true en un workspace."
  type        = bool
  default     = false
}

variable "tfstate_bucket_name" {
  description = "Nombre del bucket S3 donde vive el state remoto de Terraform (iac/backend.tf)"
  type        = string
  default     = "everywhere-travel-tfstate"
}
