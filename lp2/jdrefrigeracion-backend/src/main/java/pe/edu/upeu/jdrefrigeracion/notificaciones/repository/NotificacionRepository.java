package pe.edu.upeu.jdrefrigeracion.notificaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.EstadoNotificacion;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.Notificacion;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByEstadoOrderByFechaCreacionDesc(EstadoNotificacion estado);

    List<Notificacion> findAllByOrderByFechaCreacionDesc();

    long countByEstado(EstadoNotificacion estado);
}