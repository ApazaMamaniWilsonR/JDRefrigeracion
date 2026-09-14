package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorRequest;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorResponse;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.entity.Proveedor;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-13T23:55:03-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class ProveedorMapperImpl implements ProveedorMapper {

    @Override
    public ProveedorResponse toResponse(Proveedor proveedor) {
        if ( proveedor == null ) {
            return null;
        }

        ProveedorResponse.ProveedorResponseBuilder proveedorResponse = ProveedorResponse.builder();

        proveedorResponse.id( proveedor.getId() );
        proveedorResponse.ruc( proveedor.getRuc() );
        proveedorResponse.razonSocial( proveedor.getRazonSocial() );
        proveedorResponse.direccion( proveedor.getDireccion() );
        proveedorResponse.telefono( proveedor.getTelefono() );
        proveedorResponse.email( proveedor.getEmail() );

        return proveedorResponse.build();
    }

    @Override
    public Proveedor toEntity(ProveedorRequest request) {
        if ( request == null ) {
            return null;
        }

        Proveedor proveedor = new Proveedor();

        proveedor.setRuc( request.getRuc() );
        proveedor.setRazonSocial( request.getRazonSocial() );
        proveedor.setDireccion( request.getDireccion() );
        proveedor.setTelefono( request.getTelefono() );
        proveedor.setEmail( request.getEmail() );

        return proveedor;
    }
}
