package pe.edu.upeu.jdrefrigeracion.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.edu.upeu.jdrefrigeracion.inventario.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}