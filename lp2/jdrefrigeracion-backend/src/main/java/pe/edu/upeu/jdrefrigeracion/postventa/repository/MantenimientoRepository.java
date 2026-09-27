package pe.edu.upeu.jdrefrigeracion.postventa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.postventa.entity.EstadoMantenimiento;
import pe.edu.upeu.jdrefrigeracion.postventa.entity.Mantenimiento;

import java.time.LocalDate;
import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {

    List<Mantenimiento> findByEstado(EstadoMantenimiento estado);

    List<Mantenimiento> findByFechaProgramadaBetween(LocalDate desde, LocalDate hasta);

    List<Mantenimiento> findByFechaProgramadaLessThanEqualAndEstado(
            LocalDate fecha,
            EstadoMantenimiento estado
    );
}