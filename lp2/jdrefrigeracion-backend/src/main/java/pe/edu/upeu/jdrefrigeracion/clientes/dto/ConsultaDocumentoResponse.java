package pe.edu.upeu.jdrefrigeracion.clientes.dto;

import pe.edu.upeu.jdrefrigeracion.clientes.entity.TipoCliente;

public record ConsultaDocumentoResponse(
        TipoCliente tipoCliente,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String razonSocial,
        String nombreComercial,
        String direccion,
        String estadoContribuyente,
        String condicionContribuyente
) {
}