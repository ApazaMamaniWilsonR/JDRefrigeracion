package pe.edu.upeu.jdrefrigeracion.inventario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.common.exception.RecursoNoEncontradoException;
import pe.edu.upeu.jdrefrigeracion.inventario.dto.ProductoRequest;
import pe.edu.upeu.jdrefrigeracion.inventario.dto.ProductoResponse;
import pe.edu.upeu.jdrefrigeracion.inventario.entity.Categoria;
import pe.edu.upeu.jdrefrigeracion.inventario.entity.Producto;
import pe.edu.upeu.jdrefrigeracion.inventario.repository.ProductoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public List<ProductoResponse> listar() {
        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Producto buscarEntidadPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    public ProductoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadPorId(id));
    }

    public ProductoResponse crear(ProductoRequest request) {
        Categoria categoria = categoriaService.buscarPorId(request.categoriaId());

        Producto producto = Producto.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .stock(request.stock())
                .stockMinimo(request.stockMinimo())
                .categoria(categoria)
                .activo(true)
                .build();

        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarEntidadPorId(id);
        Categoria categoria = categoriaService.buscarPorId(request.categoriaId());

        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setStockMinimo(request.stockMinimo());
        producto.setCategoria(categoria);

        return toResponse(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = buscarEntidadPorId(id);
        productoRepository.delete(producto);
    }

    public void descontarStock(Long productoId, Integer cantidad) {
        Producto producto = buscarEntidadPorId(productoId);

        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getStockMinimo(),
                producto.getCategoria().getNombre(),
                producto.getActivo()
        );
    }
}