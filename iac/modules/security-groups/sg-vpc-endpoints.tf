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

resource "aws_vpc_security_group_ingress_rule" "vpce_sqs_from_ecs" {
    security_group_id            = aws_security_group.vpce_sqs.id
    referenced_security_group_id = aws_security_group.ecs_task.id
    from_port                    = 443
    to_port                      = 443
    ip_protocol                  = "tcp"
    description                  = "HTTPS desde ECS Tasks"
}

resource "aws_vpc_security_group_ingress_rule" "vpce_sqs_from_lambda" {
    security_group_id            = aws_security_group.vpce_sqs.id
    referenced_security_group_id = aws_security_group.lambda.id
    from_port                    = 443
    to_port                      = 443
    ip_protocol                  = "tcp"
    description                  = "HTTPS desde Lambda doc-generante"
}

resource "aws_vpc_security_group_ingress_rule" "vpce_sm_from_ecs" {
    security_group_id            = aws_security_group.vpce_sm.id
    referenced_security_group_id = aws_security_group.ecs_task.id
    from_port                    = 443
    to_port                      = 443
    ip_protocol                  = "tcp"
    description                  = "HTTPS desde ECS Tasks"
}

resource "aws_vpc_security_group_ingress_rule" "vpce_sm_from_lambda" {
    security_group_id            = aws_security_group.vpce_sm.id
    referenced_security_group_id = aws_security_group.lambda.id
    from_port                    = 443
    to_port                      = 443
    ip_protocol                  = "tcp"
    description                  = "HTTPS desde Lambda doc-generante"
}

resource "aws_vpc_security_group_ingress_rule" "vpce_logs_from_ecs" {
    security_group_id            = aws_security_group.vpce_logs.id
    referenced_security_group_id = aws_security_group.ecs_task.id
    from_port                    = 443
    to_port                      = 443
    ip_protocol                  = "tcp"
    description                  = "HTTPS desde ECS Tasks"
}

resource "aws_vpc_security_group_ingress_rule" "vpce_logs_from_lambda" {
    security_group_id            = aws_security_group.vpce_logs.id
    referenced_security_group_id = aws_security_group.lambda.id
    from_port                    = 443
    to_port                      = 443
    ip_protocol                  = "tcp"
    description                  = "HTTPS desde Lambda doc-generante"
}

resource "aws_vpc_security_group_ingress_rule" "vpce_ecr_from_ecs" {
    security_group_id            = aws_security_group.vpce_ecr.id
    referenced_security_group_id = aws_security_group.ecs_task.id
    from_port                    = 443
    to_port                      = 443
    ip_protocol                  = "tcp"
    description                  = "HTTPS desde ECS Tasks para pull de imagenes"
}
