package pe.edu.upeu.jdrefrigeracion.facturacion.dto;

import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.TipoComprobante;

public record ComprobanteRequest(
        @NotNull(message = "La venta es obligatoria")
        Long ventaId,

        @NotNull(message = "El tipo de comprobante es obligatorio")
        TipoComprobante tipoComprobante
) {
}