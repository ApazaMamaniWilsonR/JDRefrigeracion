package pe.edu.upeu.jdrefrigeracion.clientes.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCliente tipoCliente;

    @Column(nullable = false, length = 11, unique = true)
    private String numeroDocumento;

    @Column(length = 120)
    private String nombres;

    @Column(length = 120)
    private String apellidos;

    @Column(length = 200)
    private String razonSocial;

    @Column(nullable = false, length = 220)
    private String nombreComercial;

    @Column(length = 250)
    private String direccion;

    @Column(length = 80)
    private String estadoContribuyente;

    @Column(length = 80)
    private String condicionContribuyente;

    @Column(length = 150)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(length = 120)
    private String personaContacto;

    @Column(nullable = false)
    private Boolean activo = true;
}