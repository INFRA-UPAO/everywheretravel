# Everywheretravel

Proyecto de infraestructura como código usando Terraform + AWS.

---

## Primeros pasos

### ¿Eres el primero en configurar el proyecto? → Corre el bootstrap

El bootstrap crea el bucket S3 en AWS donde todos los devs compartirán el estado de Terraform. **Solo se hace una vez** (ya sea por el líder del equipo o quien levanta el proyecto por primera vez).

```bash
cd iac/bootstrap
terraform init
terraform apply
```

> Si el bucket ya existe en AWS, omite este paso por completo.

---

### ¿Ya existe el bucket? → Solo haz el init normal

Todos los demás devs simplemente se paran en `iac/` y corren:

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
