locals {
  is_prod = terraform.workspace == "prod"
  env     = terraform.workspace
  prefix  = "${var.project_name}-${terraform.workspace}"

  config = {
    dev = {
      db_instance_class = "db.t3.micro"
      db_multi_az       = false
      ecs_cpu           = 256
      ecs_memory        = 512
      ecs_min_tasks     = 1
      ecs_max_tasks     = 2
      nat_gateway_count = 1
    }
    prod = {
      db_instance_class = "db.t3.small"
      db_multi_az       = true
      ecs_cpu           = 1024
      ecs_memory        = 2048
      ecs_min_tasks     = 2
      ecs_max_tasks     = 10
      nat_gateway_count = 2
    }
  }

  config_actual     = local.config[contains(keys(local.config), local.env) ? local.env : "dev"]
  db_instance_class = local.config_actual.db_instance_class
  db_multi_az       = local.config_actual.db_multi_az
  ecs_cpu           = local.config_actual.ecs_cpu
  ecs_memory        = local.config_actual.ecs_memory
  ecs_min_tasks     = local.config_actual.ecs_min_tasks
  ecs_max_tasks     = local.config_actual.ecs_max_tasks
  nat_gateway_count = local.config_actual.nat_gateway_count
}
