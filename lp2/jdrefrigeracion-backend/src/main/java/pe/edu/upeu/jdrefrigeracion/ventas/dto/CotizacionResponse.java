package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoCotizacion;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CotizacionResponse(
        Long id,
        LocalDateTime fecha,
        EstadoCotizacion estado,
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
        String observaciones,
        List<DetalleCotizacionResponse> detalles
) {
}