resource "null_resource" "prod_protection" {
  count = local.is_prod ? 1 : 0

  triggers = {
    workspace = terraform.workspace
  }

  provisioner "local-exec" {
    command = "echo 'APLICANDO EN PRODUCCION -- workspace: ${terraform.workspace}'"
  }
}

resource "aws_route53_zone" "main" {
  provider = aws.main
  name     = "everywheretravel.online"

  tags = {
    Name = "everywheretravel.online"
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
  kms_logs_arn      = module.kms.kms_logs_arn
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

module "vpc_endpoints" {
  source = "./modules/vpc-endpoints"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  vpc_id                 = module.networking.vpc_id
  private_app_subnet_ids = module.networking.private_app_subnet_ids
  rt_private_app_az_a_id = module.networking.rt_private_app_az_a_id
  rt_private_app_az_b_id = module.networking.rt_private_app_az_b_id
  sg_vpce_sqs_id         = module.security_groups.sg_vpce_sqs_id
  sg_vpce_sm_id          = module.security_groups.sg_vpce_sm_id
  sg_vpce_logs_id        = module.security_groups.sg_vpce_logs_id
  sg_vpce_ecr_id         = module.security_groups.sg_vpce_ecr_id
  s3_docs_bucket_arn     = module.s3.s3_docs_bucket_arn
  ecs_task_role_arn      = module.iam.ecs_task_role_arn
  ecs_execution_role_arn = module.iam.ecs_execution_role_arn
  lambda_docgen_role_arn = module.iam.lambda_docgen_role_arn
  rds_secret_arn         = module.secrets.rds_secret_arn
  sqs_queue_arn          = module.sqs.sqs_queue_arn
  sqs_dlq_arn            = module.sqs.sqs_dlq_arn
}

module "sqs" {
  source = "./modules/sqs"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  kms_sqs_arn            = module.kms.kms_sqs_arn
  ecs_task_role_arn      = module.iam.ecs_task_role_arn
  lambda_docgen_role_arn = module.iam.lambda_docgen_role_arn
  sns_alerts_arn         = module.sns.sns_alerts_arn
}

module "compute" {
  source = "./modules/compute"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  vpc_id                 = module.networking.vpc_id
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

module "auth" {
  source = "./modules/auth"

  providers = {
    aws = aws.main
  }

  prefix      = local.prefix
  domain_name = var.domain_name
  env         = local.env
}

module "api_gateway" {
  source = "./modules/api-gateway"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  domain_name            = var.domain_name
  cognito_issuer_url     = module.auth.cognito_issuer_url
  cognito_app_client_id  = module.auth.cognito_app_client_id
  alb_listener_arn       = module.compute.alb_listener_arn
  private_app_subnet_ids = module.networking.private_app_subnet_ids
  sg_vpclink_id          = module.security_groups.sg_vpclink_id
  kms_logs_arn           = module.kms.kms_logs_arn
}

module "edge" {
  source = "./modules/edge"

  providers = {
    aws      = aws.main
    aws.edge = aws.edge
  }

  prefix                 = local.prefix
  domain_name            = var.domain_name
  s3_frontend_bucket_id  = module.s3.s3_frontend_bucket_id
  s3_frontend_bucket_arn = module.s3.s3_frontend_bucket_arn
  s3_access_logs_bucket  = module.s3.s3_access_logs_bucket
  s3_waf_logs_bucket_arn = module.s3.s3_waf_logs_bucket_arn
  api_endpoint           = module.api_gateway.api_endpoint
  lambda_edge_role_arn   = module.iam.lambda_edge_role_arn
  route53_zone_id        = aws_route53_zone.main.zone_id
}

module "route53" {
  source = "./modules/route53"

  providers = {
    aws = aws.main
  }

  prefix                    = local.prefix
  domain_name               = var.domain_name
  is_prod                   = local.is_prod
  route53_zone_id           = aws_route53_zone.main.zone_id
  cloudfront_domain_name    = module.edge.cloudfront_domain_name
  cloudfront_hosted_zone_id = module.edge.cloudfront_hosted_zone_id
  zoho_verification_token   = var.zoho_verification_token
  zoho_dkim_cname_value     = var.zoho_dkim_cname_value
}

module "backup" {
  source = "./modules/backup"

  providers = {
    aws      = aws.main
    aws.edge = aws.edge
  }

  prefix             = local.prefix
  is_prod            = local.is_prod
  kms_backups_arn    = module.kms.kms_backups_arn
  backup_role_arn    = module.iam.backup_role_arn
  rds_arn            = module.database.rds_arn
  s3_docs_bucket_arn = module.s3.s3_docs_bucket_arn
  sns_backup_arn     = module.sns.sns_backup_arn
}

module "lambda" {
  source = "./modules/lambda"

  providers = {
    aws = aws.main
  }

  prefix                 = local.prefix
  lambda_memory          = var.lambda_memory
  lambda_timeout         = var.lambda_timeout
  private_app_subnet_ids = module.networking.private_app_subnet_ids
  sg_lambda_id           = module.security_groups.sg_lambda_id
  lambda_docgen_role_arn = module.iam.lambda_docgen_role_arn
  sqs_queue_arn          = module.sqs.sqs_queue_arn
  sqs_queue_url          = module.sqs.sqs_queue_url
  s3_docs_bucket         = module.s3.s3_docs_bucket
  rds_secret_arn         = module.secrets.rds_secret_arn
  kms_logs_arn           = module.kms.kms_logs_arn
}

module "observability" {
  source = "./modules/observability"

  providers = {
    aws = aws.main
  }

  prefix                  = local.prefix
  sns_alerts_arn          = module.sns.sns_alerts_arn
  sqs_dlq_name            = module.sqs.sqs_dlq_name
  alb_arn_suffix          = module.compute.alb_arn_suffix
  target_group_arn_suffix = module.compute.target_group_arn_suffix
  rds_identifier          = module.database.rds_identifier
  ecs_cluster_name        = module.compute.ecs_cluster_name
  ecs_service_name        = module.compute.ecs_service_name
  lambda_function_name    = module.lambda.lambda_function_name
  nat_gateway_az_a_id     = module.networking.nat_gateway_az_a_id
  nat_gateway_az_b_id     = module.networking.nat_gateway_az_b_id
  has_nat_az_b            = local.nat_gateway_count > 1
  api_id                  = module.api_gateway.api_id
}
