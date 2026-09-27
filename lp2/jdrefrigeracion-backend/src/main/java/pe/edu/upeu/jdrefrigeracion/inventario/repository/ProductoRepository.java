package pe.edu.upeu.jdrefrigeracion.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.inventario.entity.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByStockLessThanEqual(Integer stockMinimo);
}