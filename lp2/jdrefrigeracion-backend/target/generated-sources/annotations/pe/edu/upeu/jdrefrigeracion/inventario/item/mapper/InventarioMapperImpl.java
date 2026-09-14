package pe.edu.upeu.jdrefrigeracion.inventario.item.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioRequest;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;
import pe.edu.upeu.jdrefrigeracion.inventario.item.entity.Inventario;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-13T23:55:01-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class InventarioMapperImpl implements InventarioMapper {

    @Override
    public InventarioResponse toResponse(Inventario inventario) {
        if ( inventario == null ) {
            return null;
        }

        InventarioResponse.InventarioResponseBuilder inventarioResponse = InventarioResponse.builder();

        inventarioResponse.id( inventario.getId() );
        inventarioResponse.nombre( inventario.getNombre() );
        inventarioResponse.descripcion( inventario.getDescripcion() );
        inventarioResponse.unidadMedida( inventario.getUnidadMedida() );
        inventarioResponse.precio( inventario.getPrecio() );
        inventarioResponse.stock( inventario.getStock() );
        inventarioResponse.stockMinimo( inventario.getStockMinimo() );

        return inventarioResponse.build();
    }

    @Override
    public Inventario toEntity(InventarioRequest request) {
        if ( request == null ) {
            return null;
        }

        Inventario inventario = new Inventario();

        inventario.setNombre( request.getNombre() );
        inventario.setDescripcion( request.getDescripcion() );
        inventario.setUnidadMedida( request.getUnidadMedida() );
        inventario.setPrecio( request.getPrecio() );
        inventario.setStock( request.getStock() );
        inventario.setStockMinimo( request.getStockMinimo() );

        return inventario;
    }
}
