package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProveedorRequest {

    @NotBlank
    @Size(min = 11, max = 11)
    private String ruc;

    @NotBlank
    private String razonSocial;

    private String direccion;

    private String telefono;

    private String email;
}
