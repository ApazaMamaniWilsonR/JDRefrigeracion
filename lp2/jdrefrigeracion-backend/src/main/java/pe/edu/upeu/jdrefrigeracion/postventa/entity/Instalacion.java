package pe.edu.upeu.jdrefrigeracion.postventa.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Venta;

import java.time.LocalDate;

@Entity
@Table(name = "instalaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instalacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false, unique = true)
    private Venta venta;

    @Column(nullable = false)
    private LocalDate fechaInstalacion;

    @Column(nullable = false)
    private LocalDate fechaFinGarantia;

    @Column(nullable = false)
    private LocalDate fechaProximoMantenimiento;

    @Column(length = 250)
    private String direccionInstalacion;

    @Column(length = 500)
    private String observaciones;
}