package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import pe.edu.upeu.jdrefrigeracion.ventas.entity.MetodoPago;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long ventaId,
        LocalDateTime fechaPago,
        Moneda moneda,
        BigDecimal tipoCambio,
        BigDecimal monto,
        BigDecimal montoSoles,
        BigDecimal montoDolares,
        MetodoPago metodoPago,
        String observacion
) {
}