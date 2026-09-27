package pe.edu.upeu.jdrefrigeracion.notificaciones.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.notificaciones.dto.NotificacionRequest;
import pe.edu.upeu.jdrefrigeracion.notificaciones.dto.NotificacionResponse;
import pe.edu.upeu.jdrefrigeracion.notificaciones.service.NotificacionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public List<NotificacionResponse> listarTodas() {
        return notificacionService.listarTodas();
    }

    @GetMapping("/no-leidas")
    public List<NotificacionResponse> listarNoLeidas() {
        return notificacionService.listarNoLeidas();
    }

    @GetMapping("/contador")
    public long contarNoLeidas() {
        return notificacionService.contarNoLeidas();
    }

    @PostMapping
    public NotificacionResponse crear(@Valid @RequestBody NotificacionRequest request) {
        return notificacionService.crear(request);
    }

    @PatchMapping("/{id}/leer")
    public NotificacionResponse marcarComoLeida(@PathVariable Long id) {
        return notificacionService.marcarComoLeida(id);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
    }
}