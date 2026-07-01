data "aws_iam_policy_document" "backup_trust" {
    statement {
        sid     = "AllowBackupAssume"
        effect  = "Allow"
        actions = ["sts:AssumeRole"]

        principals {
        type        = "Service"
        identifiers = ["backup.amazonaws.com"]
        }

        condition {
        test     = "StringEquals"
        variable = "aws:SourceAccount"
        values   = [local.account_id]
        }
    }
}

resource "aws_iam_role" "backup" {
    name               = "${var.prefix}-backup-role"
    assume_role_policy = data.aws_iam_policy_document.backup_trust.json

    tags = {
        Name = "${var.prefix}-backup-role"
    }
}

resource "aws_iam_role_policy_attachment" "backup_policy" {
    role       = aws_iam_role.backup.name
    policy_arn = "arn:aws:iam::aws:policy/service-role/AWSBackupServiceRolePolicyForBackup"
}

resource "aws_iam_role_policy_attachment" "backup_restore_policy" {
    role       = aws_iam_role.backup.name
    policy_arn = "arn:aws:iam::aws:policy/service-role/AWSBackupServiceRolePolicyForRestores"
}

resource "aws_iam_role_policy_attachment" "backup_s3_policy" {
    role       = aws_iam_role.backup.name
    policy_arn = "arn:aws:iam::aws:policy/AWSBackupServiceRolePolicyForS3Backup"
}

resource "aws_iam_role_policy_attachments_exclusive" "backup" {
    role_name = aws_iam_role.backup.name
    policy_arns = [
        aws_iam_role_policy_attachment.backup_policy.policy_arn,
        aws_iam_role_policy_attachment.backup_restore_policy.policy_arn,
        aws_iam_role_policy_attachment.backup_s3_policy.policy_arn
    ]
}

data "aws_iam_policy_document" "backup_kms" {
    statement {
        sid    = "KMSBackupAccess"
        effect = "Allow"
        actions = [
        "kms:CreateGrant",
        "kms:ListGrants",
        "kms:DescribeKey",
        "kms:GenerateDataKey",
        "kms:Decrypt"
        ]
        resources = [var.kms_backups_arn]
    }
}

resource "aws_iam_role_policy" "backup_kms" {
    name   = "${var.prefix}-backup-kms-policy"
    role   = aws_iam_role.backup.id
    policy = data.aws_iam_policy_document.backup_kms.json
}

resource "aws_iam_role_policies_exclusive" "backup" {
    role_name    = aws_iam_role.backup.name
    policy_names = [aws_iam_role_policy.backup_kms.name]
}
