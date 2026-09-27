package pe.edu.upeu.jdrefrigeracion.notificaciones.dto;

import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.EstadoNotificacion;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.TipoNotificacion;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        TipoNotificacion tipo,
        String titulo,
        String mensaje,
        EstadoNotificacion estado,
        LocalDateTime fechaCreacion,
        String referenciaTipo,
        Long referenciaId
) {
}