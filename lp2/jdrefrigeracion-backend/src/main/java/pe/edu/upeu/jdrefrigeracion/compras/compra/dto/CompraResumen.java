package pe.edu.upeu.jdrefrigeracion.compras.compra.dto;

import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.EstadoCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraResumen(Long id, LocalDateTime fecha, EstadoCompra estado, BigDecimal total, int cantidadItems) {
}
