import { Proveedor } from '../../core/models/proveedor.model';
import { Producto } from '../../core/models/producto.model';

export interface DetalleCompra {
  id?: number;
  productoId?: number;
  producto?: Producto;
  cantidad: number;
  precioUnitarioCompra: number;
}

export interface Compra {
  id?: number;
  proveedorId?: number;
  proveedor?: Proveedor;
  fechaCompra: string;
  numeroFactura?: string;
  moneda: 'PEN' | 'USD';
  total: number;
  detalles: DetalleCompra[];
}
