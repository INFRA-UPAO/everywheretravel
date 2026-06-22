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