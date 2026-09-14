package pe.edu.upeu.jdrefrigeracion.inventario.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
}
