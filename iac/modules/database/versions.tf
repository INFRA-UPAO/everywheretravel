terraform {
  required_providers {
    aws = {
      source                = "hashicorp/aws"
      configuration_aliases = [aws]
    }
    random = {
      source = "hashicorp/random"
    }
  }
}
