resource "null_resource" "prod_protection" {
  count = local.is_prod ? 1 : 0

  triggers = {
    workspace = terraform.workspace
  }

  provisioner "local-exec" {
    command = "echo 'APLICANDO EN PRODUCCIÓN — workspace: ${terraform.workspace}'"
  }
}

module "kms" {
  source    = "./modules/kms"
  providers = { aws = aws.main }

  prefix = local.prefix
  env    = local.env
}

module "sns" {
  source    = "./modules/sns"
  providers = { aws = aws.main }

  prefix      = local.prefix
  alert_email = var.alert_email
}

module "iam" {
  source    = "./modules/iam"
  providers = { aws = aws.main }

  prefix          = local.prefix
  kms_s3_docs_arn = module.kms.kms_s3_docs_arn
  kms_sqs_arn     = module.kms.kms_sqs_arn
  kms_secrets_arn = module.kms.kms_secrets_arn
  kms_logs_arn    = module.kms.kms_logs_arn
  kms_ecr_arn     = module.kms.kms_ecr_arn
  kms_backups_arn = module.kms.kms_backups_arn
}

module "security_groups" {
  source = "./modules/security-groups"

  providers = {
    aws = aws.main
  }

  prefix   = local.prefix
  vpc_id   = module.networking.vpc_id
  vpc_cidr = var.vpc_cidr
}