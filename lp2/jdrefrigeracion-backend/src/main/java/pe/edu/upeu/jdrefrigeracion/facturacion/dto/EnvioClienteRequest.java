package pe.edu.upeu.jdrefrigeracion.facturacion.dto;

import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.EstadoEnvioCliente;

public record EnvioClienteRequest(
        @NotNull(message = "El medio de envío es obligatorio")
        EstadoEnvioCliente medioEnvio
) {
}