package pe.edu.upeu.jdrefrigeracion.compras.compra.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraReporte;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraRequest;
import pe.edu.upeu.jdrefrigeracion.compras.compra.dto.CompraResponse;
import pe.edu.upeu.jdrefrigeracion.compras.compra.entity.EstadoCompra;
import pe.edu.upeu.jdrefrigeracion.compras.compra.service.CompraService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/compras")
@Tag(name = "Compras")
@Slf4j
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    @Operation(summary = "Listar compras con filtros y ordenamiento opcionales")
    public List<CompraResponse> buscar(
            @RequestParam(required = false) EstadoCompra estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "fecha") String ordenarPor,
            @RequestParam(defaultValue = "DESC") String direccion) {
        return compraService.buscar(estado, desde, hasta, ordenarPor, direccion);
    }

    @GetMapping("/resumen")
    @Operation(summary = "Obtener reporte agregado de compras")
    public CompraReporte reporte(
            @RequestParam(required = false) EstadoCompra estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return compraService.reporte(estado, desde, hasta);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una compra por ID")
    public CompraResponse obtener(@PathVariable Long id) {
        return compraService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar una nueva compra (incrementa stock)")
    public CompraResponse crear(@Valid @RequestBody CompraRequest request) {
        return compraService.crear(request);
    }
}
