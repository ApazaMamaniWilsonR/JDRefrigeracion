package pe.edu.upeu.jdrefrigeracion.compras.compra.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompraRequest {

    @NotNull
    private Long proveedorId;

    @NotEmpty
    @Valid
    private List<DetalleCompraRequest> detalles;
}
