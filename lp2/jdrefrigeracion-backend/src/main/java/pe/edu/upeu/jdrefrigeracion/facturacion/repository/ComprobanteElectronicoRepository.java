package pe.edu.upeu.jdrefrigeracion.facturacion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.ComprobanteElectronico;

import java.util.Optional;

public interface ComprobanteElectronicoRepository extends JpaRepository<ComprobanteElectronico, Long> {

    Optional<ComprobanteElectronico> findByVentaId(Long ventaId);

    boolean existsByVentaId(Long ventaId);
}