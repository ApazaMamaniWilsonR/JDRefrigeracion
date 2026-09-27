package pe.edu.upeu.jdrefrigeracion.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Cotizacion;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoCotizacion;

import java.util.List;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    List<Cotizacion> findByEstado(EstadoCotizacion estado);
}