package pe.edu.upeu.jdrefrigeracion.compras.compra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraResponse;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.DetalleCompraRequest;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.DetalleCompraResponse;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.Compra;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.DetalleCompra;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;

@Mapper(componentModel = "spring")
public interface CompraMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "compra", ignore = true)
    @Mapping(target = "inventarioId", source = "inventario.id")
    @Mapping(target = "nombreItem", source = "inventario.nombre")
    @Mapping(target = "precioUnitario", source = "inventario.precio")
    @Mapping(target = "subtotal", expression = "java(inventario.getPrecio().multiply(java.math.BigDecimal.valueOf(request.getCantidad())))")
    DetalleCompra toDetalle(DetalleCompraRequest request, InventarioResponse inventario);

    CompraResponse toResponse(Compra compra);

    DetalleCompraResponse toDetalleResponse(DetalleCompra detalle);
}
