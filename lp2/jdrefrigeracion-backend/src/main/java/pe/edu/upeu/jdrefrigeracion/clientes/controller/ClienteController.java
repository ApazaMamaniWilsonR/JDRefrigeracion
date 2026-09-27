package pe.edu.upeu.jdrefrigeracion.clientes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ClienteRequest;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ClienteResponse;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ConsultaDocumentoResponse;
import pe.edu.upeu.jdrefrigeracion.clientes.service.ClienteService;
import pe.edu.upeu.jdrefrigeracion.clientes.service.ConsultaDocumentoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final ConsultaDocumentoService consultaDocumentoService;

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @PostMapping
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        return clienteService.crear(request);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
    }

    @GetMapping("/consultar-dni/{dni}")
    public ConsultaDocumentoResponse consultarDni(@PathVariable String dni) {
        return consultaDocumentoService.consultarDni(dni);
    }

    @GetMapping("/consultar-ruc/{ruc}")
    public ConsultaDocumentoResponse consultarRuc(@PathVariable String ruc) {
        return consultaDocumentoService.consultarRuc(ruc);
    }
}