# Everywheretravel

Proyecto de infraestructura como código usando Terraform + AWS.

---

## Requisitos previos

- [Terraform CLI](https://developer.hashicorp.com/terraform/downloads)
- [AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Node.js + npm](https://nodejs.org/)
- Java 21
- Python 3
- Ansible
- Acceso a la cuenta AWS del equipo mediante SSO

---

## Configuracion de AWS

El proyecto no usa credenciales hardcodeadas. Cada integrante configura un profile local de AWS CLI apuntando a la cuenta AWS del equipo.

### AWS SSO / IAM Identity Center

Usar SSO y agregar un nuevo usuario, configurar un profile SSO:

```bash
aws configure sso --profile leturia --use-device-code
```

Te pedira la start URL y la region del SSO del equipo.

Luego inicia sesion:

```bash
aws sso login --profile leturia --use-device-code
```

Antes de usar Terraform, setea la variable de entorno:

```powershell
# PowerShell
$env:AWS_PROFILE = "leturia"
```

```bash
# Bash / Linux / Mac
export AWS_PROFILE=leturia
```

> **Nota:** Para una prueba, el camino mas simple suele ser el usuario IAM temporal `leturia`. SSO es preferible si el equipo ya tiene IAM Identity Center configurado y puede invitarlo.

---

## Primeros pasos

### ¿Eres el primero en configurar el proyecto? → Corre el bootstrap

El bootstrap crea el bucket S3 en AWS donde todos los devs compartiran el estado de Terraform. **Solo se hace una vez** (ya sea por el lider del equipo o quien levanta el proyecto por primera vez).

```bash
cd iac/bootstrap
terraform init
terraform apply
```

> Si el bucket ya existe en AWS, omite este paso por completo.

---

### ¿Ya existe el bucket? → Solo haz el init normal

Todos los demas devs simplemente se paran en `iac/` y corren:

```bash
cd iac
terraform init
```

Eso descarga el estado compartido desde S3 y ya pueden trabajar sincronizados con el resto del equipo.

---

## Workspaces de Terraform

Este proyecto usa **Terraform workspaces** para separar los entornos (dev, staging, prod, etc.). Cada workspace mantiene su propio archivo de estado, asi que los cambios en un entorno no afectan a los demas.

### Verificar en que workspace estas

**Antes de correr cualquier comando** (`plan`, `apply`, `destroy`, etc.), verifica siempre en que workspace te encuentras:

```bash
terraform workspace show
```

### Listar workspaces disponibles

```bash
terraform workspace list
```

El workspace activo aparece marcado con un `*`.

### Cambiar de workspace

```bash
terraform workspace select dev
```

### Crear un nuevo workspace

```bash
terraform workspace new nombre-del-workspace (dev o prod)
```

> **IMPORTANTE:** Si se hace `terraform apply` sin verificar el workspace, podrias aplicar cambios en el entorno equivocado (por ejemplo, modificar produccion cuando querias tocar dev). Siempre corre `terraform workspace show` antes de cualquier operacion destructiva.

### Archivos de variables por entorno (.tfvars)

Los archivos `.tfvars` contienen las variables especificas de cada entorno (region, dominio, nombre del proyecto, etc.) y **no se suben al repositorio** por seguridad (estan en `.gitignore`).

Se subiran los tfvars en el comentario de la tarea en canvas. Luego crea los archivos manualmente en la carpeta `iac/tfvars/`:

```
iac/tfvars/
├── dev.tfvars
├── prod.tfvars
```

### Ejecutar plan o apply con variables por entorno

Siempre especifica el archivo `.tfvars` correspondiente al workspace en el que estas:

```bash
terraform plan -var-file="tfvars/prod.tfvars"
terraform apply -var-file="tfvars/prod.tfvars"
```

---

## Uso de Ansible en Windows

Si estas en Windows, ejecuta Ansible desde **WSL con Ubuntu**. Ansible no se recomienda como control node nativo en Windows.

Instala WSL desde PowerShell:

```powershell
wsl --install -d Ubuntu
```

Despues de reiniciar si Windows lo pide, abre Ubuntu y entra al proyecto montado desde Windows:

```bash
cd "/mnt/c/ruta-al-proyecto/proyecto-iac"
```

Instala Python, pipx, Ansible y utilidades basicas dentro de WSL:

```bash
sudo apt update
sudo apt install -y python3 python3-full python3-venv unzip
sudo apt install -y pipx
command -v pipx
pipx ensurepath || /usr/bin/pipx ensurepath
source ~/.bashrc
pipx install ansible || /usr/bin/pipx install ansible
ansible --version
```

> No uses `python3 -m pip install --user ansible` en Ubuntu reciente. Puede fallar con `externally-managed-environment` por PEP 668. `pipx` instala Ansible en un entorno aislado y evita romper Python del sistema.
> Si `sudo apt install -y pipx` dice que no encuentra el paquete, ejecuta `sudo apt update` otra vez y vuelve a instalarlo.

Tambien instala y configura AWS CLI dentro de WSL, porque los playbooks ejecutan comandos `aws`:

```bash
aws configure sso --profile leturia --use-device-code
aws sso login --profile leturia --use-device-code
export AWS_PROFILE=leturia
aws sts get-caller-identity
```

Si vas a desplegar el backend, Docker Desktop debe estar abierto y con integracion WSL activa para Ubuntu:

```text
Docker Desktop > Settings > Resources > WSL Integration > Ubuntu
```

Los comandos de Terraform pueden ejecutarse desde PowerShell o WSL. Los comandos de Ansible se ejecutan desde WSL.

---

## Prueba completa para docente

Esta guia permite probar el proyecto completo sin pipelines:

- Terraform provisiona la infraestructura en AWS.
- Ansible configura y despliega backend, frontend y Lambda.
- El login inicia en la pagina del sistema (`/auth/login`) y autentica con AWS Cognito.
- La Lambda `lambda-doc-generator` genera documentos y los guarda en S3.

> Antes de empezar, confirma que Docker Desktop este abierto y que el archivo `iac/tfvars/dev.tfvars` exista.

### 1. Configurar acceso AWS

Con SSO:

```powershell
aws configure sso --profile leturia --use-device-code
aws sso login --profile leturia --use-device-code
$env:AWS_PROFILE="leturia"
aws sts get-caller-identity
```

El comando `aws sts get-caller-identity` debe mostrar la cuenta AWS del equipo.

### 2. Provisionar infraestructura con Terraform

```powershell
cd iac
terraform init
terraform workspace select dev
terraform validate
terraform apply -var-file="tfvars/dev.tfvars"
terraform output -json > ../ansible/terraform-output.json
cd ..
```

Si el workspace `dev` no existe, crealo una sola vez:

```powershell
cd iac
terraform workspace new dev
cd ..
```

### 3. Instalar dependencias de Ansible

> Si estas en Windows, ejecuta este comando desde WSL.

```bash
ansible-galaxy collection install -r ansible/requirements.yml
```

### 4. Desplegar backend en ECS

Este paso construye la imagen Docker del backend, la sube a ECR y actualiza el servicio ECS.

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/deploy_backend_ecs.yml -e env=dev -e image_tag=demo-v1
```

### 5. Desplegar Lambda doc generator

Este paso instala dependencias de produccion, empaqueta la Lambda y actualiza la funcion en AWS.

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/deploy_lambda_doc_generator.yml -e env=dev
```

### 6. Desplegar frontend Angular

Este paso genera `environment.prod.ts` con los valores reales de Cognito, compila Angular, sube el SPA al bucket S3 del frontend e invalida CloudFront.

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/deploy_frontend.yml -e env=dev
```

### 7. Crear usuario demo en Cognito

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/create_cognito_demo_user.yml -e demo_email=docente@example.com -e demo_password='Demo12345!'
```

Usa ese correo y password para iniciar sesion desde la pagina `/auth/login`.

### 8. Probar Lambda doc generator

Este paso invoca la Lambda con un evento de prueba similar al que llegaria desde SQS.

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/test_lambda_doc_generator.yml -e env=dev
```

Luego revisa el bucket de documentos en S3, dentro de:

```text
generated/recibo/
```

### 9. Abrir la aplicacion

Puedes obtener las URLs con:

```powershell
cd iac
terraform output cloudfront_domain_name
terraform output domain_name
cd ..
```

Abre el dominio configurado o el dominio de CloudFront. El flujo esperado es:

1. Entrar a `/auth/login`.
2. Presionar el boton de inicio de sesion.
3. Autenticarse en AWS Cognito.
4. Volver por `/callback`.
5. Entrar al dashboard.

---

## Despliegue manual con Terraform + Ansible

El orden correcto para levantar el proyecto completo es:

1. Configurar acceso AWS con usuario IAM temporal o SSO.
2. `terraform init`.
3. `terraform workspace select dev` o `terraform workspace new dev`.
4. `terraform validate`.
5. `terraform apply -var-file="tfvars/dev.tfvars"`.
6. `terraform output -json > ../ansible/terraform-output.json`.
7. `ansible-galaxy collection install -r ansible/requirements.yml`.
8. Ejecutar `deploy_backend_ecs.yml`.
9. Ejecutar `deploy_lambda_doc_generator.yml`.
10. Ejecutar `deploy_frontend.yml`.
11. Ejecutar `create_cognito_demo_user.yml`.
12. Probar app desde CloudFront o dominio.
13. Ejecutar `test_lambda_doc_generator.yml`.

---

## Validaciones finales

### Terraform

```powershell
cd iac
terraform validate
terraform output
cd ..
```

### Backend ECS

```powershell
aws ecs describe-services `
  --cluster $(cd iac; terraform output -raw ecs_cluster_name) `
  --services $(cd iac; terraform output -raw ecs_service_name)
```

El servicio debe aparecer estable y con tareas en ejecucion.

### Frontend

```powershell
cd iac
terraform output cloudfront_domain_name
terraform output domain_name
cd ..
```

La aplicacion debe cargar desde CloudFront o desde el dominio configurado.

### Cognito

La pagina `/auth/login` debe redirigir a Cognito y luego volver a `/callback`.

### Lambda

Despues de ejecutar `test_lambda_doc_generator.yml`, debe existir un PDF en el bucket S3 de documentos bajo `generated/recibo/`.

---

## Estructura del proyecto

```
iac/
├── bootstrap/   # Se corre una sola vez para crear el backend remoto
├── modules/     # Módulos reutilizables de infraestructura
├── backend.tf   # Apunta al bucket S3 creado por bootstrap
└── ...
```

```
ansible/
├── inventory/    # Inventario local para ejecutar playbooks desde la maquina del docente
├── playbooks/    # Despliegue de backend, frontend, Lambda y usuario demo
└── scripts/      # Utilidades usadas por los playbooks
```
