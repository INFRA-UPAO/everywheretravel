resource "aws_security_group" "rds" {
  #checkov:skip=CKV2_AWS_5:SG is attached in compute/vpc-endpoints modules
  name        = "${var.prefix}-sg-rds"
  description = "Security Group para RDS PostgreSQL"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-rds" }
}

resource "aws_security_group" "lambda" {
  #checkov:skip=CKV2_AWS_5:SG is attached in compute/vpc-endpoints modules
  name        = "${var.prefix}-sg-lambda"
  description = "Security Group para Lambda doc-generante (en VPC)"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-lambda" }
}

resource "aws_vpc_security_group_ingress_rule" "rds_from_ecs" {
  security_group_id            = aws_security_group.rds.id
  referenced_security_group_id = aws_security_group.ecs_task.id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL desde ECS Tasks"
}

resource "aws_vpc_security_group_ingress_rule" "rds_from_lambda" {
  security_group_id            = aws_security_group.rds.id
  referenced_security_group_id = aws_security_group.lambda.id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL desde Lambda doc-generante"
}

resource "aws_vpc_security_group_egress_rule" "lambda_to_rds" {
  security_group_id            = aws_security_group.lambda.id
  referenced_security_group_id = aws_security_group.rds.id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL hacia RDS"
}

resource "aws_vpc_security_group_egress_rule" "lambda_to_vpce_sqs" {
  security_group_id            = aws_security_group.lambda.id
  referenced_security_group_id = aws_security_group.vpce_sqs.id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "tcp"
  description                  = "HTTPS hacia VPC Endpoint SQS"
}

resource "aws_vpc_security_group_egress_rule" "lambda_to_vpce_sm" {
  security_group_id            = aws_security_group.lambda.id
  referenced_security_group_id = aws_security_group.vpce_sm.id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "tcp"
  description                  = "HTTPS hacia VPC Endpoint Secrets Manager"
}

resource "aws_vpc_security_group_egress_rule" "lambda_to_vpce_logs" {
  security_group_id            = aws_security_group.lambda.id
  referenced_security_group_id = aws_security_group.vpce_logs.id
  from_port                    = 443
  to_port                      = 443
  ip_protocol                  = "tcp"
  description                  = "HTTPS hacia VPC Endpoint CloudWatch Logs"
}

resource "aws_vpc_security_group_egress_rule" "lambda_to_s3" {
  security_group_id = aws_security_group.lambda.id
  prefix_list_id    = data.aws_prefix_list.s3.id
  from_port         = 443
  to_port           = 443
  ip_protocol       = "tcp"
  description       = "HTTPS hacia S3 para subir PDFs generados"
}
