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

## Estructura del proyecto

```
iac/
├── bootstrap/   # Se corre una sola vez para crear el backend remoto
├── modules/     # Módulos reutilizables de infraestructura
├── backend.tf   # Apunta al bucket S3 creado por bootstrap
└── ...
```
