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
