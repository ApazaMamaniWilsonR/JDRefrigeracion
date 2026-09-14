package pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.jdrefrigeracion.exception.ResourceNotFoundException;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorRequest;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.dto.ProveedorResponse;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.entity.Proveedor;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.mapper.ProveedorMapper;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.repository.ProveedorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return proveedorRepository.findAll().stream()
                .map(proveedorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse obtener(Long id) {
        return proveedorMapper.toResponse(buscarOFallar(id));
    }

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        Proveedor proveedor = proveedorMapper.toEntity(request);
        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = buscarOFallar(id);
        proveedor.setRuc(request.getRuc());
        proveedor.setRazonSocial(request.getRazonSocial());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        return proveedorMapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        proveedorRepository.delete(buscarOFallar(id));
    }

    private Proveedor buscarOFallar(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id));
    }
}
