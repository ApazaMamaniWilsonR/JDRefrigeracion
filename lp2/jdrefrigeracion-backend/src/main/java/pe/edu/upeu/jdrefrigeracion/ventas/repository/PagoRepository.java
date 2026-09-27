package pe.edu.upeu.jdrefrigeracion.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Pago;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByVentaId(Long ventaId);
}