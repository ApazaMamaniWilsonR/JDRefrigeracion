package pe.edu.upeu.jdrefrigeracion.notificaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.TipoNotificacion;

public record NotificacionRequest(
        @NotNull(message = "El tipo de notificación es obligatorio")
        TipoNotificacion tipo,

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        String referenciaTipo,

        Long referenciaId
) {
}