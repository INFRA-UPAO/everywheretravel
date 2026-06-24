resource "aws_security_group" "alb" {
  name        = "${var.prefix}-sg-alb"
  description = "Security Group para el ALB interno"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-alb" }
}

resource "aws_security_group" "ecs_task" {
  name        = "${var.prefix}-sg-ecs-task"
  description = "Security Group para las ECS Tasks (Spring Boot)"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-ecs-task" }
}

resource "aws_security_group" "rds" {
  name        = "${var.prefix}-sg-rds"
  description = "Security Group para RDS PostgreSQL"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-rds" }
}

resource "aws_security_group" "lambda" {
  name        = "${var.prefix}-sg-lambda"
  description = "Security Group para Lambda doc-generante (en VPC)"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-lambda" }
}

resource "aws_security_group" "vpclink" {
  name        = "${var.prefix}-sg-vpclink"
  description = "Security Group para el VPC Link de API Gateway"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-vpclink" }
}