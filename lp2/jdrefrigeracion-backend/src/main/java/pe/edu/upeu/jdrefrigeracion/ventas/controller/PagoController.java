package pe.edu.upeu.jdrefrigeracion.ventas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.PagoRequest;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.PagoResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.ResumenPagoVentaResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.service.PagoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public PagoResponse registrar(@Valid @RequestBody PagoRequest request) {
        return pagoService.registrar(request);
    }

    @GetMapping("/venta/{ventaId}")
    public List<PagoResponse> listarPorVenta(@PathVariable Long ventaId) {
        return pagoService.listarPorVenta(ventaId);
    }

    @GetMapping("/venta/{ventaId}/resumen")
    public ResumenPagoVentaResponse resumenPorVenta(@PathVariable Long ventaId) {
        return pagoService.resumenPorVenta(ventaId);
    }
}