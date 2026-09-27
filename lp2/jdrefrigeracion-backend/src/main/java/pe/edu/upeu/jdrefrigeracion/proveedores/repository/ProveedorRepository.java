package pe.edu.upeu.jdrefrigeracion.proveedores.repository;
import pe.edu.upeu.jdrefrigeracion.proveedores.entity.Proveedor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByRuc(String ruc);
}
