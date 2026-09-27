package pe.edu.upeu.jdrefrigeracion.ventas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.PagoRequest;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.PagoResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.ResumenPagoVentaResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoPago;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Pago;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Venta;
import pe.edu.upeu.jdrefrigeracion.ventas.repository.PagoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final VentaService ventaService;

    public PagoResponse registrar(PagoRequest request) {
        Venta venta = ventaService.buscarEntidadPorId(request.ventaId());

        BigDecimal montoSoles;
        BigDecimal montoDolares;

        if (request.moneda() == Moneda.PEN) {
            montoSoles = request.monto().setScale(2, RoundingMode.HALF_UP);
            montoDolares = request.monto().divide(request.tipoCambio(), 2, RoundingMode.HALF_UP);
        } else {
            montoDolares = request.monto().setScale(2, RoundingMode.HALF_UP);
            montoSoles = request.monto().multiply(request.tipoCambio()).setScale(2, RoundingMode.HALF_UP);
        }

        Pago pago = Pago.builder()
                .venta(venta)
                .fechaPago(LocalDateTime.now())
                .moneda(request.moneda())
                .tipoCambio(request.tipoCambio())
                .monto(request.monto().setScale(2, RoundingMode.HALF_UP))
                .montoSoles(montoSoles)
                .montoDolares(montoDolares)
                .metodoPago(request.metodoPago())
                .observacion(request.observacion())
                .build();

        return toResponse(pagoRepository.save(pago));
    }

    public List<PagoResponse> listarPorVenta(Long ventaId) {
        return pagoRepository.findByVentaId(ventaId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ResumenPagoVentaResponse resumenPorVenta(Long ventaId) {
        Venta venta = ventaService.buscarEntidadPorId(ventaId);

        List<Pago> pagos = pagoRepository.findByVentaId(ventaId);

        BigDecimal totalPagadoSoles = pagos.stream()
                .map(Pago::getMontoSoles)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalPagadoDolares = pagos.stream()
                .map(Pago::getMontoDolares)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoPendienteSoles = venta.getTotalSoles()
                .subtract(totalPagadoSoles)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal saldoPendienteDolares = venta.getTotalDolares()
                .subtract(totalPagadoDolares)
                .setScale(2, RoundingMode.HALF_UP);

        if (saldoPendienteSoles.compareTo(BigDecimal.ZERO) < 0) {
            saldoPendienteSoles = BigDecimal.ZERO;
        }

        if (saldoPendienteDolares.compareTo(BigDecimal.ZERO) < 0) {
            saldoPendienteDolares = BigDecimal.ZERO;
        }

        EstadoPago estadoPago;

        if (totalPagadoSoles.compareTo(BigDecimal.ZERO) == 0) {
            estadoPago = EstadoPago.PENDIENTE;
        } else if (saldoPendienteSoles.compareTo(BigDecimal.ZERO) == 0) {
            estadoPago = EstadoPago.PAGADO;
        } else {
            estadoPago = EstadoPago.PARCIAL;
        }

        List<PagoResponse> pagosResponse = pagos.stream()
                .map(this::toResponse)
                .toList();

        return new ResumenPagoVentaResponse(
                venta.getId(),
                venta.getMoneda(),
                venta.getTotal(),
                venta.getTotalSoles(),
                venta.getTotalDolares(),
                totalPagadoSoles,
                totalPagadoDolares,
                saldoPendienteSoles,
                saldoPendienteDolares,
                estadoPago,
                pagosResponse
        );
    }

    private PagoResponse toResponse(Pago pago) {
        return new PagoResponse(
                pago.getId(),
                pago.getVenta().getId(),
                pago.getFechaPago(),
                pago.getMoneda(),
                pago.getTipoCambio(),
                pago.getMonto(),
                pago.getMontoSoles(),
                pago.getMontoDolares(),
                pago.getMetodoPago(),
                pago.getObservacion()
        );
    }
}