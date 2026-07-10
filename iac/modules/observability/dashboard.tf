resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = "${var.prefix}-overview"

  dashboard_body = jsonencode({
    widgets = [

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
