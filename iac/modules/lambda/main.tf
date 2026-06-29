data "aws_region" "current" {}

locals {
  region = data.aws_region.current.region
}

# PLACEHOLDER ZIP
data "archive_file" "lambda_placeholder" {
  type        = "zip"
  output_path = "${path.module}/lambda_placeholder.zip"

  source {
    filename = "index.js"
    content  = <<-EOF
      'use strict';

      /**
       * Lambda doc-generante — PLACEHOLDER
       * Este código es reemplazado por CI/CD con la lógica real.
       * Propósito: permitir que Terraform cree la función
       * antes de que el código de negocio esté listo.
       */
      exports.handler = async (event) => {
        console.log('Lambda doc-generante invocada');
        console.log('Mensajes en el batch:', event.Records.length);

        for (const record of event.Records) {
          const body = JSON.parse(record.body);
          console.log('Mensaje recibido:', JSON.stringify(body));
          // TODO: implementar generación de PDF aquí
        }

        // Retornar sin errores para que SQS borre los mensajes.
        return { statusCode: 200, processed: event.Records.length };
      };
    EOF
  }
}

# LOG GROUP
# FIX CKV_AWS_338 — retención mínima 1 año
resource "aws_cloudwatch_log_group" "lambda" {
  name              = "/aws/lambda/${var.prefix}-doc-generante"
  retention_in_days = 365
  kms_key_id        = var.kms_logs_arn

  tags = {
    Name = "${var.prefix}-lambda-docgen-logs"
  }
}

# LAMBDA FUNCTION
resource "aws_lambda_function" "doc_generante" {
  # checkov:skip=CKV_AWS_272: Code signing no aplica a placeholder — CI/CD reemplaza el código vía ECR/S3
  # checkov:skip=CKV_AWS_116: Dead Letter Queue configurada via dead_letter_config con var.sqs_dlq_arn
  function_name = "${var.prefix}-doc-generante"
  role          = var.lambda_docgen_role_arn
  runtime       = "nodejs20.x"
  handler       = "index.handler"

  filename         = data.archive_file.lambda_placeholder.output_path
  source_code_hash = data.archive_file.lambda_placeholder.output_base64sha256

  memory_size                    = var.lambda_memory
  timeout                        = var.lambda_timeout
  reserved_concurrent_executions = 0
  kms_key_arn                    = var.kms_logs_arn

  # FIX CKV_AWS_116 — Dead Letter Queue
  dead_letter_config {
    target_arn = var.sqs_dlq_arn
  }

  tracing_config {
    mode = "Active"
  }

  vpc_config {
    subnet_ids         = var.private_app_subnet_ids
    security_group_ids = [var.sg_lambda_id]
  }

  environment {
    variables = {
      S3_DOCS_BUCKET                      = var.s3_docs_bucket
      S3_PREFIX                           = "generated/"
      DB_SECRET_ARN                       = var.rds_secret_arn
      SQS_QUEUE_URL                       = var.sqs_queue_url
      AWS_NODEJS_CONNECTION_REUSE_ENABLED = "1" # reutiliza conexiones HTTP
    }
  }

  logging_config {
    log_format = "JSON"
    log_group  = aws_cloudwatch_log_group.lambda.name
  }

  lifecycle {
    ignore_changes = [
      filename,
      source_code_hash,
      environment
    ]
  }

  tags = {
    Name = "${var.prefix}-doc-generante"
  }

  depends_on = [aws_cloudwatch_log_group.lambda]
}

# ESM
resource "aws_lambda_event_source_mapping" "sqs" {
  event_source_arn = var.sqs_queue_arn
  function_name    = aws_lambda_function.doc_generante.arn

  batch_size = 5

  maximum_batching_window_in_seconds = 10

  function_response_types = ["ReportBatchItemFailures"]
}
