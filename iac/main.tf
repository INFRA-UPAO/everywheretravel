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
