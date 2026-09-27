package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.MetodoPago;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;

import java.math.BigDecimal;

public record PagoRequest(
        @NotNull(message = "La venta es obligatoria")
        Long ventaId,

        @NotNull(message = "La moneda es obligatoria")
        Moneda moneda,

        @NotNull(message = "El tipo de cambio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El tipo de cambio debe ser mayor a cero")
        BigDecimal tipoCambio,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        String observacion
) {
}