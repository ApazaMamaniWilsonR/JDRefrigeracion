# JD Refrigeración S.A.C.

Sistema de Gestión ERP (monolito modular) desarrollado con **Spring Boot + Spring Modulith** (backend) y **Angular 22** (frontend).

---

## 🛠️ Requisitos Previos

Para levantar este proyecto necesitarás tener instalado:

1. **Java 21** (JDK 21)
2. **Oracle Database** (Ej: Oracle Database 23c Free o 21c Express Edition)
3. **Node.js LTS** (incluye npm)
4. **Angular CLI 22** (`npm install -g @angular/cli@22`)
5. **IDE de desarrollo** (IntelliJ IDEA, Eclipse, o VS Code)

---

## ⚙️ Paso 1: Configurar la Base de Datos Oracle

No necesitas crear las tablas manualmente. Spring Boot (mediante Hibernate) las creará automáticamente cuando inicies el proyecto. Solo necesitas crear el usuario de la base de datos.

1. Abre **SQL Developer** (o SQL Plus).
2. Conéctate como usuario administrador (`sys` as sysdba o `system`).
3. Abre y ejecuta todo el contenido del archivo `db_setup.sql` que está en la raíz del proyecto.
   *Este script creará el usuario `JDREFRIG_APP` y le dará los permisos necesarios.*

---

## 🚀 Paso 2: Levantar el Backend

### Opción A: Desde la Terminal (Recomendado)

1. Abre una terminal (PowerShell o CMD) en la carpeta `lp2/jdrefrigeracion-backend`.
2. Ejecuta el siguiente comando:

```bash
.\mvnw.cmd clean spring-boot:run
```

### Opción B: Desde un IDE (IntelliJ / Eclipse)

1. Abre tu IDE y selecciona **Open Project**.
2. Navega hasta la carpeta `lp2/jdrefrigeracion-backend` y selecciona el archivo `pom.xml`.
3. Deja que el IDE descargue las dependencias.
4. Ejecuta la clase principal: `JdRefrigeracionApplication.java`.

> **Nota:** La primera vez que el proyecto inicie, la clase `InsertDummyData.java` insertará automáticamente algunos proveedores e ítems de inventario de prueba en la base de datos.

---

## 🌐 Paso 3: Levantar el Frontend

1. Abre una terminal en la carpeta `lp2/jdrefrigeracion-frontend`.
2. Instala las dependencias (solo la primera vez):

```bash
npm install
```

3. Levanta el servidor de desarrollo:

```bash
ng serve
```

4. Abre `http://localhost:4200` en tu navegador.

---

## 🧪 Paso 4: Probar la API

Una vez que la consola muestre que Tomcat ha iniciado en el puerto `8081`, puedes probar los endpoints sin necesidad de Postman.

Ingresa a tu navegador web y abre Swagger UI:
👉 **[http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)**

---

## 📦 Estructura de Módulos

| Módulo | Tipo | Descripción |
|---|---|---|
| `inventario` | Catálogo | Gestión de materiales, productos y categorías. |
| `proveedores` | Catálogo | Directorio de proveedores (dato maestro). |
| `clientes` | Catálogo | Registro de clientes (persona natural / empresa). |
| `compras` | Transaccional | Compras (cabecera-detalle) que incrementan el stock. |
| `ventas` | Transaccional | Ventas (cabecera-detalle) con descuento de stock, IGV y equivalencias en PEN/USD. |
| `facturacion` | Transaccional | (En construcción) Emisión de comprobantes SUNAT a partir de ventas. |
| `postventa` | Transaccional | (En construcción) Gestión de devoluciones, garantías y notas de crédito. |
| `notificaciones` | Transversal | (En construcción) Servicio de alertas y correos electrónicos. |
| `common` / `exception` | Transversal | Arquitectura base, utilidades y manejo global de errores. |

---

## 👥 Equipo - JD Devs

| Integrante | Módulo Transaccional | Módulo Catálogo |
|---|---|---|
| Yoel Wagner Chambi Sirena | Compras | Proveedores |
| Aron Rodrigo | **Ventas** | Inventario (Materiales) |
| Ronald Apaza | Cotizaciones | Clientes |