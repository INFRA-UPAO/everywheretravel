data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

# Fix CKV_AWS_149: rotacion automatica de credenciales RDS cada 30 dias.
resource "aws_secretsmanager_secret" "rds_credentials" {
  name                    = "${var.prefix}-rds-credentials"
  description             = "Credenciales de conexion a RDS PostgreSQL - ${var.prefix}"
  kms_key_id              = var.kms_secrets_arn
  recovery_window_in_days = 0

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

# Fix CKV_AWS_149: Lambda de rotacion usando el template AWS para RDS PostgreSQL.
resource "aws_serverlessapplicationrepository_cloudformation_stack" "rotation_lambda" {
  name           = "${var.prefix}-rds-rotation"
  application_id = "arn:aws:serverlessrepo:us-east-1:297356227824:applications/SecretsManagerRDSPostgreSQLRotationSingleUser"

  capabilities = ["CAPABILITY_IAM", "CAPABILITY_RESOURCE_POLICY"]

  parameters = {
    functionName = "${var.prefix}-rds-rotation"
    endpoint     = "https://secretsmanager.${local.region}.amazonaws.com"
    vpcSubnetIds = join(",", var.private_app_subnet_ids)
    vpcSecurityGroupIds = var.sg_lambda_id
  }

  tags = {
    Name = "${var.prefix}-rds-rotation"
  }
}

resource "aws_secretsmanager_secret_rotation" "rds_credentials" {
  secret_id           = aws_secretsmanager_secret.rds_credentials.id
  rotation_lambda_arn = aws_serverlessapplicationrepository_cloudformation_stack.rotation_lambda.outputs["RotationLambdaARN"]

  rotation_rules {
    automatically_after_days = 30
  }

  depends_on = [aws_secretsmanager_secret_policy.rds_credentials]
}
