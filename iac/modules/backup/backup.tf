data "aws_caller_identity" "current" {}

locals {
  account_id = data.aws_caller_identity.current.account_id
}

resource "aws_backup_vault" "primary" {
  name        = "${var.prefix}-backup-vault"
  kms_key_arn = var.kms_backups_arn

  tags = {
    Name = "${var.prefix}-backup-vault"
  }
}

data "aws_iam_policy_document" "vault_policy" {
  statement {
    sid    = "DenyDeleteRecoveryPoints"
    effect = "Deny"

    principals {
      type        = "*"
      identifiers = ["*"]
    }

    actions = [
      "backup:DeleteRecoveryPoint",
      "backup:UpdateRecoveryPointLifecycle",
      "backup:DeleteBackupVault"
    ]

    resources = ["*"]

    condition {
      test     = "StringNotEquals"
      variable = "aws:PrincipalArn"
      values   = [var.backup_role_arn]
    }
  }
}

resource "aws_backup_vault_policy" "primary" {
  backup_vault_name = aws_backup_vault.primary.name
  policy            = data.aws_iam_policy_document.vault_policy.json
}

resource "aws_backup_vault_notifications" "primary" {
  backup_vault_name = aws_backup_vault.primary.name
  sns_topic_arn     = var.sns_backup_arn

  backup_vault_events = [
    "BACKUP_JOB_FAILED",
    "BACKUP_JOB_COMPLETED",
    "RESTORE_JOB_FAILED",
    "COPY_JOB_FAILED"
  ]
}

resource "aws_backup_plan" "main" {
  name = "${var.prefix}-backup-plan"

  # Regla 1: RDS PostgreSQL
  rule {
    rule_name         = "rds-daily-backup"
    target_vault_name = aws_backup_vault.primary.name
    schedule          = "cron(0 3 * * ? *)"

    start_window      = 60
    completion_window = 180

    lifecycle {
      delete_after = 35
    }

    recovery_point_tags = {
      BackupType = "rds-daily"
      Workspace  = var.prefix
    }
  }

  # Regla 2: S3 Documentos 
  rule {
    rule_name         = "s3-docs-daily-backup"
    target_vault_name = aws_backup_vault.primary.name
    schedule          = "cron(0 3 * * ? *)"

    start_window      = 60
    completion_window = 360

    lifecycle {
      delete_after = 90
    }

    recovery_point_tags = {
      BackupType = "s3-daily"
      Workspace  = var.prefix
    }
  }

  tags = {
    Name = "${var.prefix}-backup-plan"
  }
}

resource "aws_backup_selection" "main" {
  name         = "${var.prefix}-backup-selection"
  plan_id      = aws_backup_plan.main.id
  iam_role_arn = var.backup_role_arn

  resources = [
    var.rds_arn,
    var.s3_docs_bucket_arn
  ]
}
