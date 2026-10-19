# Sesión 10b: Seguridad Backend con Keycloak - JD Refrigeración S.A.C.

**Proyecto:** JD Refrigeración S.A.C.  
**Equipo:** Wilson R. Apaza Mamani, Yoel Chambi, Aron Rodrigo  
**Sesión:** S10b - Seguridad Backend con Keycloak (OAuth2 Resource Server + JWT + RBAC + RN8)  
**Rama:** `tarea10`  
**Repositorio:** [https://github.com/ApazaMamaniWilsonR/JDRefrigeracion](https://github.com/ApazaMamaniWilsonR/JDRefrigeracion)

---

## 1. Resumen de la Arquitectura Implementada

Se implementó la arquitectura de seguridad OAuth2 Resource Server con **Keycloak** para el backend de JD Refrigeración, sustituyendo la necesidad de un módulo de seguridad local monolítico y desacoplando la emisión de tokens de la lógica de negocio.

```
+-------------------------------------------------------------+
|                     Keycloak (localhost:38080)             |
|   Realm: bomerp                                             |
|   Cliente: bomerp-frontend / jdrefrigeracion-frontend       |
|   Roles de Realm: ADMIN, SUPERVISOR, VENDEDOR               |
|   Usuarios Semilla: vendedor1, vendedor2, supervisor, admin |
+-------------------------------------------------------------+
                            |
           1. Token JWT (RS256, roles en realm_access.roles)
                            v
+-------------------------------------------------------------+
|             Backend Spring Boot (localhost:8081)           |
|   - OAuth2 Resource Server (spring.security.oauth2)         |
|   - JwtAuthenticationConverter (ROLE_ADMIN, etc.)           |
|   - SecurityConfig: Reglas por endpoint y rol               |
|   - RN8: Control de acceso por dueño (vendedorId = sub)     |
|   - OpenApiConfig: Soporte JWT Bearer en Swagger UI         |
+-------------------------------------------------------------+
```

---

## 2. Componentes Implementados

### 2.1 Dependencias Maven (`pom.xml`)
Se incorporaron los starters estándar de Spring Security sin necesidad de firmas locales (delegadas a Keycloak):
- `spring-boot-starter-security`
- `spring-boot-starter-security-oauth2-resource-server`

### 2.2 Configuración (`application-dev.yml`)
Configuración del validador de emisor JWT de Keycloak:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:38080/realms/bomerp}
```

### 2.3 `SecurityConfig.java`
- **Mapeo de Roles:** `JwtAuthenticationConverter` extrae la colección `roles` dentro de `realm_access` y añade el prefijo `ROLE_` (`ROLE_ADMIN`, `ROLE_SUPERVISOR`, `ROLE_VENDEDOR`).
- **Sesión Stateless:** `SessionCreationPolicy.STATELESS` y CSRF deshabilitado.
- **Soporte CORS:** `CorsConfigurationSource` integrado para compatibilidad con Angular (`http://localhost:4200`) y Vite.
- **Reglas de Acceso:**
  - `/actuator/**`, `/swagger-ui/**`, `/v3/api-docs/**`: `permitAll()`
  - `GET /api/v1/productos/**`, `/api/v1/categorias/**`: `authenticated()`
  - `POST/PUT/DELETE /api/v1/productos/**`, `/api/v1/categorias/**`: `hasRole("ADMIN")`
  - `POST /api/v1/ventas`: `hasAnyRole("VENDEDOR", "SUPERVISOR")`
  - `GET /api/v1/ventas/resumen`: `hasAnyRole("SUPERVISOR", "ADMIN")`
  - `PATCH /api/v1/ventas/*/anular`: `hasAnyRole("SUPERVISOR", "ADMIN")`
  - `POST /api/v1/ventas/*/anular`: `hasAnyRole("SUPERVISOR", "ADMIN")`
  - `/api/v1/ventas/**`: `hasAnyRole("VENDEDOR", "SUPERVISOR", "ADMIN")`
  - `/api/admin/compras/**`, `/api/admin/proveedores/**`: `hasAnyRole("SUPERVISOR", "ADMIN")`
  - `/api/v1/clientes/**`, cotizaciones, pagos: `hasAnyRole("VENDEDOR", "SUPERVISOR", "ADMIN")`
  - Cualquier otra petición: `authenticated()`

### 2.4 Swagger con Autorización (`OpenApiConfig.java`)
Configuración de `SecurityScheme` de tipo `HTTP Bearer JWT` (`bearerAuth`) que habilita el candado **Authorize** en Swagger UI (`http://localhost:8081/swagger-ui.html`).

