package pe.edu.upeu.jdrefrigeracion.postventa.dto;

import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.jdrefrigeracion.postventa.entity.EstadoMantenimiento;

import java.time.LocalDate;

public record ActualizarMantenimientoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoMantenimiento estado,

        LocalDate fechaRealizada,

        String observaciones
) {
}