package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {
    private Long id;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
}
