# Recurso único por cuenta AWS: solo se crea en el workspace donde
# var.create_provider = true (ver iac/main.tf). El resto de workspaces
# referencian el mismo provider por su ARN determinístico (local.oidc_provider_arn).
resource "aws_iam_openid_connect_provider" "github" {
  count = var.create_provider ? 1 : 0

  url             = "https://token.actions.githubusercontent.com"
  client_id_list  = ["sts.amazonaws.com"]
  thumbprint_list = [data.tls_certificate.github_actions.certificates[0].sha1_fingerprint]

  tags = {
    Name = "github-actions-oidc"
  }
}
