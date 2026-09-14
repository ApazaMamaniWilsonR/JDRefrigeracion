package pe.edu.upeu.jdrefrigeracion.inventario.item.service;

import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioRequest;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;

import java.util.List;

public interface InventarioService {
    List<InventarioResponse> listar();
    InventarioResponse obtener(Long id);
    InventarioResponse crear(InventarioRequest request);
    InventarioResponse actualizar(Long id, InventarioRequest request);
    void eliminar(Long id);
    List<InventarioResponse> alertasStockBajo();
    void incrementarStock(Long id, Integer cantidad);
}
