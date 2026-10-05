package pe.edu.upeu.jdrefrigeracion.ventas.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.clientes.entity.Cliente;
import pe.edu.upeu.jdrefrigeracion.clientes.service.ClienteService;
import pe.edu.upeu.jdrefrigeracion.common.exception.RecursoNoEncontradoException;
import pe.edu.upeu.jdrefrigeracion.inventario.entity.Producto;
import pe.edu.upeu.jdrefrigeracion.inventario.service.ProductoService;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.DetalleVentaRequest;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.DetalleVentaResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaRequest;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Cotizacion;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.DetalleCotizacion;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.DetalleVenta;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoCotizacion;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Venta;
import pe.edu.upeu.jdrefrigeracion.ventas.repository.VentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private static final BigDecimal IGV = new BigDecimal("0.18");

    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final CotizacionService cotizacionService;

    public List<VentaResponse> listar() {
        return ventaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VentaResponse> listarPorCliente(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Venta buscarEntidadPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con id: " + id));
    }

    public VentaResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadPorId(id));
    }

    @Transactional
    public VentaResponse registrar(VentaRequest request) {
        Cliente cliente = clienteService.buscarEntidadPorId(request.clienteId());

        BigDecimal descuento = request.descuento() == null
                ? BigDecimal.ZERO
                : request.descuento();

        Venta venta = Venta.builder()
                .fecha(LocalDateTime.now())
                .estado(EstadoVenta.REGISTRADA)
                .moneda(request.moneda())
                .tipoCambio(request.tipoCambio())
                .cliente(cliente)
                .subtotal(BigDecimal.ZERO)
                .descuento(descuento)
                .igv(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .totalSoles(BigDecimal.ZERO)
                .totalDolares(BigDecimal.ZERO)
                .detalles(new ArrayList<>())
                .build();

        BigDecimal subtotalVenta = BigDecimal.ZERO;

        for (DetalleVentaRequest detalleRequest : request.detalles()) {
            Producto producto = productoService.buscarEntidadPorId(detalleRequest.productoId());

            if (producto.getStock() < detalleRequest.cantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            BigDecimal precioUnitario = obtenerPrecioSegunMoneda(
                    producto.getPrecio(),
                    request.moneda(),
                    request.tipoCambio()
            );

            BigDecimal subtotalDetalle = precioUnitario
                    .multiply(BigDecimal.valueOf(detalleRequest.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleVenta detalleVenta = DetalleVenta.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(detalleRequest.cantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotalDetalle)
                    .build();

            venta.getDetalles().add(detalleVenta);
            subtotalVenta = subtotalVenta.add(subtotalDetalle);

            productoService.descontarStock(producto.getId(), detalleRequest.cantidad());
        }

        BigDecimal baseImponible = subtotalVenta.subtract(descuento);

        if (baseImponible.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser mayor que el subtotal");
        }

        BigDecimal igv = baseImponible
                .multiply(IGV)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = baseImponible
                .add(igv)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalSoles;
        BigDecimal totalDolares;

        if (request.moneda() == Moneda.PEN) {
            totalSoles = total;
            totalDolares = total.divide(request.tipoCambio(), 2, RoundingMode.HALF_UP);
        } else {
            totalDolares = total;
            totalSoles = total.multiply(request.tipoCambio()).setScale(2, RoundingMode.HALF_UP);
        }

        venta.setSubtotal(subtotalVenta.setScale(2, RoundingMode.HALF_UP));
        venta.setDescuento(descuento.setScale(2, RoundingMode.HALF_UP));
        venta.setIgv(igv);
        venta.setTotal(total);
        venta.setTotalSoles(totalSoles);
        venta.setTotalDolares(totalDolares);

        Venta ventaGuardada = ventaRepository.save(venta);

        return toResponse(ventaGuardada);
    }

    @Transactional
    public VentaResponse convertirCotizacionAVenta(Long cotizacionId) {
        Cotizacion cotizacion = cotizacionService.buscarEntidadPorId(cotizacionId);

        if (cotizacion.getEstado() == EstadoCotizacion.RECHAZADA) {
            throw new IllegalStateException("No se puede convertir una cotización rechazada en venta");
        }

        if (cotizacion.getEstado() == EstadoCotizacion.CONVERTIDA) {
            throw new IllegalStateException("La cotización ya fue convertida en venta");
        }

        if (cotizacion.getEstado() != EstadoCotizacion.APROBADA) {
            throw new IllegalStateException("Solo una cotización aprobada puede convertirse en venta");
        }

        Venta venta = Venta.builder()
                .fecha(LocalDateTime.now())
                .estado(EstadoVenta.REGISTRADA)
                .moneda(cotizacion.getMoneda())
                .tipoCambio(cotizacion.getTipoCambio())
                .cliente(cotizacion.getCliente())
                .subtotal(cotizacion.getSubtotal())
                .descuento(cotizacion.getDescuento())
                .igv(cotizacion.getIgv())
                .total(cotizacion.getTotal())
                .totalSoles(cotizacion.getTotalSoles())
                .totalDolares(cotizacion.getTotalDolares())
                .detalles(new ArrayList<>())
                .build();

        for (DetalleCotizacion detalleCotizacion : cotizacion.getDetalles()) {
            Producto producto = detalleCotizacion.getProducto();

            if (producto.getStock() < detalleCotizacion.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            DetalleVenta detalleVenta = DetalleVenta.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(detalleCotizacion.getCantidad())
                    .precioUnitario(detalleCotizacion.getPrecioUnitario())
                    .subtotal(detalleCotizacion.getSubtotal())
                    .build();

            venta.getDetalles().add(detalleVenta);

            productoService.descontarStock(producto.getId(), detalleCotizacion.getCantidad());
        }

        Venta ventaGuardada = ventaRepository.save(venta);

        cotizacionService.marcarComoConvertida(cotizacionId);

        return toResponse(ventaGuardada);
    }

    public void marcarComoFacturada(Long ventaId) {
        Venta venta = buscarEntidadPorId(ventaId);
        venta.setEstado(EstadoVenta.FACTURADA);
        ventaRepository.save(venta);
    }

    @Transactional
    public void anular(Long ventaId) {
        Venta venta = buscarEntidadPorId(ventaId);
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalStateException("La venta ya está anulada");
        }
        venta.setEstado(EstadoVenta.ANULADA);
        
        for (DetalleVenta detalle : venta.getDetalles()) {
            productoService.aumentarStock(detalle.getProducto().getId(), detalle.getCantidad());
        }
        
        ventaRepository.save(venta);
    }

    private BigDecimal obtenerPrecioSegunMoneda(BigDecimal precioSoles, Moneda moneda, BigDecimal tipoCambio) {
        if (moneda == Moneda.PEN) {
            return precioSoles.setScale(2, RoundingMode.HALF_UP);
        }

        return precioSoles.divide(tipoCambio, 2, RoundingMode.HALF_UP);
    }

    private VentaResponse toResponse(Venta venta) {
        List<DetalleVentaResponse> detalles = venta.getDetalles()
                .stream()
                .map(detalle -> new DetalleVentaResponse(
                        detalle.getProducto().getId(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new VentaResponse(
                venta.getId(),
                venta.getFecha(),
                venta.getEstado(),
                venta.getMoneda(),
                venta.getTipoCambio(),
                venta.getCliente().getId(),
                venta.getCliente().getNombreComercial(),
                venta.getSubtotal(),
                venta.getDescuento(),
                venta.getIgv(),
                venta.getTotal(),
                venta.getTotalSoles(),
                venta.getTotalDolares(),
                detalles
        );
    }
}