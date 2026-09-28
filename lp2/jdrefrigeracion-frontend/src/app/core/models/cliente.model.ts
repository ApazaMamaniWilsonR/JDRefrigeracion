export interface Cliente {
  id: number;
  tipoCliente: string;
  numeroDocumento: string;
  nombres?: string;
  apellidos?: string;
  razonSocial?: string;
  nombreComercial?: string;
  direccion?: string;
  correo?: string;
  telefono?: string;
  activo: boolean;
}
