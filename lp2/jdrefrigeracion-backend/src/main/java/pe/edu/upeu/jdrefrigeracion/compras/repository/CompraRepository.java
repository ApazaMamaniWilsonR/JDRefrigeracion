package pe.edu.upeu.jdrefrigeracion.compras.repository;
import pe.edu.upeu.jdrefrigeracion.compras.entity.Compra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
}
