package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;

import java.math.BigDecimal;
import java.util.List;

public record VentaRequest(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "La moneda es obligatoria")
        Moneda moneda,

        @NotNull(message = "El tipo de cambio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El tipo de cambio debe ser mayor a cero")
        BigDecimal tipoCambio,

        BigDecimal descuento,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        List<@Valid DetalleVentaRequest> detalles
) {
}