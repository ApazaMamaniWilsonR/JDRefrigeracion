# Brief Técnico del Proyecto Integrador - JD Refrigeración S.A.C.

Este documento declara el proyecto que el equipo construirá durante el ciclo, definiendo el alcance y la asignación de módulos para cada integrante.

## 1. Datos del equipo

- **Nombre del equipo:** JD Devs (o nombre a definir)
- **Sección:** (Por completar)
- **Repositorio (URL):** (Por completar)
- **Topics del repositorio configurados (sí/no):** (Por completar)

**Integrantes y curso(s) que lleva cada uno:**

| Integrante | ADS | BD2 | LP2 |
|---|---|---|---|
| Yoel Wagner Chambi Sirena | Sí | Sí | Sí |
| Aron Rodrigo | Sí | Sí | Sí |
| Ronald Apaza | Sí | Sí | Sí |

## 2. Dominio del proyecto

- **Nombre del proyecto:** Sistema de Gestión ERP para JD Refrigeración S.A.C.
- **Problema o necesidad que resuelve:** JD Refrigeración actualmente maneja sus cotizaciones, ventas, compras e inventario de manera manual mediante Excel, lo que genera retrasos, pérdida de información y descontrol en el stock de materiales. El sistema centralizará y automatizará estas operaciones.
- **Dominio de negocio:** Servicios e instalación de sistemas de climatización, refrigeración y ventilación.
- **Usuarios / actores principales:** Administrador (gestión total), Vendedor (cotizaciones y ventas), Encargado de Almacén (inventario y compras), Cliente (solicitante del servicio).
- **¿Continúa un proyecto de un ciclo anterior, o es un dominio nuevo?** Es un dominio nuevo basado en la empresa real JD Refrigeración S.A.C.

## 3. Módulos de negocio y alcance full-stack esperado

**Asignación de módulos:**

| Integrante | Módulo transaccional (tipo `ventas`) | Módulo no transaccional (tipo `catalogo`) |
|---|---|---|
| Yoel Wagner Chambi Sirena | Compras | Proveedores |
| Aron Rodrigo | Ventas | Inventario (Materiales) |
| Ronald Apaza | Cotizaciones | Clientes |

---

### Módulo: Compras (integrante: Yoel Wagner Chambi Sirena · tipo: transaccional)

- **Descripción breve:** Permite registrar las compras de materiales e insumos a los proveedores para abastecer el inventario. Al registrar una compra, el stock de los materiales aumenta automáticamente.
- **Entidad principal o cabecera-detalle:** `Compra` (cabecera) / `DetalleCompra` (detalle).
- **Lista inicial de requisitos:**
    1. El sistema debe permitir registrar una compra asociando un proveedor y múltiples ítems de inventario con sus cantidades y precios.
    2. El sistema debe calcular automáticamente el subtotal por ítem y el total de la compra.
    3. El sistema debe incrementar automáticamente el stock de los ítems en el módulo de inventario al registrar una compra.

### Módulo: Proveedores (integrante: Yoel Wagner Chambi Sirena · tipo: no transaccional)

- **Descripción breve:** Gestiona el directorio de empresas proveedoras que suministran materiales y repuestos a JD Refrigeración. Es el dato maestro necesario para el módulo de compras.
- **Entidad principal:** `Proveedor`.
- **Lista inicial de requisitos:**
    1. El sistema debe permitir registrar, actualizar y eliminar proveedores.
    2. El sistema debe validar que el RUC del proveedor tenga exactamente 11 dígitos y sea único.
    3. El sistema debe permitir listar todos los proveedores registrados para seleccionarlos al realizar una compra.

---

### Módulo: Ventas (integrante: Aron Rodrigo · tipo: transaccional)

- **Descripción breve:** Permite registrar la venta de servicios o productos finales a los clientes. Al registrar una venta, el stock de los materiales utilizados se descuenta.
- **Entidad principal o cabecera-detalle:** `Venta` (cabecera) / `DetalleVenta` (detalle).
- **Lista inicial de requisitos:**
    1. El sistema debe registrar una venta relacionándola con un cliente y detallando los servicios/materiales vendidos.
    2. El sistema debe descontar automáticamente el stock de los materiales vendidos.
    3. El sistema debe impedir que se concrete una venta si no hay stock suficiente de un material requerido.

### Módulo: Inventario (integrante: Aron Rodrigo · tipo: no transaccional)

- **Descripción breve:** Administra el catálogo de materiales de servicio (tuberías, gas, planchas) y equipos, controlando sus niveles de stock actuales y mínimos.
- **Entidad principal:** `Inventario` (o `Material`).
- **Lista inicial de requisitos:**
    1. El sistema debe permitir registrar, editar y listar materiales con su respectivo precio y unidad de medida.
    2. El sistema debe mantener el registro del stock actual y el stock mínimo permitido para cada ítem.
    3. El sistema debe proveer una alerta o listado especial de los ítems cuyo stock actual haya caído por debajo de su stock mínimo.

---

### Módulo: Cotizaciones (integrante: Ronald Apaza · tipo: transaccional)

- **Descripción breve:** Permite generar presupuestos estimados para los clientes antes de concretar una venta de servicio de refrigeración. Las cotizaciones pueden aprobarse o rechazarse.
- **Entidad principal o cabecera-detalle:** `Cotizacion` (cabecera) / `DetalleCotizacion` (detalle).
- **Lista inicial de requisitos:**
    1. El sistema debe permitir crear una cotización detallando servicios, materiales estimados y precios, asociada a un cliente.
    2. El sistema debe calcular el monto total estimado sin afectar el inventario real.
    3. El sistema debe permitir cambiar el estado de la cotización (ej. Emitida, Aprobada, Rechazada) y facilitar su conversión a una Venta real si es aprobada.

### Módulo: Clientes (integrante: Ronald Apaza · tipo: no transaccional)

- **Descripción breve:** Gestiona el registro de los clientes (personas naturales o empresas) a los cuales JD Refrigeración brinda sus servicios.
- **Entidad principal:** `Cliente`.
- **Lista inicial de requisitos:**
    1. El sistema debe registrar la información completa del cliente (DNI/RUC, Razón Social/Nombre, Dirección, Contacto).
    2. El sistema debe permitir buscar clientes por su documento de identidad.
    3. El sistema debe permitir actualizar los datos de contacto de un cliente existente.

---

- **Qué SÍ cubre este proyecto en conjunto:** Gestión del flujo central de la empresa: desde la solicitud (Cotización) a la entrega (Venta), controlando el abastecimiento (Compras, Proveedores) y el almacén (Inventario).
- **Qué NO cubre — fuera de alcance:** No incluye facturación electrónica directa con SUNAT, ni módulo contable o de planillas (RRHH).

## 4. Aprobación

- **Docente ADS:**
- **Docente BD2:**
- **Docente LP2:**
- **Fecha:**
