output "ecr_repo_url" {
  description = "URL completa del repositorio ECR para usar en docker push/pull"
  value       = aws_ecr_repository.monolito.repository_url
}

output "ecr_repo_arn" {
  description = "ARN del repositorio ECR"
  value       = aws_ecr_repository.monolito.arn
}

output "ecr_repo_name" {
  description = "Nombre del repositorio ECR"
  value       = aws_ecr_repository.monolito.name
}
