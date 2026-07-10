output "sqs_queue_url" {
  description = "URL de la queue principal"
  value       = aws_sqs_queue.main.url
}

output "sqs_queue_arn" {
  description = "ARN de la queue principal"
  value       = aws_sqs_queue.main.arn
}

output "sqs_queue_name" {
  description = "Nombre de la queue principal"
  value       = aws_sqs_queue.main.name
}

output "sqs_dlq_url" {
  description = "URL de la Dead Letter Queue"
  value       = aws_sqs_queue.dlq.url
}

output "sqs_dlq_arn" {
  description = "ARN de la Dead Letter Queue"
  value       = aws_sqs_queue.dlq.arn
}

output "sqs_dlq_name" {
  description = "Nombre de la DLQ (usado en CloudWatch alarm)"
  value       = aws_sqs_queue.dlq.name
}
