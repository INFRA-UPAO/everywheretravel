# ALARMA 1 — DLQ MESSAGES
resource "aws_cloudwatch_metric_alarm" "dlq_messages" {
  alarm_name          = "${var.prefix}-dlq-messages"
  alarm_description   = "Hay mensajes en la DLQ — revisar PDFs fallidos"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  threshold           = 0
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/SQS"
  metric_name = "ApproximateNumberOfMessagesVisible"
  statistic   = "Sum"
  period      = 60

  dimensions = {
    QueueName = var.sqs_dlq_name
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-dlq-messages-alarm" }
}

# ALARMA 2 — ECS CPU
resource "aws_cloudwatch_metric_alarm" "ecs_cpu" {
  alarm_name          = "${var.prefix}-ecs-cpu"
  alarm_description   = "ECS CPU > 70% — posible necesidad de más Tasks"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  threshold           = 70
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/ECS"
  metric_name = "CPUUtilization"
  statistic   = "Average"
  period      = 60

  dimensions = {
    ClusterName = var.ecs_cluster_name
    ServiceName = var.ecs_service_name
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-ecs-cpu-alarm" }
}

# ALARMA 4 — ALB 5XX
resource "aws_cloudwatch_metric_alarm" "ecs_5xx" {
  alarm_name          = "${var.prefix}-ecs-5xx"
  alarm_description   = "ALB recibe > 2% de errores 5XX del backend"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 2
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "error_rate"
    expression  = "(errors/requests)*100"
    label       = "5XX Error Rate %"
    return_data = true
  }

  metric_query {
    id = "errors"
    metric {
      namespace   = "AWS/ApplicationELB"
      metric_name = "HTTPCode_Target_5XX_Count"
      stat        = "Sum"
      period      = 60
      dimensions = {
        LoadBalancer = var.alb_arn_suffix
        TargetGroup  = var.target_group_arn_suffix
      }
    }
  }

  metric_query {
    id = "requests"
    metric {
      namespace   = "AWS/ApplicationELB"
      metric_name = "RequestCount"
      stat        = "Sum"
      period      = 60
      dimensions = {
        LoadBalancer = var.alb_arn_suffix
      }
    }
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-ecs-5xx-alarm" }
}

# ALARMA 5 — RDS CONNECTIONS
resource "aws_cloudwatch_metric_alarm" "rds_connections" {
  alarm_name          = "${var.prefix}-rds-connections"
  alarm_description   = "RDS tiene > 80 conexiones activas — revisar pool config"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  threshold           = 80
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/RDS"
  metric_name = "DatabaseConnections"
  statistic   = "Average"
  period      = 60

  dimensions = {
    DBInstanceIdentifier = var.rds_identifier
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-rds-connections-alarm" }
}

# ALARMA 6 — RDS FREE STORAGE
resource "aws_cloudwatch_metric_alarm" "rds_free_storage" {
  alarm_name          = "${var.prefix}-rds-free-storage"
  alarm_description   = "RDS tiene < 5 GB libres — storage al 75% de capacidad"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 2
  threshold           = 5368709120
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/RDS"
  metric_name = "FreeStorageSpace"
  statistic   = "Average"
  period      = 300

  dimensions = {
    DBInstanceIdentifier = var.rds_identifier
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-rds-free-storage-alarm" }
}

# ALARMA 7 — RDS READ LATENCY
resource "aws_cloudwatch_metric_alarm" "rds_read_latency" {
  alarm_name          = "${var.prefix}-rds-read-latency"
  alarm_description   = "Latencia de lectura RDS > 100ms — revisar queries lentas"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 0.1 # 100ms en segundos
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/RDS"
  metric_name = "ReadLatency"
  statistic   = "Average"
  period      = 60

  dimensions = {
    DBInstanceIdentifier = var.rds_identifier
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-rds-read-latency-alarm" }
}


# ALARMA 8 — LAMBDA ERRORS
resource "aws_cloudwatch_metric_alarm" "lambda_errors" {
  alarm_name          = "${var.prefix}-lambda-errors"
  alarm_description   = "Lambda doc-generante falla > 5% de invocaciones"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 5
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "error_rate"
    expression  = "(errors/invocations)*100"
    label       = "Lambda Error Rate %"
    return_data = true
  }

  metric_query {
    id = "errors"
    metric {
      namespace   = "AWS/Lambda"
      metric_name = "Errors"
      stat        = "Sum"
      period      = 60
      dimensions = {
        FunctionName = var.lambda_function_name
      }
    }
  }

  metric_query {
    id = "invocations"
    metric {
      namespace   = "AWS/Lambda"
      metric_name = "Invocations"
      stat        = "Sum"
      period      = 60
      dimensions = {
        FunctionName = var.lambda_function_name
      }
    }
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-lambda-errors-alarm" }
}

# ALARMA 9 — LAMBDA DURATION
resource "aws_cloudwatch_metric_alarm" "lambda_duration" {
  alarm_name          = "${var.prefix}-lambda-duration"
  alarm_description   = "Lambda doc-generante p99 > 25s — cerca del timeout de 30s"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 3
  threshold           = 25000 # 25 segundos en ms
  treat_missing_data  = "notBreaching"

  namespace          = "AWS/Lambda"
  metric_name        = "Duration"
  extended_statistic = "p99"
  period             = 60

  dimensions = {
    FunctionName = var.lambda_function_name
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-lambda-duration-alarm" }
}

# ALARMA 10 — API GATEWAY 4XX
resource "aws_cloudwatch_metric_alarm" "apigw_4xx" {
  alarm_name          = "${var.prefix}-apigw-4xx"
  alarm_description   = "API Gateway recibe > 10% de errores 4XX"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 5
  threshold           = 10
  treat_missing_data  = "notBreaching"

  metric_query {
    id          = "error_rate"
    expression  = "(errors/requests)*100"
    label       = "4XX Error Rate %"
    return_data = true
  }

  metric_query {
    id = "errors"
    metric {
      namespace   = "AWS/ApiGateway"
      metric_name = "4XXError"
      stat        = "Sum"
      period      = 60
      dimensions = {
        ApiId = var.api_id
      }
    }
  }

  metric_query {
    id = "requests"
    metric {
      namespace   = "AWS/ApiGateway"
      metric_name = "Count"
      stat        = "Sum"
      period      = 60
      dimensions = {
        ApiId = var.api_id
      }
    }
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-apigw-4xx-alarm" }
}

# ALARMA 11 — NAT GATEWAY ERROR PORT ALLOCATION
# AZ-a — siempre existe (dev y prod)
resource "aws_cloudwatch_metric_alarm" "nat_error_az_a" {
  alarm_name          = "${var.prefix}-nat-error-az-a"
  alarm_description   = "NAT Gateway AZ-a no puede asignar puertos"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  threshold           = 0
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/NatGateway"
  metric_name = "ErrorPortAllocation"
  statistic   = "Sum"
  period      = 60

  dimensions = {
    NatGatewayId = var.nat_gateway_az_a_id
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-nat-error-az-a-alarm" }
}

# AZ-b — solo en prod (count = 0 en dev)
resource "aws_cloudwatch_metric_alarm" "nat_error_az_b" {
  count = var.has_nat_az_b ? 1 : 0

  alarm_name          = "${var.prefix}-nat-error-az-b"
  alarm_description   = "NAT Gateway AZ-b no puede asignar puertos"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  threshold           = 0
  treat_missing_data  = "notBreaching"

  namespace   = "AWS/NatGateway"
  metric_name = "ErrorPortAllocation"
  statistic   = "Sum"
  period      = 60

  dimensions = {
    NatGatewayId = var.nat_gateway_az_b_id
  }

  alarm_actions = [var.sns_alerts_arn]
  ok_actions    = [var.sns_alerts_arn]

  tags = { Name = "${var.prefix}-nat-error-az-b-alarm" }
}

# CLOUDWATCH DASHBOARD
resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "${var.prefix}-overview"

  dashboard_body = jsonencode({
    widgets = [

      # FILA 1: DISPONIBILIDAD 

      {
        type   = "metric"
        x      = 0
        y      = 0
        width  = 8
        height = 6
        properties = {
          title  = "ALB — Request Count"
          region = "us-east-2"
          metrics = [[
            "AWS/ApplicationELB", "RequestCount",
            "LoadBalancer", var.alb_arn_suffix,
            { stat = "Sum", period = 60 }
          ]]
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 8
        y      = 0
        width  = 8
        height = 6
        properties = {
          title  = "ALB — 5XX Errors"
          region = "us-east-2"
          metrics = [[
            "AWS/ApplicationELB", "HTTPCode_Target_5XX_Count",
            "LoadBalancer", var.alb_arn_suffix,
            "TargetGroup", var.target_group_arn_suffix,
            { stat = "Sum", period = 60, color = "#d62728" }
          ]]
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 16
        y      = 0
        width  = 8
        height = 6
        properties = {
          title  = "ECS — Tasks Running"
          region = "us-east-2"
          metrics = [
            ["AWS/ECS", "RunningTaskCount",
              "ClusterName", var.ecs_cluster_name,
              "ServiceName", var.ecs_service_name,
              { stat = "Average", period = 60, color = "#2ca02c" }
            ]
          ]
          view = "timeSeries"
        }
      },

      # FILA 2: RENDIMIENTO 

      {
        type   = "metric"
        x      = 0
        y      = 6
        width  = 6
        height = 6
        properties = {
          title  = "ECS — CPU Utilization"
          region = "us-east-2"
          metrics = [[
            "AWS/ECS", "CPUUtilization",
            "ClusterName", var.ecs_cluster_name,
            "ServiceName", var.ecs_service_name,
            { stat = "Average", period = 60 }
          ]]
          annotations = {
            horizontal = [{ value = 70, label = "Umbral alarma", color = "#ff7f0e" }]
          }
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 6
        y      = 6
        width  = 6
        height = 6
        properties = {
          title  = "ECS — Memory Utilization"
          region = "us-east-2"
          metrics = [[
            "AWS/ECS", "MemoryUtilization",
            "ClusterName", var.ecs_cluster_name,
            "ServiceName", var.ecs_service_name,
            { stat = "Average", period = 60 }
          ]]
          annotations = {
            horizontal = [{ value = 80, label = "Umbral alarma", color = "#ff7f0e" }]
          }
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 6
        width  = 6
        height = 6
        properties = {
          title  = "Lambda — Duration p99"
          region = "us-east-2"
          metrics = [[
            "AWS/Lambda", "Duration",
            "FunctionName", var.lambda_function_name,
            { stat = "p99", period = 60 }
          ]]
          annotations = {
            horizontal = [{ value = 25000, label = "Umbral alarma", color = "#ff7f0e" }]
          }
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 18
        y      = 6
        width  = 6
        height = 6
        properties = {
          title  = "API Gateway — Latency p99"
          region = "us-east-2"
          metrics = [[
            "AWS/ApiGateway", "IntegrationLatency",
            "ApiId", var.api_id,
            { stat = "p99", period = 60 }
          ]]
          view = "timeSeries"
        }
      },

      # FILA 3: BASE DE DATOS 

      {
        type   = "metric"
        x      = 0
        y      = 12
        width  = 6
        height = 6
        properties = {
          title  = "RDS — Connections"
          region = "us-east-2"
          metrics = [[
            "AWS/RDS", "DatabaseConnections",
            "DBInstanceIdentifier", var.rds_identifier,
            { stat = "Average", period = 60 }
          ]]
          annotations = {
            horizontal = [{ value = 80, label = "Umbral alarma", color = "#ff7f0e" }]
          }
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 6
        y      = 12
        width  = 6
        height = 6
        properties = {
          title  = "RDS — CPU"
          region = "us-east-2"
          metrics = [[
            "AWS/RDS", "CPUUtilization",
            "DBInstanceIdentifier", var.rds_identifier,
            { stat = "Average", period = 60 }
          ]]
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 12
        width  = 6
        height = 6
        properties = {
          title  = "RDS — Read Latency"
          region = "us-east-2"
          metrics = [[
            "AWS/RDS", "ReadLatency",
            "DBInstanceIdentifier", var.rds_identifier,
            { stat = "Average", period = 60 }
          ]]
          annotations = {
            horizontal = [{ value = 0.1, label = "100ms umbral", color = "#ff7f0e" }]
          }
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 18
        y      = 12
        width  = 6
        height = 6
        properties = {
          title  = "RDS — Free Storage (bytes)"
          region = "us-east-2"
          metrics = [[
            "AWS/RDS", "FreeStorageSpace",
            "DBInstanceIdentifier", var.rds_identifier,
            { stat = "Average", period = 300 }
          ]]
          annotations = {
            horizontal = [{ value = 5368709120, label = "5 GB mínimo", color = "#d62728" }]
          }
          view = "timeSeries"
        }
      },

      # FILA 4: MENSAJERÍA 

      {
        type   = "metric"
        x      = 0
        y      = 18
        width  = 6
        height = 6
        properties = {
          title  = "SQS — Messages Visible"
          region = "us-east-2"
          metrics = [[
            "AWS/SQS", "ApproximateNumberOfMessagesVisible",
            "QueueName", "${var.prefix}-docs-generation-queue",
            { stat = "Sum", period = 60 }
          ]]
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 6
        y      = 18
        width  = 6
        height = 6
        properties = {
          title  = "SQS DLQ — Messages (debe ser 0)"
          region = "us-east-2"
          metrics = [[
            "AWS/SQS", "ApproximateNumberOfMessagesVisible",
            "QueueName", var.sqs_dlq_name,
            { stat = "Sum", period = 60, color = "#d62728" }
          ]]
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 18
        width  = 12
        height = 6
        properties = {
          title  = "Lambda — Invocaciones vs Errores"
          region = "us-east-2"
          metrics = [
            ["AWS/Lambda", "Invocations",
              "FunctionName", var.lambda_function_name,
              { stat = "Sum", period = 60, color = "#2ca02c" }
            ],
            ["AWS/Lambda", "Errors",
              "FunctionName", var.lambda_function_name,
              { stat = "Sum", period = 60, color = "#d62728" }
            ]
          ]
          view = "timeSeries"
        }
      },

      # FILA 5: SEGURIDAD 

      {
        type   = "metric"
        x      = 0
        y      = 24
        width  = 12
        height = 6
        properties = {
          title  = "WAF — Blocked Requests"
          region = "us-east-1"
          metrics = [[
            "AWS/WAFV2", "BlockedRequests",
            "WebACL", "${var.prefix}-waf",
            "Region", "us-east-1",
            "Rule", "ALL",
            { stat = "Sum", period = 60, color = "#d62728" }
          ]]
          view = "timeSeries"
        }
      },
      {
        type   = "metric"
        x      = 12
        y      = 24
        width  = 12
        height = 6
        properties = {
          title  = "WAF — Allowed Requests"
          region = "us-east-1"
          metrics = [[
            "AWS/WAFV2", "AllowedRequests",
            "WebACL", "${var.prefix}-waf",
            "Region", "us-east-1",
            "Rule", "ALL",
            { stat = "Sum", period = 60, color = "#2ca02c" }
          ]]
          view = "timeSeries"
        }
      }

    ]
  })
}
