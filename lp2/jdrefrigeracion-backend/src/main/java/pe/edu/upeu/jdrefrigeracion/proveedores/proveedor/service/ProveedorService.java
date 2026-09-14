package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.service;

import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorRequest;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorResponse;

import java.util.List;

public interface ProveedorService {
    List<ProveedorResponse> listar();
    ProveedorResponse obtener(Long id);
    ProveedorResponse crear(ProveedorRequest request);
    ProveedorResponse actualizar(Long id, ProveedorRequest request);
    void eliminar(Long id);
}
