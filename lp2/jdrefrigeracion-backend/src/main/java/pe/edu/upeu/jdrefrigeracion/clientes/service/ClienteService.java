package pe.edu.upeu.jdrefrigeracion.clientes.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ClienteRequest;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ClienteResponse;
import pe.edu.upeu.jdrefrigeracion.clientes.entity.Cliente;
import pe.edu.upeu.jdrefrigeracion.clientes.repository.ClienteRepository;
import pe.edu.upeu.jdrefrigeracion.common.exception.RecursoNoEncontradoException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = buscarEntidadPorId(id);
        return toResponse(cliente);
    }

    public Cliente buscarEntidadPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (clienteRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new IllegalStateException("Ya existe un cliente con el documento: " + request.numeroDocumento());
        }

        Cliente cliente = Cliente.builder()
                .tipoCliente(request.tipoCliente())
                .numeroDocumento(request.numeroDocumento())
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .razonSocial(request.razonSocial())
                .nombreComercial(generarNombreComercial(request))
                .direccion(request.direccion())
                .estadoContribuyente(request.estadoContribuyente())
                .condicionContribuyente(request.condicionContribuyente())
                .correo(request.correo())
                .telefono(request.telefono())
                .personaContacto(request.personaContacto())
                .activo(true)
                .build();

        Cliente clienteGuardado = clienteRepository.save(cliente);

        return toResponse(clienteGuardado);
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarEntidadPorId(id);

        if (!cliente.getNumeroDocumento().equals(request.numeroDocumento())
                && clienteRepository.existsByNumeroDocumento(request.numeroDocumento())) {
            throw new IllegalStateException("Ya existe otro cliente con el documento: " + request.numeroDocumento());
        }

        cliente.setTipoCliente(request.tipoCliente());
        cliente.setNumeroDocumento(request.numeroDocumento());
        cliente.setNombres(request.nombres());
        cliente.setApellidos(request.apellidos());
        cliente.setRazonSocial(request.razonSocial());
        cliente.setNombreComercial(generarNombreComercial(request));
        cliente.setDireccion(request.direccion());
        cliente.setEstadoContribuyente(request.estadoContribuyente());
        cliente.setCondicionContribuyente(request.condicionContribuyente());
        cliente.setCorreo(request.correo());
        cliente.setTelefono(request.telefono());
        cliente.setPersonaContacto(request.personaContacto());

        Cliente clienteActualizado = clienteRepository.save(cliente);

        return toResponse(clienteActualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarEntidadPorId(id);
        clienteRepository.delete(cliente);
    }

    private String generarNombreComercial(ClienteRequest request) {
        if (request.razonSocial() != null && !request.razonSocial().isBlank()) {
            return request.razonSocial();
        }

        String nombres = request.nombres() == null ? "" : request.nombres().trim();
        String apellidos = request.apellidos() == null ? "" : request.apellidos().trim();

        String nombreCompleto = (nombres + " " + apellidos).trim();

        if (nombreCompleto.isBlank()) {
            return "Cliente sin nombre";
        }

        return nombreCompleto;
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getTipoCliente(),
                cliente.getNumeroDocumento(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getRazonSocial(),
                cliente.getNombreComercial(),
                cliente.getDireccion(),
                cliente.getEstadoContribuyente(),
                cliente.getCondicionContribuyente(),
                cliente.getCorreo(),
                cliente.getTelefono(),
                cliente.getPersonaContacto(),
                cliente.getActivo()
        );
    }
}