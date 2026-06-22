data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
  region     = data.aws_region.current.region
}

resource "aws_sns_topic" "alerts" {
  name = "${var.prefix}-alerts"

  fifo_topic = false
  tags = {
    Name = "${var.prefix}-alerts"
  }
}

# TOPIC 1 — ALERTS

resource "aws_sns_topic_policy" "alerts" {
  arn = aws_sns_topic.alerts.arn

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "EnableRootAccess"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${local.account_id}:root"
        }
        Action   = "sns:*"
        Resource = aws_sns_topic.alerts.arn
      },
      {
        Sid    = "AllowCloudWatchPublish"
        Effect = "Allow"
        Principal = {
          Service = "cloudwatch.amazonaws.com"
        }
        Action   = "sns:Publish"
        Resource = aws_sns_topic.alerts.arn
        Condition = {
          StringEquals = {
            "aws:SourceAccount" = local.account_id
          }
        }
      }
    ]
  })
}

resource "aws_sns_topic_subscription" "alerts_email" {
  topic_arn                       = aws_sns_topic.alerts.arn
  protocol                        = "email"
  endpoint                        = var.alert_email
  confirmation_timeout_in_minutes = 1
}

# TOPIC 2 — BACKUP ALERTS

resource "aws_sns_topic" "backup_alerts" {
  name = "${var.prefix}-backup-alerts"

  tags = {
    Name = "${var.prefix}-backup-alerts"
  }
}

resource "aws_sns_topic_policy" "backup_alerts" {
  arn = aws_sns_topic.backup_alerts.arn

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "EnableRootAccess"
        Effect = "Allow"
        Principal = {
          AWS = "arn:aws:iam::${local.account_id}:root"
        }
        Action   = "sns:*"
        Resource = aws_sns_topic.backup_alerts.arn
      },
      {
        Sid    = "AllowBackupPublish"
        Effect = "Allow"
        Principal = {
          Service = "backup.amazonaws.com"
        }
        Action   = "sns:Publish"
        Resource = aws_sns_topic.backup_alerts.arn
        Condition = {
          StringEquals = {
            "aws:SourceAccount" = local.account_id
          }
        }
      }
    ]
  })
}