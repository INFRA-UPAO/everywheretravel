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

resource "aws_security_group" "vpce_sqs" {
  name        = "${var.prefix}-sg-vpce-sqs"
  description = "Security Group para VPC Endpoint Interface SQS"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-vpce-sqs" }
}

resource "aws_security_group" "vpce_sm" {
  name        = "${var.prefix}-sg-vpce-sm"
  description = "Security Group para VPC Endpoint Interface Secrets Manager"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-vpce-sm" }
}

resource "aws_security_group" "vpce_logs" {
  name        = "${var.prefix}-sg-vpce-logs"
  description = "Security Group para VPC Endpoint Interface CloudWatch Logs"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-vpce-logs" }
}

resource "aws_security_group" "vpce_ecr" {
  name        = "${var.prefix}-sg-vpce-ecr"
  description = "Security Group para VPC Endpoints Interface ECR API y DkR"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-vpce-ecr" }
}

# SG ALB 
# INBOUND
resource "aws_vpc_security_group_ingress_rule" "alb_from_vpclink" {
  security_group_id            = aws_security_group.alb.id
  referenced_security_group_id = aws_security_group.vpclink.id
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  description                  = "Trafico desde VPC Link de API Gateway"
}

# EGRESS
resource "aws_vpc_security_group_egress_rule" "alb_to_ecs" {
  security_group_id            = aws_security_group.alb.id
  referenced_security_group_id = aws_security_group.ecs_task.id
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  description                  = "Trafico hacia ECS Tasks en puerto 8080"
}

# SG ECS TASK
# INBOUND
resource "aws_vpc_security_group_ingress_rule" "ecs_from_alb" {
  security_group_id            = aws_security_group.ecs_task.id
  referenced_security_group_id = aws_security_group.alb.id
  from_port                    = 8080
  to_port                      = 8080
  ip_protocol                  = "tcp"
  description                  = "Trafico desde ALB interno"
}

# EGRESS
resource "aws_vpc_security_group_egress_rule" "ecs_to_rds" {
  security_group_id            = aws_security_group.ecs_task.id
  referenced_security_group_id = aws_security_group.rds.id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL hacia RDS"
}

# EGRESS
resource "aws_vpc_security_group_egress_rule" "ecs_to_vpce_sqs" {
  security_group_id            = aws_security_group.ecs_task.id
  referenced_security_group_id = aws_security_group.vpce_sqs.id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "tcp"
  description                  = "HTTPS hacia VPC Endpoint SQS"
}

resource "aws_vpc_security_group_egress_rule" "ecs_to_vpce_sm" {
  security_group_id            = aws_security_group.ecs_task.id
  referenced_security_group_id = aws_security_group.vpce_sm.id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "tcp"
  description                  = "HTTPS hacia VPC Endpoint Secrets Manager"
}

resource "aws_vpc_security_group_egress_rule" "ecs_to_vpce_logs" {
  security_group_id            = aws_security_group.ecs_task.id
  referenced_security_group_id = aws_security_group.vpce_logs.id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "tcp"
  description                  = "HTTPS hacia VPC Endpoint CloudWatch Logs"
}

resource "aws_vpc_security_group_egress_rule" "ecs_to_vpce_ecr" {
  security_group_id            = aws_security_group.ecs_task.id
  referenced_security_group_id = aws_security_group.vpce_ecr.id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "tcp"
  description                  = "HTTPS hacia VPC Endpoints ECR"
}
