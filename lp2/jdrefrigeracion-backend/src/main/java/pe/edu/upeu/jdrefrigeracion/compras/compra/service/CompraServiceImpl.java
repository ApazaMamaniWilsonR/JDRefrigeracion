package pe.edu.upeu.jdrefrigeracion.compras.compra.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.*;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.Compra;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.DetalleCompra;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.EstadoCompra;
import pe.edu.upeu.jdrefrigeracion.compras.compra.mapper.CompraMapper;
import pe.edu.upeu.jdrefrigeracion.compras.compra.repository.CompraRepository;
import pe.edu.upeu.jdrefrigeracion.exception.ResourceNotFoundException;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;
import pe.edu.upeu.jdrefrigeracion.inventario.item.service.InventarioService;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorResponse;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.service.ProveedorService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {
    private final CompraRepository compraRepository;
    private final InventarioService inventarioService;
    private final ProveedorService proveedorService;
    private final CompraMapper compraMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponse> buscar(EstadoCompra estado, LocalDateTime desde, LocalDateTime hasta,
                                       String ordenarPor, String direccion) {
        Sort.Direction dir = "ASC".equalsIgnoreCase(direccion) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(dir, ordenarPor);
        return compraRepository.buscar(estado, desde, hasta, sort).stream()
                .map(compraMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponse obtener(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada: " + id));
        return compraMapper.toResponse(compra);
    }

    @Override
    @Transactional
    public CompraResponse crear(CompraRequest request) {
        ProveedorResponse proveedor = proveedorService.obtener(request.getProveedorId());
        
        Compra compra = new Compra();
        compra.setFecha(LocalDateTime.now());
        compra.setEstado(EstadoCompra.REGISTRADA);
        compra.setProveedorId(proveedor.getId());
        compra.setProveedorNombre(proveedor.getRazonSocial());

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCompraRequest detalleRequest : request.getDetalles()) {
            InventarioResponse inventario = inventarioService.obtener(detalleRequest.getInventarioId());
            inventarioService.incrementarStock(detalleRequest.getInventarioId(), detalleRequest.getCantidad());

            DetalleCompra detalle = compraMapper.toDetalle(detalleRequest, inventario);
            detalle.setCompra(compra);
            compra.getDetalles().add(detalle);
            total = total.add(detalle.getSubtotal());
        }
        compra.setTotal(total);

        return compraMapper.toResponse(compraRepository.save(compra));
    }

    @Override
    @Transactional(readOnly = true)
    public CompraReporte reporte(EstadoCompra estado, LocalDateTime desde, LocalDateTime hasta) {
        CompraAgregado agregado = compraRepository.agregados(estado, desde, hasta);
        Sort sort = Sort.by(Sort.Direction.DESC, "fecha");
        List<CompraResumen> compras = compraRepository.buscarResumen(estado, desde, hasta, sort);
        return new CompraReporte(agregado, compras);
    }
}
