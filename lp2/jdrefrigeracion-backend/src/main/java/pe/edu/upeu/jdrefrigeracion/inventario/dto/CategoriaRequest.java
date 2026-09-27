package pe.edu.upeu.jdrefrigeracion.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no debe superar 120 caracteres")
        String nombre,

        @Size(max = 250, message = "La descripción no debe superar 250 caracteres")
        String descripcion
) {
}