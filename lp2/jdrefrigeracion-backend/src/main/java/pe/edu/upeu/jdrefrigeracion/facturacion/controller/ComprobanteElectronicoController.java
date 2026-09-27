package pe.edu.upeu.jdrefrigeracion.facturacion.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.facturacion.dto.ComprobanteRequest;
import pe.edu.upeu.jdrefrigeracion.facturacion.dto.ComprobanteResponse;
import pe.edu.upeu.jdrefrigeracion.facturacion.dto.EnvioClienteRequest;
import pe.edu.upeu.jdrefrigeracion.facturacion.service.ComprobanteElectronicoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comprobantes")
@RequiredArgsConstructor
public class ComprobanteElectronicoController {

    private final ComprobanteElectronicoService comprobanteService;

    @GetMapping
    public List<ComprobanteResponse> listar() {
        return comprobanteService.listar();
    }

    @GetMapping("/{id}")
    public ComprobanteResponse buscarPorId(@PathVariable Long id) {
        return comprobanteService.buscarPorId(id);
    }

    @GetMapping("/venta/{ventaId}")
    public ComprobanteResponse buscarPorVenta(@PathVariable Long ventaId) {
        return comprobanteService.buscarPorVenta(ventaId);
    }

    @PostMapping("/emitir")
    public ComprobanteResponse emitir(@Valid @RequestBody ComprobanteRequest request) {
        return comprobanteService.emitir(request);
    }

    @PatchMapping("/{id}/enviar-sunat")
    public ComprobanteResponse enviarSunat(@PathVariable Long id) {
        return comprobanteService.enviarSunat(id);
    }

    @PatchMapping("/{id}/verificar-sunat")
    public ComprobanteResponse verificarSunat(@PathVariable Long id) {
        return comprobanteService.verificarSunat(id);
    }

    @PatchMapping("/{id}/rechazar-sunat-simulado")
    public ComprobanteResponse rechazarSunatSimulado(@PathVariable Long id) {
        return comprobanteService.rechazarSunatSimulado(id);
    }

    @PatchMapping("/{id}/enviar-cliente")
    public ComprobanteResponse enviarCliente(
            @PathVariable Long id,
            @Valid @RequestBody EnvioClienteRequest request
    ) {
        return comprobanteService.enviarCliente(id, request);
    }
}