output "ecs_cluster_arn" {
  description = "ARN del ECS Cluster"
  value       = aws_ecs_cluster.main.arn
}

output "ecs_cluster_name" {
  description = "Nombre del ECS Cluster"
  value       = aws_ecs_cluster.main.name
}

output "ecs_service_name" {
  description = "Nombre del ECS Service"
  value       = aws_ecs_service.monolito.name
}

output "alb_arn" {
  description = "ARN del ALB interno"
  value       = aws_lb.main.arn
}

output "alb_listener_arn" {
  description = "ARN del ALB Listener"
  value       = aws_lb_listener.main.arn
}

output "alb_dns_name" {
  description = "DNS name del ALB (para configurar en API Gateway)"
  value       = aws_lb.main.dns_name
}

output "alb_arn_suffix" {
  description = "ARN suffix del ALB (para CloudWatch alarms)"
  value       = aws_lb.main.arn_suffix
}

output "target_group_arn" {
  description = "ARN del Target Group"
  value       = aws_lb_target_group.main.arn
}

output "target_group_arn_suffix" {
  description = "ARN suffix del Target Group (para CloudWatch alarms)"
  value       = aws_lb_target_group.main.arn_suffix
}

output "ecs_log_group_name" {
  description = "Nombre del CloudWatch Log Group de ECS"
  value       = aws_cloudwatch_log_group.ecs.name
}
