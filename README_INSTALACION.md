# Guía de Instalación - JD Refrigeración (Backend)

Este documento detalla los pasos necesarios para levantar el proyecto en una computadora nueva tras haberlo clonado desde GitHub.

## 1. Requisitos Previos

- **Java JDK 21**
- **Maven** (O puedes usar `./mvnw.cmd` incluido en el proyecto)
- **Base de Datos Oracle** (Oracle Database 19c / 21c XE o similar)
- **IDE** sugerido: IntelliJ IDEA, Eclipse o VS Code.

## 2. Configuración de la Base de Datos Oracle

El proyecto utiliza Oracle. Necesitas crear el usuario/esquema `JDREFRIG_APP` y otorgarle los permisos necesarios antes de iniciar la aplicación. Abre tu consola SQL (SQL*Plus, SQL Developer o DBeaver) como administrador (`SYSDBA`) y ejecuta lo siguiente:

```sql
-- 1. Crear el usuario (Asegúrate de que la sesión permita usuarios sin prefijo C## si usas Oracle 19c/21c)
ALTER SESSION SET "_ORACLE_SCRIPT"=true;

CREATE USER JDREFRIG_APP IDENTIFIED BY 123456 DEFAULT TABLESPACE USERS TEMPORARY TABLESPACE TEMP;

-- 2. Otorgar permisos
GRANT CONNECT, RESOURCE, DBA TO JDREFRIG_APP;
GRANT CREATE SESSION TO JDREFRIG_APP;
GRANT CREATE TABLE TO JDREFRIG_APP;
GRANT CREATE VIEW TO JDREFRIG_APP;
GRANT CREATE SEQUENCE TO JDREFRIG_APP;
```

> [!IMPORTANT]
> **Creación de Tablas:** No es necesario ejecutar ningún script SQL para crear las tablas (`clientes`, `productos`, `compras`, `ventas`, etc.). El proyecto está configurado con `spring.jpa.hibernate.ddl-auto=update`, lo que significa que **Spring Boot y Hibernate generarán automáticamente todas las tablas y relaciones** la primera vez que arranques la aplicación.

## 3. Configuración de Variables de Entorno

El proyecto usa credenciales de correo (SMTP) para enviar notificaciones. Para no exponer contraseñas en GitHub, se ha configurado para usar la variable de entorno `MAIL_PASSWORD`.

Antes de ejecutar el proyecto, debes configurar esta variable en tu sistema:

**En Windows (PowerShell):**
```powershell
$env:MAIL_PASSWORD="tu_contraseña_de_aplicacion_gmail"
$env:API_DOCUMENTOS_TOKEN="tu_token_de_api_peru"
```

**En Windows (Símbolo de sistema - CMD):**
```cmd
set MAIL_PASSWORD=tu_contraseña_de_aplicacion_gmail
set API_DOCUMENTOS_TOKEN=tu_token_de_api_peru
```

*(Notas: Para Gmail usa una "Contraseña de Aplicación" de 16 caracteres. Para API Perú, puedes conseguir un token gratuito registrándote en su web).*

## 4. Pruebas Rápidas con Base de Datos en Memoria (H2)

Si estás en una computadora nueva y **no tienes Oracle instalado**, puedes levantar el proyecto temporalmente usando una base de datos en memoria (H2) para probar las funcionalidades sin configuración compleja.

Para esto, ve al archivo `jdrefrigeracion-backend/src/main/resources/application-dev.yml` y cambia temporalmente el bloque `datasource` y `jpa` por esto:

```yaml
  datasource:
    url: jdbc:h2:mem:jdrefrigdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
```
Además, asegúrate de tener la dependencia de H2 en el `pom.xml`:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 5. Compilación y Ejecución

Navega a la carpeta del backend (`jdrefrigeracion-backend`) y ejecuta:

```powershell
# 1. Limpiar y compilar el proyecto (descargará las dependencias de Maven)
.\mvnw.cmd clean compile

# 2. Ejecutar la aplicación
.\mvnw.cmd spring-boot:run
```

La aplicación se levantará en el puerto **8081**. Puedes probar las APIs visualmente abriendo tu navegador en:
👉 **http://localhost:8081/swagger-ui/index.html**

## 6. Notas sobre GitHub

Se ha creado un archivo `.gitignore` en la raíz del proyecto para evitar subir las carpetas `target/`, archivos `.class`, o scripts locales (`.ps1`). Simplemente ejecuta:

```bash
git add .
git commit -m "Arquitectura modular consolidada con Aron y Wagner"
git branch -M main
git remote add origin <URL_DE_TU_REPOSITORIO>
git push -u origin main
```
