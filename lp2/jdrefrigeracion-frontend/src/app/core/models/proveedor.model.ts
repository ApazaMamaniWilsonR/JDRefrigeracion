export type TipoProveedor = 'PERSONA_NATURAL' | 'EMPRESA';

export interface Proveedor {
  id?: number;
  tipo: TipoProveedor;
  nombreORazonSocial: string;
  ruc?: string;
  dni?: string;
  direccion?: string;
  telefono?: string;
  correo?: string;
  contactoVendedor?: string;
}
