output "s3_gateway_endpoint_id" {
  description = "ID del VPC Endpoint Gateway S3"
  value       = aws_vpc_endpoint.s3.id
}

output "ecr_api_endpoint_id" {
  description = "ID del VPC Endpoint Interface ECR API"
  value       = aws_vpc_endpoint.ecr_api.id
}

output "ecr_dkr_endpoint_id" {
  description = "ID del VPC Endpoint Interface ECR DkR"
  value       = aws_vpc_endpoint.ecr_dkr.id
}

output "sqs_endpoint_id" {
  description = "ID del VPC Endpoint Interface SQS"
  value       = aws_vpc_endpoint.sqs.id
}

output "secretsmanager_endpoint_id" {
  description = "ID del VPC Endpoint Interface Secrets Manager"
  value       = aws_vpc_endpoint.secretsmanager.id
}

output "cloudwatch_logs_endpoint_id" {
  description = "ID del VPC Endpoint Interface CloudWatch Logs"
  value       = aws_vpc_endpoint.cloudwatch_logs.id
}
