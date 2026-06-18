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
