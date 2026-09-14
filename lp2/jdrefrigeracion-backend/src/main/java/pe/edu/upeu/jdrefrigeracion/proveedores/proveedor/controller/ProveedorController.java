package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorRequest;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorResponse;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.service.ProveedorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
@Tag(name = "Proveedores")
@Slf4j
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @Operation(summary = "Listar todos los proveedores")
    public List<ProveedorResponse> listar() {
        return proveedorService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un proveedor por ID")
    public ProveedorResponse obtener(@PathVariable Long id) {
        return proveedorService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo proveedor")
    public ProveedorResponse crear(@Valid @RequestBody ProveedorRequest request) {
        return proveedorService.crear(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un proveedor existente")
    public ProveedorResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorRequest request) {
        return proveedorService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un proveedor")
    public void eliminar(@PathVariable Long id) {
        proveedorService.eliminar(id);
    }
}
