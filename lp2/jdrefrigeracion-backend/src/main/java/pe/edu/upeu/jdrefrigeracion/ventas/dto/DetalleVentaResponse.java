package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import java.math.BigDecimal;

public record DetalleVentaResponse(
        Long productoId,
        String producto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}