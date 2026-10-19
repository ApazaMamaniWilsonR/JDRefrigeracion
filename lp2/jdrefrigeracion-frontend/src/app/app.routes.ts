import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./core/layout/layout').then((m) => m.Layout),
    children: [
      {
        path: '',
        loadComponent: () => import('./core/inicio/inicio').then((m) => m.Inicio),
      },
      {
        path: 'catalogo/categorias',
        loadComponent: () =>
          import('./features/catalogo/categoria/categoria-list').then((m) => m.CategoriaList),
      },
      {
        path: 'catalogo/categorias/nuevo',
        loadComponent: () =>
          import('./features/catalogo/categoria/categoria-form/categoria-form').then((m) => m.CategoriaForm),
      },
      {
        path: 'catalogo/categorias/:id',
        loadComponent: () =>
          import('./features/catalogo/categoria/categoria-form/categoria-form').then((m) => m.CategoriaForm),
      },
      {
        path: 'catalogo/productos',
        loadComponent: () =>
          import('./features/catalogo/producto/producto-list/producto-list').then((m) => m.ProductoList),
      },
      {
        path: 'catalogo/productos/nuevo',
        loadComponent: () =>
          import('./features/catalogo/producto/producto-form/producto-form').then((m) => m.ProductoForm),
      },
      {
        path: 'catalogo/productos/:id',
        loadComponent: () =>
          import('./features/catalogo/producto/producto-form/producto-form').then((m) => m.ProductoForm),
      },
      {
        path: 'clientes',
        loadComponent: () =>
          import('./features/clientes/cliente-list/cliente-list').then((m) => m.ClienteList),
      },
      {
        path: 'clientes/nuevo',
        loadComponent: () =>
          import('./features/clientes/cliente-form/cliente-form').then((m) => m.ClienteForm),
      },
      {
        path: 'clientes/:id',
        loadComponent: () =>
          import('./features/clientes/cliente-form/cliente-form').then((m) => m.ClienteForm),
      },
      {
        path: 'proveedores',
        loadComponent: () =>
          import('./features/proveedores/proveedor-list/proveedor-list').then((m) => m.ProveedorList),
      },
      {
        path: 'proveedores/nuevo',
        loadComponent: () =>
          import('./features/proveedores/proveedor-form/proveedor-form').then((m) => m.ProveedorForm),
      },
      {
        path: 'proveedores/:id',
        loadComponent: () =>
          import('./features/proveedores/proveedor-form/proveedor-form').then((m) => m.ProveedorForm),
      },
      {
        path: 'ventas',
        loadComponent: () =>
          import('./features/ventas/venta-list/venta-list').then((m) => m.VentaList),
      },
      {
        path: 'ventas/nuevo',
        loadComponent: () =>
          import('./features/ventas/venta-form/venta-form').then((m) => m.VentaForm),
      },
      {
        path: 'ventas/nueva',
        loadComponent: () =>
          import('./features/ventas/venta-form/venta-form').then((m) => m.VentaForm),
      },
      {
        path: 'ventas/reporte',
        loadComponent: () =>
          import('./features/ventas/venta-reporte/venta-reporte').then((m) => m.VentaReporteComponent),
      },
      {
        path: 'compras',
        loadComponent: () =>
          import('./features/compras/compra-list/compra-list').then((m) => m.CompraListComponent),
      },
      {
        path: 'compras/nuevo',
        loadComponent: () =>
          import('./features/compras/compra-form/compra-form').then((m) => m.CompraFormComponent),
      },
    ],
  },
];
