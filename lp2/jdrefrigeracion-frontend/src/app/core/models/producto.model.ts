export interface Producto {
  id?: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  stock: number;
  stockMinimo?: number;
  categoriaId?: number;
  categoria?: string;
  activo?: boolean;
}

export interface ProductoRequest {
  nombre: string;
  descripcion?: string;
  precio: number;
  stock: number;
  stockMinimo: number;
  categoriaId: number;
}
