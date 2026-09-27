package pe.edu.upeu.jdrefrigeracion.ventas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.CotizacionRequest;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.CotizacionResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.service.CotizacionService;
import pe.edu.upeu.jdrefrigeracion.ventas.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final VentaService ventaService;

    @GetMapping
    public List<CotizacionResponse> listar() {
        return cotizacionService.listar();
    }

    @GetMapping("/{id}")
    public CotizacionResponse buscarPorId(@PathVariable Long id) {
        return cotizacionService.buscarPorId(id);
    }

    @PostMapping
    public CotizacionResponse crear(@Valid @RequestBody CotizacionRequest request) {
        return cotizacionService.crear(request);
    }

    @PatchMapping("/{id}/enviar")
    public CotizacionResponse enviar(@PathVariable Long id) {
        return cotizacionService.marcarComoEnviada(id);
    }

    @PatchMapping("/{id}/aprobar")
    public CotizacionResponse aprobar(@PathVariable Long id) {
        return cotizacionService.aprobar(id);
    }

    @PatchMapping("/{id}/rechazar")
    public CotizacionResponse rechazar(@PathVariable Long id) {
        return cotizacionService.rechazar(id);
    }

    @PostMapping("/{id}/convertir-venta")
    public VentaResponse convertirAVenta(@PathVariable Long id) {
        return ventaService.convertirCotizacionAVenta(id);
    }
}