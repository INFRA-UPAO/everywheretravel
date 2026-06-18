data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

# CMK 1 — S3 FRONTEND

resource "aws_kms_key" "s3_frontend" {
  description             = "Cifrado SSE-KMS para S3 Frontend Angular - ${var.env}"
  deletion_window_in_days = 7
  enable_key_rotation     = true
  multi_region            = false

  tags = {
    Name = "${var.prefix}-kms-s3-frontend"
  }
}

resource "aws_kms_alias" "s3_frontend" {
  name          = "alias/${var.prefix}/s3-frontend"
  target_key_id = aws_kms_key.s3_frontend.key_id
}

resource "aws_kms_key_policy" "s3_frontend" {
  key_id = aws_kms_key.s3_frontend.id

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
        Sid    = "AllowS3Service"
        Effect = "Allow"
        Principal = {
          Service = "s3.amazonaws.com"
        }
        Action = [
          "kms:GenerateDataKey",
          "kms:Decrypt"
        ]
        Resource = "*"
      }
    ]
  })
}

# CMK 2 — S3 DOCUMENTOS

resource "aws_kms_key" "s3_docs" {
  description             = "Cifrado SSE-KMS para S3 Documentos PDF - ${var.env}"
  deletion_window_in_days = 7
  enable_key_rotation     = true
  multi_region            = false

  tags = {
    Name = "${var.prefix}-kms-s3-docs"
  }
}

resource "aws_kms_alias" "s3_docs" {
  name          = "alias/${var.prefix}/s3-docs"
  target_key_id = aws_kms_key.s3_docs.key_id
}

resource "aws_kms_key_policy" "s3_docs" {
  key_id = aws_kms_key.s3_docs.id

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
        Sid    = "AllowS3Service"
        Effect = "Allow"
        Principal = {
          Service = "s3.amazonaws.com"
        }
        Action = [
          "kms:GenerateDataKey",
          "kms:Decrypt"
        ]
        Resource = "*"
      }
    ]
  })
}

# CMK 3 — RDS

resource "aws_kms_key" "rds" {
  description             = "Cifrado storage RDS PostgreSQL - ${var.env}"
  deletion_window_in_days = 7
  enable_key_rotation     = true
  multi_region            = false

  tags = {
    Name = "${var.prefix}-kms-rds"
  }
}

resource "aws_kms_alias" "rds" {
  name          = "alias/${var.prefix}/rds"
  target_key_id = aws_kms_key.rds.key_id
}

resource "aws_kms_key_policy" "rds" {
  key_id = aws_kms_key.rds.id

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
        Sid    = "AllowRDSService"
        Effect = "Allow"
        Principal = {
          Service = "rds.amazonaws.com"
        }
        Action = [
          "kms:CreateGrant",
          "kms:ListGrants",
          "kms:DescribeKey"
        ]
        Resource = "*"
      }
    ]
  })
}

# CMK 4 — SECRETS MANAGER

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
