# RDS Proxy multiplexa las conexiones de las ECS Tasks hacia RDS: con ecs_max_tasks=20
# x 10 conexiones del pool HikariCP cada una, el proxy evita que un pico de autoescalado
# agote las conexiones de Postgres y acorta el failover de Multi-AZ. Solo se crea en prod.

resource "aws_security_group" "rds_proxy" {
  #checkov:skip=CKV2_AWS_5:SG se referencia desde este mismo modulo (aws_db_proxy) via vpc_security_group_ids
  count       = var.is_prod ? 1 : 0
  name        = "${var.prefix}-sg-rds-proxy"
  description = "Security Group para RDS Proxy"
  vpc_id      = var.vpc_id

  tags = { Name = "${var.prefix}-sg-rds-proxy" }
}

resource "aws_vpc_security_group_ingress_rule" "proxy_from_ecs" {
  count                        = var.is_prod ? 1 : 0
  security_group_id            = aws_security_group.rds_proxy[0].id
  referenced_security_group_id = var.sg_ecs_task_id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL desde ECS Tasks hacia RDS Proxy"
}

resource "aws_vpc_security_group_egress_rule" "proxy_to_rds" {
  count                        = var.is_prod ? 1 : 0
  security_group_id            = aws_security_group.rds_proxy[0].id
  referenced_security_group_id = var.sg_rds_id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL hacia RDS"
}

resource "aws_vpc_security_group_ingress_rule" "rds_from_proxy" {
  count                        = var.is_prod ? 1 : 0
  security_group_id            = var.sg_rds_id
  referenced_security_group_id = aws_security_group.rds_proxy[0].id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL desde RDS Proxy"
}

resource "aws_vpc_security_group_egress_rule" "ecs_to_proxy" {
  count                        = var.is_prod ? 1 : 0
  security_group_id            = var.sg_ecs_task_id
  referenced_security_group_id = aws_security_group.rds_proxy[0].id
  from_port                    = 5432
  to_port                      = 5432
  ip_protocol                  = "tcp"
  description                  = "PostgreSQL hacia RDS Proxy"
}

data "aws_iam_policy_document" "rds_proxy_trust" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["rds.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "rds_proxy" {
  count              = var.is_prod ? 1 : 0
  name               = "${var.prefix}-rds-proxy-role"
  assume_role_policy = data.aws_iam_policy_document.rds_proxy_trust.json

  tags = { Name = "${var.prefix}-rds-proxy-role" }
}

data "aws_iam_policy_document" "rds_proxy_permissions" {
  statement {
    sid       = "SecretsManagerGet"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [var.rds_secret_arn]
  }

  statement {
    sid       = "KMSDecryptSecrets"
    effect    = "Allow"
    actions   = ["kms:Decrypt"]
    resources = [var.kms_secrets_arn]
  }
}

resource "aws_iam_role_policy" "rds_proxy" {
  count  = var.is_prod ? 1 : 0
  name   = "${var.prefix}-rds-proxy-policy"
  role   = aws_iam_role.rds_proxy[0].id
  policy = data.aws_iam_policy_document.rds_proxy_permissions.json
}

resource "aws_db_proxy" "main" {
  count                  = var.is_prod ? 1 : 0
  name                   = "${var.prefix}-rds-proxy"
  engine_family          = "POSTGRESQL"
  role_arn               = aws_iam_role.rds_proxy[0].arn
  vpc_subnet_ids         = var.private_data_subnet_ids
  vpc_security_group_ids = [aws_security_group.rds_proxy[0].id]
  require_tls            = true
  idle_client_timeout    = 1800

  auth {
    auth_scheme = "SECRETS"
    secret_arn  = var.rds_secret_arn
    iam_auth    = "DISABLED"
  }

  tags = { Name = "${var.prefix}-rds-proxy" }
}

resource "aws_db_proxy_default_target_group" "main" {
  count         = var.is_prod ? 1 : 0
  db_proxy_name = aws_db_proxy.main[0].name

  connection_pool_config {
    max_connections_percent      = 90
    max_idle_connections_percent = 50
  }
}

resource "aws_db_proxy_target" "main" {
  count                  = var.is_prod ? 1 : 0
  db_proxy_name          = aws_db_proxy.main[0].name
  target_group_name      = aws_db_proxy_default_target_group.main[0].name
  db_instance_identifier = var.rds_instance_id
}
