package pe.edu.upeu.jdrefrigeracion.compras.compra.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraAgregado;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraResumen;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.Compra;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.EstadoCompra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Override
    @EntityGraph(attributePaths = "detalles")
    Optional<Compra> findById(Long id);

    @EntityGraph(attributePaths = "detalles")
    @Query("""
        SELECT c FROM Compra c
        WHERE (:estado IS NULL OR c.estado = :estado)
          AND (:desde IS NULL OR c.fecha >= :desde)
          AND (:hasta IS NULL OR c.fecha <= :hasta)
        """)
    List<Compra> buscar(@Param("estado") EstadoCompra estado,
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta,
                        Sort sort);

    @Query("""
        SELECT new pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraResumen(
            c.id, c.fecha, c.estado, c.total, SIZE(c.detalles))
        FROM Compra c
        WHERE (:estado IS NULL OR c.estado = :estado)
          AND (:desde IS NULL OR c.fecha >= :desde)
          AND (:hasta IS NULL OR c.fecha <= :hasta)
        """)
    List<CompraResumen> buscarResumen(@Param("estado") EstadoCompra estado,
                                      @Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta,
                                      Sort sort);

    @Query("""
        SELECT new pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraAgregado(
            COUNT(c), COALESCE(SUM(c.total), 0BD))
        FROM Compra c
        WHERE (:estado IS NULL OR c.estado = :estado)
          AND (:desde IS NULL OR c.fecha >= :desde)
          AND (:hasta IS NULL OR c.fecha <= :hasta)
        """)
    CompraAgregado agregados(@Param("estado") EstadoCompra estado,
                             @Param("desde") LocalDateTime desde,
                             @Param("hasta") LocalDateTime hasta);
}
