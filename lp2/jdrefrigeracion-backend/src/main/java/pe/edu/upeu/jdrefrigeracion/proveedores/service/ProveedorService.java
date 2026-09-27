package pe.edu.upeu.jdrefrigeracion.proveedores.service;
import pe.edu.upeu.jdrefrigeracion.proveedores.entity.Proveedor;
import pe.edu.upeu.jdrefrigeracion.proveedores.repository.ProveedorRepository;
import pe.edu.upeu.jdrefrigeracion.clientes.client.DocumentoApiClient;
import pe.edu.upeu.jdrefrigeracion.clientes.dto.ConsultaDocumentoResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final DocumentoApiClient DocumentoApiClient;

    public ProveedorService(ProveedorRepository proveedorRepository, DocumentoApiClient DocumentoApiClient) {
        this.proveedorRepository = proveedorRepository;
        this.DocumentoApiClient = DocumentoApiClient;
    }

    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    public Optional<Proveedor> buscarPorId(Long id) {
        return proveedorRepository.findById(id);
    }

    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    public void eliminar(Long id) {
        proveedorRepository.deleteById(id);
    }

    public Proveedor autocompletarPorRuc(String ruc) {
        Optional<Proveedor> existente = proveedorRepository.findByRuc(ruc);
        if (existente.isPresent()) {
            return existente.get();
        }

        ConsultaDocumentoResponse datos = DocumentoApiClient.consultarRuc(ruc);
        if (datos == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "No se encontraron datos para el RUC: " + ruc);
        }

        Proveedor nuevo = new Proveedor();
        nuevo.setTipo(Proveedor.TipoProveedor.EMPRESA);
        nuevo.setRuc(datos.numeroDocumento());
        nuevo.setNombreORazonSocial(datos.razonSocial());
        nuevo.setDireccion(datos.direccion());
        return nuevo;
    }

    public Proveedor autocompletarPorDni(String dni) {
        ConsultaDocumentoResponse datos = DocumentoApiClient.consultarDni(dni);
        if (datos == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "No se encontraron datos para el DNI: " + dni);
        }

        Proveedor nuevo = new Proveedor();
        nuevo.setTipo(Proveedor.TipoProveedor.PERSONA_NATURAL);
        nuevo.setDni(datos.numeroDocumento());
        nuevo.setNombreORazonSocial(datos.nombres() + " " + datos.apellidos());
        return nuevo;
    }
}

