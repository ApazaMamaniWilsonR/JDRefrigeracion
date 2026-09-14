package pe.edu.upeu.jdrefrigeracion.compras.compra.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleCompraRequest {

    @NotNull
    private Long inventarioId;

    @NotNull
    @Positive
    private Integer cantidad;
}
