# Informe: Actividad Autónoma (S07) - Creación y Arquitectura de la SPA

## Datos del estudiante
- **Nombre:** [Escribe tu nombre aquí]
- **Equipo:** JD Devs
- **Sesión:** S07 - Creación y Arquitectura de la SPA
- **Rol o aporte realizado:** Creación del proyecto frontend Angular 22 (`jdrefrigeracion-frontend`) con arquitectura `core`/`shared`/`features`, layout con encabezado y sidebar, interceptor HTTP de trazabilidad, servicio HTTP dedicado y CRUD completo de Categoría (tabla independiente) conectado al backend real de JD Refrigeración.
- **Link de GitHub:** [Pega tu link aquí]

---

## Evidencia técnica

### 1. Proyecto y arquitectura

- **Explicación:** El proyecto fue creado con Angular CLI 22 siguiendo la convención `core`/`shared`/`features`. Dentro de `core/` se ubican las piezas transversales de la aplicación: el `Layout` (encabezado + sidebar), el componente `Inicio`, el servicio de infraestructura `ApiService` y el interceptor `traceIdInterceptor`. Dentro de `features/` se organizan los módulos de negocio por funcionalidad: `catalogo/categoria/` contiene el modelo, servicio y componentes (list/form) de Categoría; `ventas/` contiene el mismo patrón para Ventas.

- **Estructura de carpetas:**

```text
jdrefrigeracion-frontend/src/app/
├── core/
│   ├── inicio/
│   │   ├── inicio.ts
│   │   └── inicio.html
│   ├── interceptors/
│   │   └── trace-id-interceptor.ts
│   ├── layout/
│   │   ├── layout.ts
│   │   ├── layout.html
│   │   └── layout.css
│   ├── models/
│   │   ├── cliente.model.ts
│   │   └── producto.model.ts
│   └── services/
│       ├── api-service.ts
│       ├── cliente.service.ts
│       └── producto.service.ts
├── features/
│   ├── catalogo/
│   │   └── categoria/
│   │       ├── categoria.model.ts
│   │       ├── categoria-service.ts
│   │       ├── categoria-list.ts
│   │       ├── categoria-list.html
│   │       ├── categoria-list.css
│   │       └── categoria-form/
│   │           ├── categoria-form.ts
│   │           ├── categoria-form.html
│   │           └── categoria-form.css
│   └── ventas/
│       ├── venta.model.ts
│       ├── venta.service.ts
│       ├── venta-list/
│       │   ├── venta-list.ts
│       │   ├── venta-list.html
│       │   └── venta-list.css
│       └── venta-form/
│           ├── venta-form.ts
│           ├── venta-form.html
│           └── venta-form.css
├── app.ts
├── app.html
├── app.config.ts
└── app.routes.ts
```

- **Captura:** *(Agrega aquí una captura de la estructura de carpetas en VS Code mostrando el árbol `core`/`features` desplegado, con el reloj del sistema y tu usuario visibles.)*

---

### 2. Layout y navegación

- **Explicación:** Se implementó un componente `Layout` en `core/layout/` que actúa como ruta padre y envuelve todas las pantallas de la aplicación. Incluye un encabezado con el nombre "JD Refrigeración", un sidebar vertical con enlaces a las rutas "Inicio", "Categorías" y "Ventas", y un `<router-outlet>` donde se renderizan las pantallas hijas. La ruta raíz `/` carga una página de inicio real (`Inicio`) con un mensaje de bienvenida — sin redirect hacia ninguna otra pantalla. La directiva `routerLinkActive="active"` resalta visualmente el enlace de la sección activa.

- **Rutas configuradas (`app.routes.ts`):**

```ts
export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./core/layout/layout').then((m) => m.Layout),
    children: [
      { path: '', loadComponent: () => import('./core/inicio/inicio').then((m) => m.Inicio) },
      { path: 'catalogo/categorias', loadComponent: () => ... CategoriaList },
      { path: 'catalogo/categorias/nuevo', loadComponent: () => ... CategoriaForm },
      { path: 'catalogo/categorias/:id', loadComponent: () => ... CategoriaForm },
      { path: 'ventas', loadComponent: () => ... VentaList },
      { path: 'ventas/nuevo', loadComponent: () => ... VentaForm },
    ],
  },
];
```

- **Captura 1:** *(Agrega aquí una captura de la página de inicio (`http://localhost:4200/`) mostrando el encabezado "JD Refrigeración", el sidebar vertical con los 3 enlaces, y el mensaje de bienvenida. Reloj del sistema y usuario visibles.)*
- **Captura 2:** *(Agrega aquí una captura navegando a la ruta `/catalogo/categorias`, mostrando que el layout se mantiene y solo el contenido cambia.)*

---

### 3. Servicio HTTP

- **Explicación:** Se creó un servicio de infraestructura `ApiService` en `core/services/` cuya única responsabilidad es conocer la URL base del backend (`http://localhost:8080`) y construir la URL completa para cualquier endpoint. Ningún servicio de funcionalidad conoce el host ni el puerto: todos delegan en `ApiService.buildUrl()`.

  El servicio de funcionalidad `CategoriaService` (en `features/catalogo/categoria/`) inyecta `HttpClient` y `ApiService`, y expone los cinco métodos del CRUD: `listar()`, `obtener(id)`, `crear()`, `actualizar()` y `eliminar()`. Ningún componente importa `HttpClient` directamente.

  Adicionalmente, se implementó un interceptor HTTP funcional (`traceIdInterceptor`) en `core/interceptors/` que agrega un header `X-Trace-ID` con un UUID único (`crypto.randomUUID()`) a **cada** petición saliente, registrado globalmente en `app.config.ts` con `withInterceptors([traceIdInterceptor])`.

- **Código del interceptor:**

