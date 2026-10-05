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
    public List<VentaResponse> listar(@RequestParam(required = false) Long clienteId) {
        if (clienteId != null) {
            return ventaService.listarPorCliente(clienteId);
        }
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    public VentaResponse buscarPorId(@PathVariable Long id) {
        return ventaService.buscarPorId(id);
    }

    @PostMapping
    public VentaResponse registrar(@Valid @RequestBody VentaRequest request) {
        return ventaService.registrar(request);
    }

    @PostMapping("/{id}/anular")
    public void anular(@PathVariable Long id) {
        ventaService.anular(id);
    }
}