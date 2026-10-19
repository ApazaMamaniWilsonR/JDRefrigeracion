package pe.edu.upeu.jdrefrigeracion.ventas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaRequest;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<VentaResponse> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta estado,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime desde,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime hasta
    ) {
        return ventaService.listarConFiltros(clienteId, estado, desde, hasta);
    }

    @GetMapping("/{id}")
    public VentaResponse buscarPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id);
    }

    @PostMapping
    public VentaResponse registrar(@Valid @RequestBody VentaRequest request) {
        return ventaService.registrar(request);
    }

    @PatchMapping("/{id}/anular")
    public org.springframework.http.ResponseEntity<VentaResponse> anularPatch(@PathVariable Long id) {
        return org.springframework.http.ResponseEntity.ok(ventaService.anular(id));
    }

    @PostMapping("/{id}/anular")
    public org.springframework.http.ResponseEntity<VentaResponse> anularPost(@PathVariable Long id) {
        return org.springframework.http.ResponseEntity.ok(ventaService.anular(id));
    }
}