output "sg_alb_id" {
  description = "ID del Security Group del ALB"
  value       = aws_security_group.alb.id
}

output "sg_ecs_task_id" {
  description = "ID del Security Group de ECS Tasks"
  value       = aws_security_group.ecs_task.id
}

output "sg_rds_id" {
  description = "ID del Security Group de RDS"
  value       = aws_security_group.rds.id
}

output "sg_lambda_id" {
  description = "ID del Security Group de Lambda"
  value       = aws_security_group.lambda.id
}

output "sg_vpclink_id" {
  description = "ID del Security Group del VPC Link"
  value       = aws_security_group.vpclink.id
}

output "sg_vpce_sqs_id" {
  description = "ID del Security Group del VPC Endpoint SQS"
  value       = aws_security_group.vpce_sqs.id
}

output "sg_vpce_sm_id" {
  description = "ID del Security Group del VPC Endpoint Secrets Manager"
  value       = aws_security_group.vpce_sm.id
}

output "sg_vpce_logs_id" {
  description = "ID del Security Group del VPC Endpoint CloudWatch Logs"
  value       = aws_security_group.vpce_logs.id
}

output "sg_vpce_ecr_id" {
  description = "ID del Security Group de los VPC Endpoints ECR"
  value       = aws_security_group.vpce_ecr.id
}