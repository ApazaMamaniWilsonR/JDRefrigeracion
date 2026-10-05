package pe.edu.upeu.jdrefrigeracion.ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Venta;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByEstado(EstadoVenta estado);

    List<Venta> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
    
    List<Venta> findByClienteId(Long clienteId);
}