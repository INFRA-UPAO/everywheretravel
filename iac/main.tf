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
module "networking" {
  source = "./modules/networking"

  providers = {
    aws = aws.main
  }

  prefix            = local.prefix
  vpc_cidr          = var.vpc_cidr
  nat_gateway_count = local.nat_gateway_count
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
}