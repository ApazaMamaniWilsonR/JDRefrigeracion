package pe.edu.upeu.jdrefrigeracion.postventa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.ActualizarMantenimientoRequest;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.InstalacionRequest;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.InstalacionResponse;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.MantenimientoResponse;
import pe.edu.upeu.jdrefrigeracion.postventa.service.PostventaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/postventa")
@RequiredArgsConstructor
public class PostventaController {

    private final PostventaService postventaService;

    @PostMapping("/instalaciones")
    public InstalacionResponse registrarInstalacion(@Valid @RequestBody InstalacionRequest request) {
        return postventaService.registrarInstalacion(request);
    }

    @GetMapping("/instalaciones")
    public List<InstalacionResponse> listarInstalaciones() {
        return postventaService.listarInstalaciones();
    }

    @GetMapping("/instalaciones/{id}")
    public InstalacionResponse buscarInstalacion(@PathVariable Long id) {
        return postventaService.buscarInstalacionPorId(id);
    }

    @GetMapping("/instalaciones/venta/{ventaId}")
    public InstalacionResponse buscarInstalacionPorVenta(@PathVariable Long ventaId) {
        return postventaService.buscarInstalacionPorVenta(ventaId);
    }

    @GetMapping("/mantenimientos")
    public List<MantenimientoResponse> listarMantenimientos() {
        return postventaService.listarMantenimientos();
    }

    @GetMapping("/mantenimientos/pendientes")
    public List<MantenimientoResponse> listarMantenimientosPendientes() {
        return postventaService.listarMantenimientosPendientes();
    }

    @GetMapping("/mantenimientos/proximos")
    public List<MantenimientoResponse> listarMantenimientosProximos() {
        return postventaService.listarMantenimientosProximos();
    }

    @GetMapping("/mantenimientos/vencidos")
    public List<MantenimientoResponse> listarMantenimientosVencidos() {
        return postventaService.listarMantenimientosVencidos();
    }

    @PatchMapping("/mantenimientos/{id}")
    public MantenimientoResponse actualizarMantenimiento(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarMantenimientoRequest request
    ) {
        return postventaService.actualizarMantenimiento(id, request);
    }
}   