package pe.edu.upeu.jdrefrigeracion.compras.compra.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraResponse;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.DetalleCompraRequest;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.DetalleCompraResponse;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.Compra;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.DetalleCompra;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-13T23:55:03-0500",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class CompraMapperImpl implements CompraMapper {

    @Override
    public DetalleCompra toDetalle(DetalleCompraRequest request, InventarioResponse inventario) {
        if ( request == null && inventario == null ) {
            return null;
        }

        DetalleCompra detalleCompra = new DetalleCompra();

        if ( request != null ) {
            detalleCompra.setCantidad( request.getCantidad() );
        }
        if ( inventario != null ) {
            detalleCompra.setInventarioId( inventario.getId() );
            detalleCompra.setNombreItem( inventario.getNombre() );
            detalleCompra.setPrecioUnitario( inventario.getPrecio() );
        }
        detalleCompra.setSubtotal( inventario.getPrecio().multiply(java.math.BigDecimal.valueOf(request.getCantidad())) );

        return detalleCompra;
    }

    @Override
    public CompraResponse toResponse(Compra compra) {
        if ( compra == null ) {
            return null;
        }

        CompraResponse.CompraResponseBuilder compraResponse = CompraResponse.builder();

        compraResponse.id( compra.getId() );
        compraResponse.fecha( compra.getFecha() );
        if ( compra.getEstado() != null ) {
            compraResponse.estado( compra.getEstado().name() );
        }
        compraResponse.total( compra.getTotal() );
        compraResponse.proveedorId( compra.getProveedorId() );
        compraResponse.proveedorNombre( compra.getProveedorNombre() );
        compraResponse.detalles( detalleCompraListToDetalleCompraResponseList( compra.getDetalles() ) );

        return compraResponse.build();
    }

    @Override
    public DetalleCompraResponse toDetalleResponse(DetalleCompra detalle) {
        if ( detalle == null ) {
            return null;
        }

        DetalleCompraResponse.DetalleCompraResponseBuilder detalleCompraResponse = DetalleCompraResponse.builder();

        detalleCompraResponse.inventarioId( detalle.getInventarioId() );
        detalleCompraResponse.nombreItem( detalle.getNombreItem() );
        detalleCompraResponse.precioUnitario( detalle.getPrecioUnitario() );
        detalleCompraResponse.cantidad( detalle.getCantidad() );
        detalleCompraResponse.subtotal( detalle.getSubtotal() );

        return detalleCompraResponse.build();
    }

    protected List<DetalleCompraResponse> detalleCompraListToDetalleCompraResponseList(List<DetalleCompra> list) {
        if ( list == null ) {
            return null;
        }

        List<DetalleCompraResponse> list1 = new ArrayList<DetalleCompraResponse>( list.size() );
        for ( DetalleCompra detalleCompra : list ) {
            list1.add( toDetalleResponse( detalleCompra ) );
        }

        return list1;
    }
}
