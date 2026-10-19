package pe.edu.upeu.jdrefrigeracion.ventas.dto;

import lombok.Getter;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class VentaResumen {
    private final Long id;
    private final LocalDateTime fecha;
    private final String estado;
    private final BigDecimal total;
    private final long cantidadDetalles;

    public VentaResumen(Long id, LocalDateTime fecha, EstadoVenta estado, BigDecimal total, long cantidadDetalles) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado != null ? estado.name() : null;
        this.total = total;
        this.cantidadDetalles = cantidadDetalles;
    }
}
