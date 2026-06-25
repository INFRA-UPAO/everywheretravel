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


module "compute" {
  source = "./modules/compute"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  private_app_subnet_ids = module.networking.private_app_subnet_ids
  sg_alb_id              = module.security_groups.sg_alb_id
  sg_ecs_task_id         = module.security_groups.sg_ecs_task_id
  s3_access_logs_bucket  = module.s3.s3_access_logs_bucket
  ecs_cpu                = local.ecs_cpu
  ecs_memory             = local.ecs_memory
  ecs_app_port           = var.ecs_app_port
  ecs_min_tasks          = local.ecs_min_tasks
  ecs_max_tasks          = local.ecs_max_tasks
  ecs_execution_role_arn = module.iam.ecs_execution_role_arn
  ecs_task_role_arn      = module.iam.ecs_task_role_arn
  ecr_repo_url           = module.ecr.ecr_repo_url
  ecr_image_tag          = "initial"
  sqs_queue_url          = module.sqs.sqs_queue_url
  s3_docs_bucket         = module.s3.s3_docs_bucket
  rds_secret_arn         = module.secrets.rds_secret_arn
  kms_logs_arn           = module.kms.kms_logs_arn

  depends_on = [module.iam]
}