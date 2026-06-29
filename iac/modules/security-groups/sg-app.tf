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

resource "aws_security_group" "vpclink" {
    name        = "${var.prefix}-sg-vpclink"
    description = "Security Group para el VPC Link de API Gateway"
    vpc_id      = var.vpc_id

    tags = { Name = "${var.prefix}-sg-vpclink" }
}

resource "aws_vpc_security_group_ingress_rule" "alb_from_vpclink" {
    security_group_id            = aws_security_group.alb.id
    referenced_security_group_id = aws_security_group.vpclink.id
    from_port                    = 8080
    to_port                      = 8080
    ip_protocol                  = "tcp"
    description                  = "Trafico desde VPC Link de API Gateway"
}

resource "aws_vpc_security_group_egress_rule" "alb_to_ecs" {
    security_group_id            = aws_security_group.alb.id
    referenced_security_group_id = aws_security_group.ecs_task.id
    from_port                    = 8080
    to_port                      = 8080
    ip_protocol                  = "tcp"
    description                  = "Trafico hacia ECS Tasks en puerto 8080"
}

resource "aws_vpc_security_group_ingress_rule" "ecs_from_alb" {
    security_group_id            = aws_security_group.ecs_task.id
    referenced_security_group_id = aws_security_group.alb.id
    from_port                    = 8080
    to_port                      = 8080
    ip_protocol                  = "tcp"
    description                  = "Trafico desde ALB interno"
}

resource "aws_vpc_security_group_egress_rule" "ecs_to_rds" {
    security_group_id            = aws_security_group.ecs_task.id
    referenced_security_group_id = aws_security_group.rds.id
    from_port                    = 5432
    to_port                      = 5432
    ip_protocol                  = "tcp"
    description                  = "PostgreSQL hacia RDS"
}

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

resource "aws_vpc_security_group_ingress_rule" "vpclink_from_apigw" {
    security_group_id = aws_security_group.vpclink.id
    cidr_ipv4         = var.vpc_cidr
    from_port         = 8080
    to_port           = 8080
    ip_protocol       = "tcp"
    description       = "Trafico desde API Gateway via VPC Link (CIDR del VPC)"
}

resource "aws_vpc_security_group_egress_rule" "vpclink_to_alb" {
    security_group_id            = aws_security_group.vpclink.id
    referenced_security_group_id = aws_security_group.alb.id
    from_port                    = 8080
    to_port                      = 8080
    ip_protocol                  = "tcp"
    description                  = "Trafico hacia ALB interno"
}
