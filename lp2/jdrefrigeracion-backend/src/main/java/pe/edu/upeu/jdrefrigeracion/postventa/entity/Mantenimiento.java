package pe.edu.upeu.jdrefrigeracion.postventa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mantenimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instalacion_id", nullable = false)
    private Instalacion instalacion;

    @Column(nullable = false)
    private LocalDate fechaProgramada;

    private LocalDate fechaRealizada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoMantenimiento estado;

    @Column(length = 500)
    private String observaciones;
}