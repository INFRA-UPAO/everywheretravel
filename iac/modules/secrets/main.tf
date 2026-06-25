data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

# TODO: implementar aws_secretsmanager_secret_rotation cuando el modulo vpc-endpoints
# este desplegado. Requiere subnets privadas y el endpoint de Secrets Manager activo.
# Fix pendiente: CKV_AWS_149.
resource "aws_secretsmanager_secret" "rds_credentials" {
  name                    = "${var.prefix}-rds-credentials"
  description             = "Credenciales de conexion a RDS PostgreSQL - ${var.prefix}"
  kms_key_id              = var.kms_secrets_arn
  recovery_window_in_days = terraform.workspace == "dev" ? 0 : 7

  tags = {
    Name = "${var.prefix}-rds-credentials"
  }
}

resource "aws_secretsmanager_secret_version" "rds_credentials" {
  secret_id = aws_secretsmanager_secret.rds_credentials.id

  secret_string = jsonencode({
    host     = var.db_host
    port     = tostring(var.db_port)
    dbname   = var.db_name
    username = var.db_username
    password = var.db_password
  })

  lifecycle {
    ignore_changes = [secret_string]
  }
}

data "aws_iam_policy_document" "rds_secret_policy" {
  # ECS TASKS - Spring boot
  statement {
    sid    = "AllowECSExecutionRole"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [var.ecs_execution_role_arn]
    }

    actions   = ["secretsmanager:GetSecretValue"]
    resources = [aws_secretsmanager_secret.rds_credentials.arn]
  }

  statement {
    sid    = "AllowECSTaskRole"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [var.ecs_task_role_arn]
    }

    actions   = ["secretsmanager:GetSecretValue"]
    resources = [aws_secretsmanager_secret.rds_credentials.arn]
  }

  # Lambda doc-generante
  statement {
    sid    = "AllowLambdaDocgenRole"
    effect = "Allow"

    principals {
      type        = "AWS"
      identifiers = [var.lambda_docgen_role_arn]
    }

    actions   = ["secretsmanager:GetSecretValue"]
    resources = [aws_secretsmanager_secret.rds_credentials.arn]
  }

  # SM necesita acceso para ejecutar la rotación.
  statement {
    sid    = "AllowSecretsManagerRotation"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["secretsmanager.amazonaws.com"]
    }

    actions = [
      "secretsmanager:GetSecretValue",
      "secretsmanager:DescribeSecret",
      "secretsmanager:PutSecretValue",
      "secretsmanager:UpdateSecretVersionStage"
    ]

    resources = [aws_secretsmanager_secret.rds_credentials.arn]

    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values   = [local.account_id]
    }
  }

  # Denegar acceso desde fuera de la cuenta.
  statement {
    sid    = "DenyExternalAccess"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions   = ["secretsmanager:*"]
    resources = [aws_secretsmanager_secret.rds_credentials.arn]

    condition {
      test     = "StringNotEquals"
      variable = "aws:PrincipalAccount"
      values   = [local.account_id]
    }
  }
}

resource "aws_secretsmanager_secret_policy" "rds_credentials" {
  secret_arn = aws_secretsmanager_secret.rds_credentials.arn
  policy     = data.aws_iam_policy_document.rds_secret_policy.json
}
