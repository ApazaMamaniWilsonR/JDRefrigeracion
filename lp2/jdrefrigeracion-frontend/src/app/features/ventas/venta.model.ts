export interface DetalleVentaRequest {
  productoId: number;
  cantidad: number;
}

export interface VentaRequest {
  clienteId: number;
  moneda: string;
  tipoCambio: number;
  descuento: number;
  detalles: DetalleVentaRequest[];
}

export interface DetalleVentaResponse {
  id: number;
  productoId: number;
  producto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface VentaResponse {
  id: number;
  fecha: string;
  estado: string;
  moneda: string;
  tipoCambio: number;
  clienteId: number;
  cliente: string;
  subtotal: number;
  descuento: number;
  igv: number;
  total: number;
  totalSoles: number;
  totalDolares: number;
  detalles: DetalleVentaResponse[];
}
