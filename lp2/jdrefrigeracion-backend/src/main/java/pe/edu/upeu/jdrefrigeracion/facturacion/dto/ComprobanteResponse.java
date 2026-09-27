package pe.edu.upeu.jdrefrigeracion.facturacion.dto;

import pe.edu.upeu.jdrefrigeracion.facturacion.entity.EstadoEnvioCliente;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.EstadoSunat;
import pe.edu.upeu.jdrefrigeracion.facturacion.entity.TipoComprobante;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ComprobanteResponse(
        Long id,
        Long ventaId,
        String cliente,
        TipoComprobante tipoComprobante,
        String serie,
        String correlativo,
        String numeroComprobante,
        LocalDateTime fechaEmision,
        Moneda moneda,
        BigDecimal tipoCambio,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal igv,
        BigDecimal total,
        BigDecimal totalSoles,
        BigDecimal totalDolares,
        EstadoSunat estadoSunat,
        String mensajeSunat,
        EstadoEnvioCliente estadoEnvioCliente,
        String correoDestino,
        String telefonoDestino,
        String nombreArchivoXml,
        String nombreArchivoCdr,
        String nombreArchivoPdf,
        LocalDateTime fechaEnvioSunat,
        LocalDateTime fechaRespuestaSunat,
        LocalDateTime fechaEnvioCliente
) {
}