package pe.edu.upeu.jdrefrigeracion.compras.compra.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponse {
    private Long id;
    private LocalDateTime fecha;
    private String estado;
    private BigDecimal total;
    private Long proveedorId;
    private String proveedorNombre;
    private List<DetalleCompraResponse> detalles;
}
