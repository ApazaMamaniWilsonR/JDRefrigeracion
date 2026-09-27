package pe.edu.upeu.jdrefrigeracion.postventa.dto;

import java.time.LocalDate;

public record InstalacionResponse(
        Long id,
        Long ventaId,
        String cliente,
        LocalDate fechaInstalacion,
        LocalDate fechaFinGarantia,
        LocalDate fechaProximoMantenimiento,
        String direccionInstalacion,
        String observaciones
) {
}