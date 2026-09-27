package pe.edu.upeu.jdrefrigeracion.postventa.dto;

import pe.edu.upeu.jdrefrigeracion.postventa.entity.EstadoMantenimiento;

import java.time.LocalDate;

public record MantenimientoResponse(
        Long id,
        Long instalacionId,
        Long ventaId,
        String cliente,
        LocalDate fechaProgramada,
        LocalDate fechaRealizada,
        EstadoMantenimiento estado,
        String observaciones
) {
}