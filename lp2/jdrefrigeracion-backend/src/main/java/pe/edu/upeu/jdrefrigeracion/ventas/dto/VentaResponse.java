package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long id,
        LocalDateTime fecha,
        EstadoVenta estado,
        Moneda moneda,
        BigDecimal tipoCambio,
        Long clienteId,
        String cliente,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal igv,
        BigDecimal total,
        BigDecimal totalSoles,
        BigDecimal totalDolares,
        List<DetalleVentaResponse> detalles
) {
}