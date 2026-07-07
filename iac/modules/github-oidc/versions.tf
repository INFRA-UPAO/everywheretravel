terraform {
  required_providers {
    aws = {
      source                = "hashicorp/aws"
      configuration_aliases = [aws]
    }
    tls = {
      source = "hashicorp/tls"
    }
  }
}
