package pe.edu.upeu.jdrefrigeracion.inventario.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioRequest;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;
import pe.edu.upeu.jdrefrigeracion.inventario.item.entity.Inventario;

@Mapper(componentModel = "spring")
public interface InventarioMapper {
    InventarioResponse toResponse(Inventario inventario);

    @Mapping(target = "id", ignore = true)
    Inventario toEntity(InventarioRequest request);
}