```ts
export const traceIdInterceptor: HttpInterceptorFn = (req, next) => {
  const traceId = crypto.randomUUID();
  return next(req.clone({ headers: req.headers.set('X-Trace-ID', traceId) }));
};
```

- **Código del ApiService:**

```ts
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = environment.apiBaseUrl;

  buildUrl(path: string): string {
    const normalizedPath = path.startsWith('/') ? path : `/${path}`;
    return `${this.baseUrl}${normalizedPath}`;
  }
}
```

- **Captura 1:** *(Agrega aquí una captura de la pestaña Network del navegador (Chrome DevTools), expandiendo los Request Headers de cualquier petición a `/api/v1/categorias`, mostrando el header `X-Trace-ID` con su UUID. Reloj del sistema visible.)*
- **Captura 2:** *(Agrega aquí una captura de una petición exitosa (Status 200) en la pestaña Network, mostrando la respuesta JSON del backend real.)*

---

### 4. CRUD independiente

- **Explicación:** Se implementó un CRUD completo de **Categoría** (tabla independiente — no depende de ninguna otra entidad para crearse). Los cuatro casos fueron probados contra el backend real (`http://localhost:8080/api/v1/categorias`):

  - **Crear:** Formulario reactivo (`ReactiveFormsModule`) con validación de campo requerido y longitud máxima. Al guardar, redirige a la lista y la nueva categoría aparece de inmediato (sin necesidad de recargar la página).
  - **Listar:** Tabla que muestra todas las categorías existentes usando un `signal<Categoria[]>` para garantizar la reactividad.
  - **Editar:** Al dar clic en "Editar", se navega al formulario con el `:id` en la URL. El formulario carga los datos actuales de la categoría desde el backend y permite modificarlos.
  - **Eliminar:** Botón con confirmación (`confirm()`) que elimina la categoría del backend y la remueve de la lista al instante.

- **Captura 1 (Crear):** *(Agrega aquí una captura del formulario "Nueva Categoría" con datos ingresados, antes de guardar. Reloj visible.)*
- **Captura 2 (Listar):** *(Agrega aquí una captura de la tabla de categorías mostrando las categorías creadas. Reloj visible.)*
- **Captura 3 (Editar):** *(Agrega aquí una captura del formulario de edición con los datos precargados de una categoría existente. Reloj visible.)*
- **Captura 4 (Eliminar):** *(Agrega aquí una captura de la tabla después de eliminar una categoría, mostrando que ya no aparece. Reloj visible.)*

---

## Error o hallazgo

- **Hallazgo durante el desarrollo:** Al generar el proyecto con Angular CLI 22, la aplicación fue creada en modo **Zoneless** (sin Zone.js) por defecto. Esto causó que, al registrar una categoría y volver a la lista, la tabla apareciera vacía — los datos sí llegaban del servidor (se veían en la pestaña Network), pero la pantalla no se repintaba porque Angular Zoneless no detecta automáticamente cambios producidos por `subscribe()` de un `Observable`. El problema se resolvió cambiando la variable `categorias: Categoria[]` por un **Signal** (`categorias = signal<Categoria[]>([])`), y actualizándola con `.set(data)` en lugar de una asignación directa. Los Signals son la forma oficial de Angular 17+ para forzar la reactividad sin depender de Zone.js. Este mismo patrón se replicó en todos los componentes de lista del proyecto (`CategoriaList`, `VentaList`).

---

## Reflexión técnica breve

> **¿Por qué separar `CategoriaService` del componente que lo usa facilita un cambio futuro en la URL o en la forma de consumir el backend?**

Separar el servicio HTTP del componente crea una capa de abstracción clara: el componente solo conoce métodos como `listar()`, `crear()` o `eliminar()`, sin saber qué verbo HTTP usan, qué URL invocan ni qué headers necesitan. Si mañana la URL del endpoint cambia de `/api/v1/categorias` a `/api/v2/categorias`, o si el backend migra de REST a GraphQL, el cambio se hace **únicamente** en `CategoriaService` — los componentes `CategoriaList` y `CategoriaForm` ni se enteran. Además, al existir `ApiService` como segunda capa, la URL base del servidor también se centraliza: si el backend se muda de `localhost:8080` a un dominio real de producción, se modifica en un solo lugar (`environment.ts`), y todos los servicios de funcionalidad (`CategoriaService`, `VentaService`, `ProductoService`) heredan ese cambio sin tocar ni una línea.

---

## Anexo: Feedback de la sesión

1. **¿Cuál es el aprendizaje más importante que te llevas de la clase de hoy?**
   [Escribe tu respuesta aquí]

2. **¿Qué punto de la clase te resultó más confuso o te dejó con dudas?**
   [Escribe tu respuesta aquí]

3. **¿Tienes alguna pregunta que te gustaría que sea respondida la siguiente clase?**
   [Escribe tu respuesta aquí]

4. **Sobre tu nivel de comprensión de la clase de hoy, marca una opción:**
   - [ ] ¡Entendido! - Lo domino y podría explicarlo.
   - [ ] Más o menos. - Entendí la idea general, pero tengo dudas.
   - [ ] Necesito ayuda. - Me siento perdido/a con este tema.

5. **¿Cómo puedo ayudarte a comprender mejor el tema?**
   [Escribe tu respuesta aquí]

6. **Pensando en tu participación y esfuerzo en la clase de hoy, ¿cómo te autoevaluarías? Marca una opción:**
   - [ ] Muy Comprometido/a: Me esforcé al máximo.
   - [ ] Comprometido/a: Sé que podría haberme esforzado un poco más.
   - [ ] Poco Comprometido/a: Hoy no di mi mejor esfuerzo.

7. **Mi satisfacción con la clase fue...** (califica del 1 al 10): ____
