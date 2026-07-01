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
