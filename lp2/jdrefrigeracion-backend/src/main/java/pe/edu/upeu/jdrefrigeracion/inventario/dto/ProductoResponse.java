package pe.edu.upeu.jdrefrigeracion.inventario.dto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        Integer stockMinimo,
        String categoria,
        Boolean activo
) {
}