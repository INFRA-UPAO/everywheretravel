data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

locals {
  account_id             = data.aws_caller_identity.current.account_id
  region                 = data.aws_region.current.region
  s3_docs_bucket_arn     = "arn:aws:s3:::${var.prefix}-docs-bucket"
  ecr_repo_arn           = "arn:aws:ecr:${local.region}:${local.account_id}:repository/${var.prefix}-monolito"
  sqs_queue_arn          = "arn:aws:sqs:${local.region}:${local.account_id}:${var.prefix}-docs-generation-queue"
  sqs_dlq_arn            = "arn:aws:sqs:${local.region}:${local.account_id}:${var.prefix}-docs-generation-dlq"
  rds_secret_arn_pattern = "arn:aws:secretsmanager:${local.region}:${local.account_id}:secret:${var.prefix}-rds-credentials*"
}

# ROL 1 — ECS TASK EXECUTION ROLE

data "aws_iam_policy_document" "ecs_execution_trust" {
  statement {
    sid     = "AllowECSTasksAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_task_execution" {
  name               = "${var.prefix}-ecs-task-execution-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_execution_trust.json

  tags = {
    Name = "${var.prefix}-ecs-task-execution-role"
  }
}

data "aws_iam_policy_document" "ecs_execution_permissions" {
  statement {
    sid       = "ECRAuthentication"
    effect    = "Allow"
    actions   = ["ecr:GetAuthorizationToken"]
    resources = ["*"]
  }

  statement {
    sid    = "ECRPullImage"
    effect = "Allow"
    actions = [
      "ecr:BatchCheckLayerAvailability",
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchGetImage"
    ]
    resources = [local.ecr_repo_arn]
  }

  statement {
    sid       = "KMSDecryptECR"
    effect    = "Allow"
    actions   = ["kms:Decrypt"]
    resources = [var.kms_ecr_arn]
  }

  statement {
    sid    = "CloudWatchLogs"
    effect = "Allow"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = ["arn:aws:logs:${local.region}:${local.account_id}:log-group:/aws/ecs/*"]
  }

  statement {
    sid       = "SecretsManagerGet"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [local.rds_secret_arn_pattern]
  }

  statement {
    sid       = "KMSDecryptSecrets"
    effect    = "Allow"
    actions   = ["kms:Decrypt"]
    resources = [var.kms_secrets_arn]
  }
}

resource "aws_iam_role_policy" "ecs_task_execution" {
  name   = "${var.prefix}-ecs-task-execution-policy"
  role   = aws_iam_role.ecs_task_execution.id
  policy = data.aws_iam_policy_document.ecs_execution_permissions.json
}

resource "aws_iam_role_policies_exclusive" "ecs_task_execution" {
  role_name    = aws_iam_role.ecs_task_execution.name
  policy_names = [aws_iam_role_policy.ecs_task_execution.name]
}

# ROL 2 — ECS TASK ROLE

data "aws_iam_policy_document" "ecs_task_trust" {
  statement {
    sid     = "AllowECSTasksAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "ecs_task" {
  name               = "${var.prefix}-ecs-task-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_trust.json

  tags = {
    Name = "${var.prefix}-ecs-task-role"
  }
}

data "aws_iam_policy_document" "ecs_task_permissions" {
  statement {
    sid    = "S3DocsAccess"
    effect = "Allow"
    actions = [
      "s3:PutObject",
      "s3:GetObject"
    ]
    resources = ["${local.s3_docs_bucket_arn}/*"]
  }

  statement {
    sid    = "KMSDocsAccess"
    effect = "Allow"
    actions = [
      "kms:GenerateDataKey",
      "kms:Decrypt"
    ]
    resources = [var.kms_s3_docs_arn]
  }

  statement {
    sid    = "SQSSend"
    effect = "Allow"
    actions = [
      "sqs:SendMessage",
      "sqs:GetQueueAttributes"
    ]
    resources = [local.sqs_queue_arn]
  }

  statement {
    sid    = "KMSSQSAccess"
    effect = "Allow"
    actions = [
      "kms:GenerateDataKey",
      "kms:Decrypt"
    ]
    resources = [var.kms_sqs_arn]
  }

  statement {
    sid       = "SecretsManagerGet"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [local.rds_secret_arn_pattern]
  }

  statement {
    sid       = "KMSSecretsAccess"
    effect    = "Allow"
    actions   = ["kms:Decrypt"]
    resources = [var.kms_secrets_arn]
  }

  statement {
    sid       = "CloudWatchMetrics"
    effect    = "Allow"
    actions   = ["cloudwatch:PutMetricData"]
    resources = ["*"]

    condition {
      test     = "StringEquals"
      variable = "cloudwatch:namespace"
      values   = ["EverywhereTravel"]
    }
  }
}

resource "aws_iam_role_policy" "ecs_task" {
  name   = "${var.prefix}-ecs-task-policy"
  role   = aws_iam_role.ecs_task.id
  policy = data.aws_iam_policy_document.ecs_task_permissions.json
}

resource "aws_iam_role_policies_exclusive" "ecs_task" {
  role_name    = aws_iam_role.ecs_task.name
  policy_names = [aws_iam_role_policy.ecs_task.name]
}

# ROL 3 — LAMBDA DOC-GENERANTE

data "aws_iam_policy_document" "lambda_docgen_trust" {
  statement {
    sid     = "AllowLambdaAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "lambda_docgen" {
  name               = "${var.prefix}-lambda-docgen-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_docgen_trust.json

  tags = {
    Name = "${var.prefix}-lambda-docgen-role"
  }
}

data "aws_iam_policy_document" "lambda_docgen_permissions" {
  statement {
    sid    = "SQSConsume"
    effect = "Allow"
    actions = [
      "sqs:ReceiveMessage",
      "sqs:DeleteMessage",
      "sqs:GetQueueAttributes",
      "sqs:ChangeMessageVisibility"
    ]
    resources = [
      local.sqs_queue_arn,
      local.sqs_dlq_arn
    ]
  }

  statement {
    sid    = "KMSSQSDecrypt"
    effect = "Allow"
    actions = [
      "kms:Decrypt",
      "kms:GenerateDataKey"
    ]
    resources = [var.kms_sqs_arn]
  }

  statement {
    sid       = "S3DocsPut"
    effect    = "Allow"
    actions   = ["s3:PutObject"]
    resources = ["${local.s3_docs_bucket_arn}/generated/*"]
  }

  statement {
    sid    = "KMSDocsAccess"
    effect = "Allow"
    actions = [
      "kms:GenerateDataKey",
      "kms:Decrypt"
    ]
    resources = [var.kms_s3_docs_arn]
  }

  statement {
    sid       = "SecretsManagerGet"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [local.rds_secret_arn_pattern]
  }

  statement {
    sid       = "KMSSecretsDecrypt"
    effect    = "Allow"
    actions   = ["kms:Decrypt"]
    resources = [var.kms_secrets_arn]
  }

  statement {
    sid    = "CloudWatchLogs"
    effect = "Allow"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = ["arn:aws:logs:${local.region}:${local.account_id}:log-group:/aws/lambda/*"]
  }

  statement {
    sid    = "VPCDescribeOnly"
    effect = "Allow"
    actions = [
      "ec2:DescribeNetworkInterfaces",
      "ec2:DescribeSubnets",
      "ec2:DescribeSecurityGroups",
      "ec2:DescribeVpcs"
    ]
    resources = ["*"]
  }

  statement {
    sid    = "VPCNetworkInterfaceWrite"
    effect = "Allow"
    actions = [
      "ec2:CreateNetworkInterface",
      "ec2:DeleteNetworkInterface",
      "ec2:AttachNetworkInterface",
      "ec2:DetachNetworkInterface"
    ]
    resources = [
      "arn:aws:ec2:${local.region}:${local.account_id}:network-interface/*",
      "arn:aws:ec2:${local.region}:${local.account_id}:subnet/*",
      "arn:aws:ec2:${local.region}:${local.account_id}:security-group/*"
    ]
  }
}

resource "aws_iam_role_policy" "lambda_docgen" {
  name   = "${var.prefix}-lambda-docgen-policy"
  role   = aws_iam_role.lambda_docgen.id
  policy = data.aws_iam_policy_document.lambda_docgen_permissions.json
}

resource "aws_iam_role_policy_attachment" "lambda_vpc_access" {
  role       = aws_iam_role.lambda_docgen.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policies_exclusive" "lambda_docgen" {
  role_name    = aws_iam_role.lambda_docgen.name
  policy_names = [aws_iam_role_policy.lambda_docgen.name]
}

# ROL 4 — AWS BACKUP

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

# ROL 5 — LAMBDA@EDGE

data "aws_iam_policy_document" "lambda_edge_trust" {
  statement {
    sid     = "AllowLambdaAndEdgeLambdaAssume"
    effect  = "Allow"
    actions = ["sts:AssumeRole"]

    principals {
      type = "Service"
      identifiers = [
        "lambda.amazonaws.com",
        "edgelambda.amazonaws.com"
      ]
    }
  }
}

resource "aws_iam_role" "lambda_edge" {
  name               = "${var.prefix}-lambda-edge-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_edge_trust.json

  tags = {
    Name = "${var.prefix}-lambda-edge-role"
  }
}

data "aws_iam_policy_document" "lambda_edge_permissions" {
  statement {
    sid    = "CloudWatchLogsEdge"
    effect = "Allow"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = ["arn:aws:logs:*:${local.account_id}:log-group:/aws/lambda/*"]
  }
}

resource "aws_iam_role_policy" "lambda_edge" {
  name   = "${var.prefix}-lambda-edge-policy"
  role   = aws_iam_role.lambda_edge.id
  policy = data.aws_iam_policy_document.lambda_edge_permissions.json
}

resource "aws_iam_role_policies_exclusive" "lambda_edge" {
  role_name    = aws_iam_role.lambda_edge.name
  policy_names = [aws_iam_role_policy.lambda_edge.name]
}
