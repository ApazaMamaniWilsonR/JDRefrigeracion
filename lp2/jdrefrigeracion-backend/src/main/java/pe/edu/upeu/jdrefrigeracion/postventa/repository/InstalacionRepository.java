package pe.edu.upeu.jdrefrigeracion.postventa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.postventa.entity.Instalacion;

import java.util.Optional;

public interface InstalacionRepository extends JpaRepository<Instalacion, Long> {

    boolean existsByVentaId(Long ventaId);

    Optional<Instalacion> findByVentaId(Long ventaId);
}