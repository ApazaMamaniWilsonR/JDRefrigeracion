package pe.edu.upeu.jdrefrigeracion.ventas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.clientes.entity.Cliente;
import pe.edu.upeu.jdrefrigeracion.clientes.service.ClienteService;
import pe.edu.upeu.jdrefrigeracion.common.exception.RecursoNoEncontradoException;
import pe.edu.upeu.jdrefrigeracion.inventario.entity.Producto;
import pe.edu.upeu.jdrefrigeracion.inventario.service.ProductoService;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.*;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.*;
import pe.edu.upeu.jdrefrigeracion.ventas.repository.CotizacionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private static final BigDecimal IGV = new BigDecimal("0.18");

    private final CotizacionRepository cotizacionRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    public List<CotizacionResponse> listar() {
        return cotizacionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Cotizacion buscarEntidadPorId(Long id) {
        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cotización no encontrada con id: " + id));
    }

    public CotizacionResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadPorId(id));
    }

    public CotizacionResponse crear(CotizacionRequest request) {
        Cliente cliente = clienteService.buscarEntidadPorId(request.clienteId());

        BigDecimal descuento = request.descuento() == null ? BigDecimal.ZERO : request.descuento();

        Cotizacion cotizacion = Cotizacion.builder()
                .fecha(LocalDateTime.now())
                .estado(EstadoCotizacion.BORRADOR)
                .moneda(request.moneda())
                .tipoCambio(request.tipoCambio())
                .cliente(cliente)
                .subtotal(BigDecimal.ZERO)
                .descuento(descuento)
                .igv(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .totalSoles(BigDecimal.ZERO)
                .totalDolares(BigDecimal.ZERO)
                .observaciones(request.observaciones())
                .detalles(new ArrayList<>())
                .build();

        BigDecimal subtotalCotizacion = BigDecimal.ZERO;

        for (DetalleCotizacionRequest detalleRequest : request.detalles()) {
            Producto producto = productoService.buscarEntidadPorId(detalleRequest.productoId());

            BigDecimal precioUnitario = obtenerPrecioSegunMoneda(producto.getPrecio(), request.moneda(), request.tipoCambio());

            BigDecimal subtotalDetalle = precioUnitario.multiply(BigDecimal.valueOf(detalleRequest.cantidad()));

            DetalleCotizacion detalle = DetalleCotizacion.builder()
                    .cotizacion(cotizacion)
                    .producto(producto)
                    .cantidad(detalleRequest.cantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotalDetalle)
                    .build();

            cotizacion.getDetalles().add(detalle);
            subtotalCotizacion = subtotalCotizacion.add(subtotalDetalle);
        }

        BigDecimal baseImponible = subtotalCotizacion.subtract(descuento);

        if (baseImponible.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento no puede ser mayor que el subtotal");
        }

        BigDecimal igv = baseImponible.multiply(IGV).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = baseImponible.add(igv).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalSoles;
        BigDecimal totalDolares;

        if (request.moneda() == Moneda.PEN) {
            totalSoles = total;
            totalDolares = total.divide(request.tipoCambio(), 2, RoundingMode.HALF_UP);
        } else {
            totalDolares = total;
            totalSoles = total.multiply(request.tipoCambio()).setScale(2, RoundingMode.HALF_UP);
        }

        cotizacion.setSubtotal(subtotalCotizacion.setScale(2, RoundingMode.HALF_UP));
        cotizacion.setIgv(igv);
        cotizacion.setTotal(total);
        cotizacion.setTotalSoles(totalSoles);
        cotizacion.setTotalDolares(totalDolares);

        return toResponse(cotizacionRepository.save(cotizacion));
    }

    public CotizacionResponse marcarComoEnviada(Long id) {
        Cotizacion cotizacion = buscarEntidadPorId(id);

        if (cotizacion.getEstado() == EstadoCotizacion.RECHAZADA) {
            throw new IllegalStateException("No se puede enviar una cotización rechazada");
        }

        cotizacion.setEstado(EstadoCotizacion.ENVIADA);
        return toResponse(cotizacionRepository.save(cotizacion));
    }

    public CotizacionResponse aprobar(Long id) {
        Cotizacion cotizacion = buscarEntidadPorId(id);

        if (cotizacion.getEstado() == EstadoCotizacion.RECHAZADA) {
            throw new IllegalStateException("No se puede aprobar una cotización rechazada");
        }

        if (cotizacion.getEstado() == EstadoCotizacion.CONVERTIDA) {
            throw new IllegalStateException("La cotización ya fue convertida en venta");
        }

        cotizacion.setEstado(EstadoCotizacion.APROBADA);
        return toResponse(cotizacionRepository.save(cotizacion));
    }

    public CotizacionResponse rechazar(Long id) {
        Cotizacion cotizacion = buscarEntidadPorId(id);

        if (cotizacion.getEstado() == EstadoCotizacion.CONVERTIDA) {
            throw new IllegalStateException("No se puede rechazar una cotización ya convertida en venta");
        }

        cotizacion.setEstado(EstadoCotizacion.RECHAZADA);
        return toResponse(cotizacionRepository.save(cotizacion));
    }

    public void marcarComoConvertida(Long id) {
        Cotizacion cotizacion = buscarEntidadPorId(id);

        if (cotizacion.getEstado() != EstadoCotizacion.APROBADA) {
            throw new IllegalStateException("Solo una cotización aprobada puede convertirse en venta");
        }

        cotizacion.setEstado(EstadoCotizacion.CONVERTIDA);
        cotizacionRepository.save(cotizacion);
    }

    private BigDecimal obtenerPrecioSegunMoneda(BigDecimal precioSoles, Moneda moneda, BigDecimal tipoCambio) {
        if (moneda == Moneda.PEN) {
            return precioSoles.setScale(2, RoundingMode.HALF_UP);
        }

        return precioSoles.divide(tipoCambio, 2, RoundingMode.HALF_UP);
    }

    private CotizacionResponse toResponse(Cotizacion cotizacion) {
        List<DetalleCotizacionResponse> detalles = cotizacion.getDetalles()
                .stream()
                .map(detalle -> new DetalleCotizacionResponse(
                        detalle.getProducto().getId(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new CotizacionResponse(
                cotizacion.getId(),
                cotizacion.getFecha(),
                cotizacion.getEstado(),
                cotizacion.getMoneda(),
                cotizacion.getTipoCambio(),
                cotizacion.getCliente().getId(),
                cotizacion.getCliente().getNombreComercial(),
                cotizacion.getSubtotal(),
                cotizacion.getDescuento(),
                cotizacion.getIgv(),
                cotizacion.getTotal(),
                cotizacion.getTotalSoles(),
                cotizacion.getTotalDolares(),
                cotizacion.getObservaciones(),
                detalles
        );
    }
}