package pe.edu.upeu.jdrefrigeracion.inventario.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioRequest;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;
import pe.edu.upeu.jdrefrigeracion.inventario.item.service.InventarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario")
@Slf4j
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    @Operation(summary = "Listar todos los ítems de inventario")
    public List<InventarioResponse> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un ítem de inventario por ID")
    public InventarioResponse obtener(@PathVariable Long id) {
        return inventarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo ítem de inventario")
    public InventarioResponse crear(@Valid @RequestBody InventarioRequest request) {
        return inventarioService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un ítem de inventario existente")
    public InventarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody InventarioRequest request) {
        return inventarioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un ítem de inventario")
    public void eliminar(@PathVariable Long id) {
        inventarioService.eliminar(id);
    }

    @GetMapping("/alertas-stock")
    @Operation(summary = "Obtener los ítems de inventario con stock por debajo del mínimo")
    public List<InventarioResponse> alertasStockBajo() {
        return inventarioService.alertasStockBajo();
    }
}
