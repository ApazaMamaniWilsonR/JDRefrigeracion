package pe.edu.upeu.jdrefrigeracion.facturacion.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.common.exception.RecursoNoEncontradoException;
import pe.edu.upeu.jdrefrigeracion.facturacion.dto.ComprobanteRequest;
import pe.edu.upeu.jdrefrigeracion.facturacion.dto.ComprobanteResponse;
import pe.edu.upeu.jdrefrigeracion.facturacion.dto.EnvioClienteRequest;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.ComprobanteElectronico;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.EstadoEnvioCliente;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.EstadoSunat;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.TipoComprobante;
import pe.edu.upeu.jdrefrigeracion.facturacion.repository.ComprobanteElectronicoRepository;
import pe.edu.upeu.jdrefrigeracion.notificaciones.dto.NotificacionRequest;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.TipoNotificacion;
import pe.edu.upeu.jdrefrigeracion.notificaciones.service.NotificacionService;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Venta;
import pe.edu.upeu.jdrefrigeracion.ventas.service.VentaService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComprobanteElectronicoService {

    private final ComprobanteElectronicoRepository comprobanteRepository;
    private final VentaService ventaService;
    private final NotificacionService notificacionService;

    public List<ComprobanteResponse> listar() {
        return comprobanteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ComprobanteResponse buscarPorId(Long id) {
        ComprobanteElectronico comprobante = obtenerComprobante(id);
        return toResponse(comprobante);
    }

    public ComprobanteResponse buscarPorVenta(Long ventaId) {
        ComprobanteElectronico comprobante = comprobanteRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe comprobante para la venta id: " + ventaId));

        return toResponse(comprobante);
    }

    @Transactional
    public ComprobanteResponse emitir(ComprobanteRequest request) {
        Venta venta = ventaService.buscarEntidadPorId(request.ventaId());

        if (venta.getEstado() == EstadoVenta.FACTURADA) {
            throw new IllegalStateException("La venta ya fue facturada");
        }

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new IllegalStateException("No se puede emitir comprobante de una venta anulada");
        }

        if (comprobanteRepository.existsByVentaId(venta.getId())) {
            throw new IllegalStateException("Ya existe un comprobante para esta venta");
        }

        String serie = generarSerie(request.tipoComprobante());
        String correlativo = String.format("%08d", venta.getId());
        String numeroComprobante = serie + "-" + correlativo;

        ComprobanteElectronico comprobante = ComprobanteElectronico.builder()
                .venta(venta)
                .tipoComprobante(request.tipoComprobante())
                .serie(serie)
                .correlativo(correlativo)
                .fechaEmision(LocalDateTime.now())
                .moneda(venta.getMoneda())
                .tipoCambio(venta.getTipoCambio())
                .subtotal(venta.getSubtotal())
                .descuento(venta.getDescuento())
                .igv(venta.getIgv())
                .total(venta.getTotal())
                .totalSoles(venta.getTotalSoles())
                .totalDolares(venta.getTotalDolares())
                .estadoSunat(EstadoSunat.GENERADO)
                .mensajeSunat("Comprobante generado en el sistema. Aún no fue enviado a SUNAT.")
                .estadoEnvioCliente(EstadoEnvioCliente.NO_ENVIADO)
                .correoDestino(venta.getCliente().getCorreo())
                .telefonoDestino(venta.getCliente().getTelefono())
                .nombreArchivoXml(numeroComprobante + ".xml")
                .nombreArchivoCdr(null)
                .nombreArchivoPdf(numeroComprobante + ".pdf")
                .fechaEnvioSunat(null)
                .fechaRespuestaSunat(null)
                .fechaEnvioCliente(null)
                .build();

        return toResponse(comprobanteRepository.save(comprobante));
    }

    @Transactional
    public ComprobanteResponse enviarSunat(Long comprobanteId) {
        ComprobanteElectronico comprobante = obtenerComprobante(comprobanteId);

        if (comprobante.getEstadoSunat() == EstadoSunat.ACEPTADO) {
            throw new IllegalStateException("El comprobante ya fue aceptado por SUNAT");
        }

        if (comprobante.getEstadoSunat() == EstadoSunat.RECHAZADO) {
            throw new IllegalStateException("El comprobante fue rechazado. Debe corregirse antes de reenviar");
        }

        if (comprobante.getEstadoSunat() == EstadoSunat.PENDIENTE_RESPUESTA) {
            throw new IllegalStateException("El comprobante ya está pendiente de respuesta de SUNAT");
        }

        comprobante.setEstadoSunat(EstadoSunat.PENDIENTE_RESPUESTA);
        comprobante.setFechaEnvioSunat(LocalDateTime.now());
        comprobante.setMensajeSunat("Comprobante enviado a SUNAT. Pendiente de respuesta.");

        return toResponse(comprobanteRepository.save(comprobante));
    }

    @Transactional
    public ComprobanteResponse verificarSunat(Long comprobanteId) {
        ComprobanteElectronico comprobante = obtenerComprobante(comprobanteId);

        if (comprobante.getEstadoSunat() != EstadoSunat.PENDIENTE_RESPUESTA) {
            throw new IllegalStateException("Solo se puede verificar un comprobante pendiente de respuesta");
        }

        String numeroComprobante = comprobante.getSerie() + "-" + comprobante.getCorrelativo();

        comprobante.setEstadoSunat(EstadoSunat.ACEPTADO);
        comprobante.setFechaRespuestaSunat(LocalDateTime.now());
        comprobante.setMensajeSunat("Comprobante aceptado por SUNAT - simulación MVP");
        comprobante.setNombreArchivoCdr("R-" + numeroComprobante + ".zip");

        ComprobanteElectronico comprobanteGuardado = comprobanteRepository.save(comprobante);

        ventaService.marcarComoFacturada(comprobante.getVenta().getId());

        notificacionService.crear(new NotificacionRequest(
                TipoNotificacion.COMPROBANTE_ACEPTADO,
                "Comprobante aceptado por SUNAT",
                "El comprobante " + numeroComprobante
                        + " del cliente "
                        + comprobante.getVenta().getCliente().getNombreComercial()
                        + " fue aceptado por SUNAT. Ya puede enviarse al cliente.",
                "COMPROBANTE",
                comprobanteGuardado.getId()
        ));

        return toResponse(comprobanteGuardado);
    }

    @Transactional
    public ComprobanteResponse rechazarSunatSimulado(Long comprobanteId) {
        ComprobanteElectronico comprobante = obtenerComprobante(comprobanteId);

        if (comprobante.getEstadoSunat() != EstadoSunat.PENDIENTE_RESPUESTA) {
            throw new IllegalStateException("Solo se puede rechazar un comprobante pendiente de respuesta");
        }

        comprobante.setEstadoSunat(EstadoSunat.RECHAZADO);
        comprobante.setFechaRespuestaSunat(LocalDateTime.now());
        comprobante.setMensajeSunat("Comprobante rechazado por SUNAT - simulación MVP. Revisar datos del cliente o comprobante.");
        comprobante.setNombreArchivoCdr(null);

        ComprobanteElectronico comprobanteGuardado = comprobanteRepository.save(comprobante);

        notificacionService.crear(new NotificacionRequest(
                TipoNotificacion.COMPROBANTE_RECHAZADO,
                "Comprobante rechazado por SUNAT",
                "El comprobante "
                        + comprobante.getSerie() + "-" + comprobante.getCorrelativo()
                        + " del cliente "
                        + comprobante.getVenta().getCliente().getNombreComercial()
                        + " fue rechazado por SUNAT. Revisar datos antes de reenviar.",
                "COMPROBANTE",
                comprobanteGuardado.getId()
        ));

        return toResponse(comprobanteGuardado);
    }

    @Transactional
    public ComprobanteResponse enviarCliente(Long comprobanteId, EnvioClienteRequest request) {
        ComprobanteElectronico comprobante = obtenerComprobante(comprobanteId);

        if (comprobante.getEstadoSunat() != EstadoSunat.ACEPTADO
                && comprobante.getEstadoSunat() != EstadoSunat.ACEPTADO_CON_OBSERVACIONES) {
            throw new IllegalStateException("Solo se puede enviar al cliente un comprobante aceptado por SUNAT");
        }

        if (request.medioEnvio() == EstadoEnvioCliente.NO_ENVIADO) {
            throw new IllegalArgumentException("Debe seleccionar un medio válido de envío al cliente");
        }

        comprobante.setEstadoEnvioCliente(request.medioEnvio());
        comprobante.setFechaEnvioCliente(LocalDateTime.now());

        if (request.medioEnvio() == EstadoEnvioCliente.ENVIADO_CORREO) {
            comprobante.setMensajeSunat("Comprobante aceptado por SUNAT y enviado al cliente por correo.");
        } else if (request.medioEnvio() == EstadoEnvioCliente.ENVIADO_WHATSAPP) {
            comprobante.setMensajeSunat("Comprobante aceptado por SUNAT y enviado al cliente por WhatsApp.");
        } else {
            comprobante.setMensajeSunat("Comprobante aceptado por SUNAT y enviado al cliente por correo y WhatsApp.");
        }

        return toResponse(comprobanteRepository.save(comprobante));
    }

    private ComprobanteElectronico obtenerComprobante(Long id) {
        return comprobanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Comprobante no encontrado con id: " + id));
    }

    private String generarSerie(TipoComprobante tipoComprobante) {
        if (tipoComprobante == TipoComprobante.FACTURA) {
            return "F001";
        }

        return "B001";
    }

    private ComprobanteResponse toResponse(ComprobanteElectronico comprobante) {
        String numeroComprobante = comprobante.getSerie() + "-" + comprobante.getCorrelativo();

        return new ComprobanteResponse(
                comprobante.getId(),
                comprobante.getVenta().getId(),
                comprobante.getVenta().getCliente().getNombreComercial(),
                comprobante.getTipoComprobante(),
                comprobante.getSerie(),
                comprobante.getCorrelativo(),
                numeroComprobante,
                comprobante.getFechaEmision(),
                comprobante.getMoneda(),
                comprobante.getTipoCambio(),
                comprobante.getSubtotal(),
                comprobante.getDescuento(),
                comprobante.getIgv(),
                comprobante.getTotal(),
                comprobante.getTotalSoles(),
                comprobante.getTotalDolares(),
                comprobante.getEstadoSunat(),
                comprobante.getMensajeSunat(),
                comprobante.getEstadoEnvioCliente(),
                comprobante.getCorreoDestino(),
                comprobante.getTelefonoDestino(),
                comprobante.getNombreArchivoXml(),
                comprobante.getNombreArchivoCdr(),
                comprobante.getNombreArchivoPdf(),
                comprobante.getFechaEnvioSunat(),
                comprobante.getFechaRespuestaSunat(),
                comprobante.getFechaEnvioCliente()
        );
    }
}