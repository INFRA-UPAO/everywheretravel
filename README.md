# Everywheretravel

Proyecto de infraestructura como código usando Terraform + AWS.

---

## Requisitos previos

- [Terraform CLI](https://developer.hashicorp.com/terraform/downloads)
- [AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)
- Acceso SSO a la cuenta AWS del equipo

---

## Configuracion de AWS

Cada dev configura su propio profile de SSO (el nombre es libre, cada uno elige el suyo):

```bash
aws configure sso --profile mi-profile --use-device-code
```

Te pedira la start URL y la region del SSO del equipo.

Para

```bash
aws sso login --profile mi-profile --use-device-code
```

Antes de usar Terraform, setea la variable de entorno:

```powershell
# PowerShell
$env:AWS_PROFILE = "mi-profile"
```

```bash
# Bash / Linux / Mac
export AWS_PROFILE=mi-profile
```

> **Nota:** No hay credenciales hardcodeadas en el codigo. Cada dev usa su propio profile apuntando a la misma cuenta AWS del equipo.

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
terraform workspace new nombre-del-workspace
```

> **IMPORTANTE:** Si haces `terraform apply` sin verificar el workspace, podrias aplicar cambios en el entorno equivocado (por ejemplo, modificar produccion cuando querias tocar dev). Siempre corre `terraform workspace show` antes de cualquier operacion destructiva.

### Archivos de variables por entorno (.tfvars)

Los archivos `.tfvars` contienen las variables especificas de cada entorno (region, dominio, nombre del proyecto, etc.) y **no se suben al repositorio** por seguridad (estan en `.gitignore`).

Para obtener el contenido, pidelo al lider del equipo. Luego crea los archivos manualmente en la carpeta `iac/tfvars/`:

```
iac/tfvars/
├── dev.tfvars
├── prod.tfvars
```

### Ejecutar plan o apply con variables por entorno

Siempre especifica el archivo `.tfvars` correspondiente al workspace en el que estas:

```bash
terraform plan -var-file="tfvars/dev.tfvars"
terraform apply -var-file="tfvars/dev.tfvars"
```

---

## Estructura del proyecto

```
iac/
├── bootstrap/   # Se corre una sola vez para crear el backend remoto
├── modules/     # Módulos reutilizables de infraestructura
├── backend.tf   # Apunta al bucket S3 creado por bootstrap
└── ...
```
