package pe.edu.upeu.jdrefrigeracion.clientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.jdrefrigeracion.clientes.entity.Cliente;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);
}