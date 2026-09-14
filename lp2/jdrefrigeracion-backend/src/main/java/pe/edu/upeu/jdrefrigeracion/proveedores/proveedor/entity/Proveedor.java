package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PROVEEDORES")
@Getter
@Setter
@NoArgsConstructor
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RUC", nullable = false, length = 11, unique = true)
    private String ruc;

    @Column(name = "RAZON_SOCIAL", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "DIRECCION", length = 255)
    private String direccion;

    @Column(name = "TELEFONO", length = 20)
    private String telefono;

    @Column(name = "EMAIL", length = 100)
    private String email;
}
