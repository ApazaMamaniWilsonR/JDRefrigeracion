package pe.edu.upeu.jdrefrigeracion.clientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.upeu.jdrefrigeracion.clientes.entity.TipoCliente;

public record ClienteRequest(
        @NotNull(message = "El tipo de cliente es obligatorio")
        TipoCliente tipoCliente,

        @NotBlank(message = "El número de documento es obligatorio")
        @Size(min = 8, max = 11, message = "El documento debe tener entre 8 y 11 dígitos")
        String numeroDocumento,

        String nombres,
        String apellidos,
        String razonSocial,
        String direccion,
        String estadoContribuyente,
        String condicionContribuyente,
        String correo,
        String telefono,
        String personaContacto
) {
}