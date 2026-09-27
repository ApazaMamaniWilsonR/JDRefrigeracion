package pe.edu.upeu.jdrefrigeracion.postventa.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InstalacionRequest(
        @NotNull(message = "La venta es obligatoria")
        Long ventaId,

        @NotNull(message = "La fecha de instalación es obligatoria")
        LocalDate fechaInstalacion,

        String direccionInstalacion,

        String observaciones
) {
}