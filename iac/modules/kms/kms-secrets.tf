resource "aws_kms_key" "secrets" {
  description             = "Cifrado Secrets Manager rds-credentials - ${var.env}"
  deletion_window_in_days = 7
  enable_key_rotation     = true
  multi_region            = false

  tags = {
    Name = "${var.prefix}-kms-secrets"
  }
}

resource "aws_kms_alias" "secrets" {
  name          = "alias/${var.prefix}/secrets"
  target_key_id = aws_kms_key.secrets.key_id
}

resource "aws_kms_key_policy" "secrets" {
  key_id = aws_kms_key.secrets.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "EnableRootAccess"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${local.account_id}:root"
        }
        Action   = "kms:*"
        Resource = "*"
      },
      {
        Sid    = "AllowSecretsManagerService"
        Effect = "Allow"
        Principal = {
          Service = "secretsmanager.amazonaws.com"
        }
        Action = [
          "kms:GenerateDataKey",
          "kms:Decrypt",
          "kms:CreateGrant",
          "kms:DescribeKey"
        ]
        Resource = "*"
        Condition = {
          StringEquals = {
            "kms:CallerAccount" = local.account_id
          }
        }
      }
    ]
  })
}
