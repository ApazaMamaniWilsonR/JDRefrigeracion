export type TipoCliente = 'PERSONA' | 'EMPRESA';

export interface Cliente {
  id: number;
  tipoCliente: TipoCliente;
  numeroDocumento: string;
  nombres?: string;
  apellidos?: string;
  razonSocial?: string;
  nombreComercial?: string;
  direccion?: string;
  estadoContribuyente?: string;
  condicionContribuyente?: string;
  correo?: string;
  telefono?: string;
  personaContacto?: string;
  activo?: boolean;
}

export interface ClienteRequest {
  tipoCliente: TipoCliente;
  numeroDocumento: string;
  nombres?: string;
  apellidos?: string;
  razonSocial?: string;
  direccion?: string;
  estadoContribuyente?: string;
  condicionContribuyente?: string;
  correo?: string;
  telefono?: string;
  personaContacto?: string;
}

export interface ConsultaDocumentoResponse {
  tipoCliente: TipoCliente;
  numeroDocumento: string;
  nombres?: string;
  apellidos?: string;
  razonSocial?: string;
  nombreComercial?: string;
  direccion?: string;
  estadoContribuyente?: string;
  condicionContribuyente?: string;
}
