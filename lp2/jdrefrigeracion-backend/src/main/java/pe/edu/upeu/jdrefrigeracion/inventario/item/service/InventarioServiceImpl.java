package pe.edu.upeu.jdrefrigeracion.inventario.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.jdrefrigeracion.exception.ResourceNotFoundException;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioRequest;
import pe.edu.upeu.jdrefrigeracion.inventario.item.dto.InventarioResponse;
import pe.edu.upeu.jdrefrigeracion.inventario.item.entity.Inventario;
import pe.edu.upeu.jdrefrigeracion.inventario.item.mapper.InventarioMapper;
import pe.edu.upeu.jdrefrigeracion.inventario.item.repository.InventarioRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponse> listar() {
        return inventarioRepository.findAll().stream()
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponse obtener(Long id) {
        return inventarioMapper.toResponse(buscarOFallar(id));
    }

    @Override
    @Transactional
    public InventarioResponse crear(InventarioRequest request) {
        Inventario inventario = inventarioMapper.toEntity(request);
        return inventarioMapper.toResponse(inventarioRepository.save(inventario));
    }

    @Override
    @Transactional
    public InventarioResponse actualizar(Long id, InventarioRequest request) {
        Inventario inventario = buscarOFallar(id);
        inventario.setNombre(request.getNombre());
        inventario.setDescripcion(request.getDescripcion());
        inventario.setUnidadMedida(request.getUnidadMedida());
        inventario.setPrecio(request.getPrecio());
        inventario.setStock(request.getStock());
        inventario.setStockMinimo(request.getStockMinimo());
        return inventarioMapper.toResponse(inventarioRepository.save(inventario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        inventarioRepository.delete(buscarOFallar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponse> alertasStockBajo() {
        return inventarioRepository.findAlertasStockBajo().stream()
                .map(inventarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementarStock(Long id, Integer cantidad) {
        Inventario inventario = buscarOFallar(id);
        inventario.setStock(inventario.getStock() + cantidad);
        inventarioRepository.save(inventario);
    }

    private Inventario buscarOFallar(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ítem de inventario no encontrado: " + id));
    }
}
