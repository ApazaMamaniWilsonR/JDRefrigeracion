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
        path: 'ventas',
        loadComponent: () =>
          import('./features/ventas/venta-list/venta-list').then((m) => m.VentaList),
      },
      {
        path: 'ventas/nuevo',
        loadComponent: () =>
          import('./features/ventas/venta-form/venta-form').then((m) => m.VentaForm),
      },
    ],
  },
];
