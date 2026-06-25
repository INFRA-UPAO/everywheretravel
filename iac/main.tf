module "s3" {
  source = "./modules/s3"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  kms_s3_frontend_arn    = module.kms.kms_s3_frontend_arn
  kms_s3_frontend_id     = module.kms.kms_s3_frontend_id
  kms_s3_docs_arn        = module.kms.kms_s3_docs_arn
  kms_s3_docs_id         = module.kms.kms_s3_docs_id
  kms_logs_arn           = module.kms.kms_logs_arn
  kms_logs_id            = module.kms.kms_logs_id
  ecs_task_role_arn      = module.iam.ecs_task_role_arn
  lambda_docgen_role_arn = module.iam.lambda_docgen_role_arn
  backup_role_arn        = module.iam.backup_role_arn
}

module "backup" {
  source = "./modules/backup"

  providers = {
    aws      = aws.main
    aws.edge = aws.edge
  }

  prefix               = local.prefix
  is_prod              = local.is_prod
  kms_backups_arn      = module.kms.kms_backups_arn
  kms_backups_edge_arn = module.kms.kms_backups_edge_arn
  backup_role_arn      = module.iam.backup_role_arn
  rds_arn              = module.database.rds_arn
  s3_docs_bucket_arn   = module.s3.s3_docs_bucket_arn
  sns_backup_arn       = module.sns.sns_backup_arn

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
    
module "ecr" {
  source = "./modules/ecr"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  kms_ecr_arn            = module.kms.kms_ecr_arn
  ecs_execution_role_arn = module.iam.ecs_execution_role_arn
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

module "networking" {
  source = "./modules/networking"

  providers = {
    aws = aws.main
  }

  prefix            = local.prefix
  vpc_cidr          = var.vpc_cidr
  nat_gateway_count = local.nat_gateway_count
}

module "auth" {
  source = "./modules/auth"

  providers = {
    aws = aws.main
  }

  prefix      = local.prefix
  domain_name = var.domain_name
  env         = local.env
}
  
module "database" {
  source    = "./modules/database"
  providers = { aws = aws.main }

  prefix                  = local.prefix
  db_name                 = var.db_name
  db_username             = var.db_username
  db_instance_class       = local.db_instance_class
  db_multi_az             = local.db_multi_az
  kms_rds_arn             = module.kms.kms_rds_arn
  private_data_subnet_ids = module.networking.private_data_subnet_ids
  sg_rds_id               = module.security_groups.sg_rds_id
}

module "secrets" {
  source    = "./modules/secrets"
  providers = { aws = aws.main }

  prefix                 = local.prefix
  kms_secrets_arn        = module.kms.kms_secrets_arn
  db_username            = var.db_username
  db_name                = var.db_name
  db_password            = module.database.rds_password
  db_host                = module.database.rds_address
  db_port                = module.database.rds_port
  ecs_execution_role_arn = module.iam.ecs_execution_role_arn
  ecs_task_role_arn      = module.iam.ecs_task_role_arn
  lambda_docgen_role_arn = module.iam.lambda_docgen_role_arn
}