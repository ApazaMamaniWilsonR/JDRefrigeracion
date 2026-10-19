package pe.edu.upeu.jdrefrigeracion.ventas.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaReporte;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaRequest;
import pe.edu.upeu.jdrefrigeracion.ventas.dto.VentaResponse;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta;
import pe.edu.upeu.jdrefrigeracion.ventas.service.VentaService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<VentaResponse> listar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoVenta estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String vendedorId = esVendedor(jwt) ? jwt.getSubject() : null;
        return ventaService.listarConFiltros(clienteId, estado, desde, hasta, vendedorId);
    }

    @GetMapping("/resumen")
    public ResponseEntity<VentaReporte> resumen(
            @RequestParam(required = false) EstadoVenta estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        return ResponseEntity.ok(ventaService.resumen(estado, desde, hasta));
    }

    @GetMapping("/{id}")
    public VentaResponse buscarPorId(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String vendedorId = esVendedor(jwt) ? jwt.getSubject() : null;
        return ventaService.buscarPorId(id, vendedorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse registrar(@Valid @RequestBody VentaRequest request, @AuthenticationPrincipal Jwt jwt) {
        String vendedorId = jwt != null ? jwt.getSubject() : null;
        return ventaService.registrar(request, vendedorId);
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anularPatch(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anular(id));
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<VentaResponse> anularPost(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anular(id));
    }

    private boolean esVendedor(Jwt jwt) {
        if (jwt == null) {
            return false;
        }
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        Object roles = realmAccess == null ? null : realmAccess.get("roles");
        return roles instanceof Collection<?> lista && lista.contains("VENDEDOR");
    }
}