### 2.5 Regla de Negocio RN8: Control de Acceso por Dueño
1. **Entidad `Venta.java`:** Se agregó el atributo `@Column(name = "VENDEDOR_ID", length = 36) private String vendedorId;` para almacenar el UUID emitido por Keycloak (`sub`).
2. **`VentaController.java`:**
   - Inyecta `@AuthenticationPrincipal Jwt jwt`.
   - Si el rol es `VENDEDOR`, extrae `vendedorId = jwt.getSubject()` para filtrar sus ventas.
   - Si el rol es `SUPERVISOR` o `ADMIN`, `vendedorId` es `null`, permitiendo ver todas las ventas.
   - Endpoint `GET /api/v1/ventas/resumen` para reportes agregados.
3. **`VentaService.java`:**
   - `registrar(request, vendedorId)`: Asocia el `vendedorId` a la cabecera de la venta.
   - `listarConFiltros`: Filtra por `vendedorId` si el usuario es vendedor.
   - `buscarPorId(id, vendedorId)`: Si un vendedor intenta consultar una venta con `vendedorId` distinto al suyo, lanza `AccesoDenegadoException`.
   - `resumen(estado, desde, hasta)`: Calcula total de ventas, monto total y ticket promedio (`VentaReporte`).
4. **Manejo de Excepciones:** `AccesoDenegadoException` es capturado retornando HTTP `403 Forbidden`.

### 2.6 Docker Compose y Auto-Import de Keycloak
- `compose-dev.yml`: Define `keycloak` (puerto `38080`) y `keycloak-db` (PostgreSQL 17 en puerto `35432`).
- `keycloak/realm-export.json`: Archivo de importación automática que aprovisiona el realm `bomerp`, clientes `bomerp-frontend` y `jdrefrigeracion-frontend`, roles `ADMIN`, `SUPERVISOR`, `VENDEDOR` y los usuarios de prueba.

---

## 3. Matriz de Accesos Verificada (Tabla 6)

| Operación | `VENDEDOR` (propia) | `VENDEDOR` (ajena) | `SUPERVISOR` / `ADMIN` | Sin token |
|---|---|---|---|---|
| `POST /api/v1/ventas` | `201 Created` | - | `201 Created` | `401 Unauthorized` |
| `GET /api/v1/ventas/{id}` | `200 OK` | `403 Forbidden` | `200 OK` | `401 Unauthorized` |
| `GET /api/v1/ventas/resumen` | `403 Forbidden` | `403 Forbidden` | `200 OK` | `401 Unauthorized` |
| `PATCH /api/v1/ventas/{id}/anular` | `403 Forbidden` | `403 Forbidden` | `200 OK` | `401 Unauthorized` |
| `POST /api/v1/productos` | `403 Forbidden` | `403 Forbidden` | `201 Created` (ADMIN) | `401 Unauthorized` |
| `GET /api/v1/productos` | `200 OK` | `200 OK` | `200 OK` | `401 Unauthorized` |

---

## 4. Guía Rápida de Pruebas

### 4.1 Levantar Keycloak
```powershell
cd "lp2\jdrefrigeracion-backend"
docker compose -f compose-dev.yml up -d
```
Keycloak levantará en `http://localhost:38080` con el realm `bomerp` y los usuarios preconfigurados.
- Administrador Keycloak: `admin` / `admin123`

### 4.2 Obtener Tokens con PowerShell
```powershell
# Token Vendedor 1
$tokenVendedor1 = (Invoke-RestMethod -Method Post -Uri "http://localhost:38080/realms/bomerp/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=password&client_id=bomerp-frontend&username=vendedor1&password=Bomerp2026!").access_token

# Token Vendedor 2
$tokenVendedor2 = (Invoke-RestMethod -Method Post -Uri "http://localhost:38080/realms/bomerp/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=password&client_id=bomerp-frontend&username=vendedor2&password=Bomerp2026!").access_token

# Token Supervisor
$tokenSupervisor = (Invoke-RestMethod -Method Post -Uri "http://localhost:38080/realms/bomerp/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body "grant_type=password&client_id=bomerp-frontend&username=supervisor&password=Bomerp2026!").access_token
```

### 4.3 Pruebas de Autorización y RN8
```powershell
# 1. Probar acceso a resumen (debe dar 403 con vendedor1, 200 con supervisor):
try {
  Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/v1/ventas/resumen" `
    -Headers @{ Authorization = "Bearer $tokenVendedor1" }
} catch {
  Write-Host "Vendedor 1 Status:" $_.Exception.Response.StatusCode.value__ # 403
}

$resumen = Invoke-RestMethod -Method Get -Uri "http://localhost:8081/api/v1/ventas/resumen" `
  -Headers @{ Authorization = "Bearer $tokenSupervisor" }
$resumen | ConvertTo-Json
```
