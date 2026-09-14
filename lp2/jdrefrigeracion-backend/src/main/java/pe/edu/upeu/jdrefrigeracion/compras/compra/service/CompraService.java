package pe.edu.upeu.jdrefrigeracion.compras.compra.service;

import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraReporte;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraRequest;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraResponse;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.EstadoCompra;

import java.time.LocalDateTime;
import java.util.List;

public interface CompraService {
    List<CompraResponse> buscar(EstadoCompra estado, LocalDateTime desde, LocalDateTime hasta, String ordenarPor, String direccion);
    CompraResponse obtener(Long id);
    CompraResponse crear(CompraRequest request);
    CompraReporte reporte(EstadoCompra estado, LocalDateTime desde, LocalDateTime hasta);
}
