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

## Configuracion de AWS (una sola vez por maquina)

El proyecto no usa credenciales hardcodeadas. Cada integrante configura un profile local de AWS CLI apuntando a la cuenta AWS del equipo.

```bash
aws configure sso --profile tu-profile --use-device-code
```

Te pedira la start URL y la region del SSO del equipo. Luego inicia sesion:

```bash
aws sso login --profile tu-profile --use-device-code
```

Antes de usar Terraform o Ansible, setea la variable de entorno en cada terminal nueva:

```powershell
# PowerShell
$env:AWS_PROFILE = "tu-profile"
```

```bash
# Bash / WSL / Linux / Mac
export AWS_PROFILE=tu-profile
```

Confirma que las credenciales estan activas:

```bash
aws sts get-caller-identity
```

> **Nota:** Si trabajas con Windows + WSL (ver seccion siguiente), esto hay que
> configurarlo **una vez en PowerShell y otra vez dentro de WSL** — son dos
> entornos separados, cada uno con su propio `~/.aws`. Para no repetir el
> login, se puede symlinkear la config de Windows dentro de WSL:
> `ln -s /mnt/c/Users/<tu-usuario>/.aws ~/.aws`.

---

## Ansible en Windows (WSL)

Si estas en Windows, ejecuta Ansible desde **WSL con Ubuntu** (no se recomienda como control node nativo en Windows). Se usan dos terminales:

- **PowerShell:** Terraform y generacion de outputs.
- **WSL / Ubuntu:** Ansible, AWS CLI para los playbooks y Docker.

Instala WSL desde PowerShell y entra al proyecto montado desde Windows:

```powershell
wsl --install -d Ubuntu
```

```bash
cd "/mnt/c/ruta-al-proyecto/proyecto-iac"
```

Instala Python, pipx, Ansible y utilidades basicas dentro de WSL:

```bash
sudo apt update
sudo apt install -y python3 python3-full python3-venv pipx unzip
pipx ensurepath
pipx install ansible
source ~/.bashrc
ansible --version
```

Instalar AWS CLI dentro de WSL con el instalador oficial

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o awscliv2.zip
unzip awscliv2.zip
sudo ./aws/install
rm -rf awscliv2.zip aws
aws --version
```

Configura las credenciales AWS dentro de WSL (ver seccion anterior) e instala las colecciones de Ansible:

```bash
ansible-galaxy collection install -r ansible/requirements.yml
```

Si vas a desplegar el backend, Docker Desktop debe estar abierto y con integracion WSL activa:

```text
Docker Desktop > Settings > Resources > WSL Integration > Ubuntu
```

Verifica que Docker responda desde WSL:

```bash
docker ps
```

---

## Terraform: primeros pasos

### ¿Eres el primero en configurar el proyecto? → Corre el bootstrap

El bootstrap crea el bucket S3 en AWS donde todos los devs compartiran el estado de Terraform. **Solo se hace una vez** (ya sea por el lider del equipo o quien levanta el proyecto por primera vez). Si el bucket ya existe en AWS, omite este paso por completo.

```bash
cd iac/bootstrap
terraform init
terraform apply
```

### Init normal (si el bucket ya existe)

```bash
cd iac
terraform init
```

### Workspaces

Este proyecto usa **Terraform workspaces** para separar los entornos (dev, prod). Cada workspace mantiene su propio archivo de estado.

```bash
terraform workspace show          # en que workspace estas (revisa SIEMPRE antes de plan/apply/destroy)
terraform workspace list          # workspaces disponibles (el activo tiene un *)
terraform workspace select prod   # cambiar de workspace
terraform workspace new prod      # crear uno nuevo
```

> **IMPORTANTE:** aplicar sin verificar el workspace puede modificar el entorno equivocado (por ejemplo, tocar produccion en vez de dev).

### Archivos de variables por entorno (.tfvars)

Los archivos `.tfvars` contienen las variables especificas de cada entorno (region, dominio, nombre del proyecto, etc.) y **no se suben al repositorio** (estan en `.gitignore`). Se comparten por otro medio (ver el comentario de la tarea en Canvas) y se colocan a mano en:

```
iac/tfvars/
├── dev.tfvars
├── prod.tfvars
```

Siempre especifica el archivo correspondiente al workspace activo:

```bash
terraform plan  -var-file="tfvars/prod.tfvars"
terraform apply -var-file="tfvars/prod.tfvars"
```

---

## Guia de despliegue completo

Levanta el proyecto entero (infraestructura + backend + frontend + Lambda) sin depender del pipeline de CI/CD. Sirve tanto para una entrega/demo como para probar cambios en `dev`.

- Terraform provisiona la infraestructura en AWS.
- Ansible construye y despliega backend, frontend y Lambda.
- El login inicia en `/auth/login` y autentica con AWS Cognito.
- La Lambda `lambda-doc-generator` genera documentos y los guarda en S3.

Elegi el entorno antes de empezar:

| Entorno    | Workspace Terraform | Archivo tfvars           | Variable Ansible |
| ---------- | ------------------- | ------------------------ | ---------------- |
| Produccion | `prod`              | `iac/tfvars/prod.tfvars` | `env=prod`       |
| Desarrollo | `dev`               | `iac/tfvars/dev.tfvars`  | `env=dev`        |

Los comandos siguientes usan `prod` — para `dev` cambia `prod` por `dev` en todos lados. Antes de empezar, confirma que Docker Desktop este abierto, que ya tengas configurado el acceso AWS (seccion "Configuracion de AWS") y, si estas en Windows, Ansible en WSL (seccion anterior).

### 1. Provisionar infraestructura con Terraform

Desde **PowerShell** (Windows) o la misma terminal (Linux/macOS):

```powershell
cd iac
terraform init
terraform workspace select prod   # o "terraform workspace new prod" si no existe
terraform validate
terraform apply -var-file="tfvars/prod.tfvars"
terraform output -json > ../ansible/terraform-output.json
cd ..
```

El archivo `ansible/terraform-output.json` se genera **despues** de que el `apply` termine correctamente — los playbooks no funcionan sin el.

### 2. Desplegar backend en ECS

Construye la imagen Docker del backend, la sube a ECR y actualiza el servicio ECS. El repositorio ECR tiene tags **inmutables**: usa un `image_tag` que no hayas usado antes.

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/deploy_backend_ecs.yml -e env=prod -e image_tag=demo-v1
```

