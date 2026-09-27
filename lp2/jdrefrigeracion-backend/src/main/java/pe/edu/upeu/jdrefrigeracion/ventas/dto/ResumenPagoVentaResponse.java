package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoPago;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;

import java.math.BigDecimal;
import java.util.List;

public record ResumenPagoVentaResponse(
        Long ventaId,
        Moneda monedaVenta,
        BigDecimal totalVenta,
        BigDecimal totalSoles,
        BigDecimal totalDolares,
        BigDecimal totalPagadoSoles,
        BigDecimal totalPagadoDolares,
        BigDecimal saldoPendienteSoles,
        BigDecimal saldoPendienteDolares,
        EstadoPago estadoPago,
        List<PagoResponse> pagos
) {
}