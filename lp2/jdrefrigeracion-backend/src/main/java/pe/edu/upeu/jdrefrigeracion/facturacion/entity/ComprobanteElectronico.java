package pe.edu.upeu.jdrefrigeracion.facturacion.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Moneda;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "comprobantes_electronicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComprobanteElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoComprobante tipoComprobante;

    @Column(nullable = false, length = 10)
    private String serie;

    @Column(nullable = false, length = 20)
    private String correlativo;

    @Column(nullable = false)
    private LocalDateTime fechaEmision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Moneda moneda;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal tipoCambio;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal igv;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSoles;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalDolares;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoSunat estadoSunat;

    @Column(length = 500)
    private String mensajeSunat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private EstadoEnvioCliente estadoEnvioCliente;

    @Column(length = 150)
    private String correoDestino;

    @Column(length = 20)
    private String telefonoDestino;

    @Column(length = 300)
    private String nombreArchivoXml;

    @Column(length = 300)
    private String nombreArchivoCdr;

    @Column(length = 300)
    private String nombreArchivoPdf;

    private LocalDateTime fechaEnvioSunat;

    private LocalDateTime fechaRespuestaSunat;

    private LocalDateTime fechaEnvioCliente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;
}