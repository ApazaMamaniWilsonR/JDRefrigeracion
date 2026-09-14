package pe.edu.upeu.jdrefrigeracion.inventario.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.upeu.jdrefrigeracion.inventario.item.entity.Inventario;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    @Query("SELECT i FROM Inventario i WHERE i.stock <= i.stockMinimo")
    List<Inventario> findAlertasStockBajo();
}