### 3. Desplegar Lambda doc generator

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/deploy_lambda_doc_generator.yml -e env=prod
```

### 4. Desplegar frontend Angular

Genera `environment.prod.ts` con los valores reales de Cognito, compila Angular, sube el SPA al bucket S3 del frontend e invalida CloudFront.

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/deploy_frontend.yml -e env=prod
```

> El frontend y el backend se despliegan por separado — cambiar uno no actualiza el otro. Si tocaste código del frontend, hay que correr este paso para verlo reflejado.

### 5. Crear el primer usuario en Cognito

Paso manual, una sola vez por ambiente nuevo (reemplaza a un workflow/playbook automatico que se uso antes; se hace a mano para no depender de un rol IAM amplio de CI/CD solo para esto):

**AWS Console → Cognito → User pools → `everywhere-travel-<env>-user-pool` → Users → Create user**, con email `admin@everywheretravel.online` (tiene que coincidir exacto con la fila semilla que crea Flyway en `usuarios`, ver `V2__seed_initial_data.sql`). Dejá que Cognito mande la invitación por email (no marques "Set a password").

Los usuarios siguientes se crean autenticado como ese usuario contra `POST /api/v1/users` — no hace falta volver a la consola.

### 6. Probar Lambda doc generator

```bash
ansible-playbook -i ansible/inventory/local.yml ansible/playbooks/test_lambda_doc_generator.yml -e env=prod
```

Luego revisa el bucket de documentos en S3, dentro de `generated/recibo/`.

### 7. Abrir la aplicacion

```powershell
cd iac
terraform output cloudfront_domain_name
terraform output domain_name
cd ..
```

Abre el dominio configurado o el de CloudFront. Flujo esperado: `/auth/login` → boton de inicio de sesion → autenticarse en Cognito → volver por `/callback` → dashboard.

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

La aplicacion debe cargar desde CloudFront o desde el dominio configurado (ver outputs `cloudfront_domain_name` / `domain_name`).

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
├── playbooks/    # Despliegue de backend, frontend y Lambda
└── scripts/      # Utilidades usadas por los playbooks
```

