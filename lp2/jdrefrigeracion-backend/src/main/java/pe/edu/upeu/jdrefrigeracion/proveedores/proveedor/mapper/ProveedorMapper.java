package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorRequest;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorResponse;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.entity.Proveedor;

@Mapper(componentModel = "spring")
public interface ProveedorMapper {
    ProveedorResponse toResponse(Proveedor proveedor);

    @Mapping(target = "id", ignore = true)
    Proveedor toEntity(ProveedorRequest request);
}
