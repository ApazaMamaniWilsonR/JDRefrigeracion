package pe.edu.upeu.jdrefrigeracion.notificaciones.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.common.exception.RecursoNoEncontradoException;
import pe.edu.upeu.jdrefrigeracion.notificaciones.dto.NotificacionRequest;
import pe.edu.upeu.jdrefrigeracion.notificaciones.dto.NotificacionResponse;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.EstadoNotificacion;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.Notificacion;
import pe.edu.upeu.jdrefrigeracion.notificaciones.repository.NotificacionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionResponse crear(NotificacionRequest request) {
        Notificacion notificacion = Notificacion.builder()
                .tipo(request.tipo())
                .titulo(request.titulo())
                .mensaje(request.mensaje())
                .estado(EstadoNotificacion.NO_LEIDA)
                .fechaCreacion(LocalDateTime.now())
                .referenciaTipo(request.referenciaTipo())
                .referenciaId(request.referenciaId())
                .build();

        return toResponse(notificacionRepository.save(notificacion));
    }

    public List<NotificacionResponse> listarTodas() {
        return notificacionRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<NotificacionResponse> listarNoLeidas() {
        return notificacionRepository.findByEstadoOrderByFechaCreacionDesc(EstadoNotificacion.NO_LEIDA)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public long contarNoLeidas() {
        return notificacionRepository.countByEstado(EstadoNotificacion.NO_LEIDA);
    }

    public NotificacionResponse marcarComoLeida(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con id: " + id));

        notificacion.setEstado(EstadoNotificacion.LEIDA);

        return toResponse(notificacionRepository.save(notificacion));
    }

    public void eliminar(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificación no encontrada con id: " + id));

        notificacionRepository.delete(notificacion);
    }

    private NotificacionResponse toResponse(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getTipo(),
                notificacion.getTitulo(),
                notificacion.getMensaje(),
                notificacion.getEstado(),
                notificacion.getFechaCreacion(),
                notificacion.getReferenciaTipo(),
                notificacion.getReferenciaId()
        );
    }
}