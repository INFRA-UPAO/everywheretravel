resource "aws_kms_key" "backups" {
  description             = "Cifrado AWS Backup Vault - ${var.env}"
  deletion_window_in_days = 7
  enable_key_rotation     = true
  multi_region            = false

  tags = {
    Name = "${var.prefix}-kms-backups"
  }
}

resource "aws_kms_alias" "backups" {
  name          = "alias/${var.prefix}/backups"
  target_key_id = aws_kms_key.backups.key_id
}

resource "aws_kms_key_policy" "backups" {
  key_id = aws_kms_key.backups.id

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
        Sid    = "AllowBackupService"
        Effect = "Allow"
        Principal = {
          Service = "backup.amazonaws.com"
        }
        Action = [
          "kms:CreateGrant",
          "kms:ListGrants",
          "kms:DescribeKey",
          "kms:GenerateDataKey",
          "kms:Decrypt"
        ]
        Resource = "*"
      }
    ]
  })
}
