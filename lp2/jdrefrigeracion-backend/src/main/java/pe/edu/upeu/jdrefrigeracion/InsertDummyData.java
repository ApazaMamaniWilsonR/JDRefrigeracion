package pe.edu.upeu.jdrefrigeracion;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.jdrefrigeracion.inventario.item.entity.Inventario;
import pe.edu.upeu.jdrefrigeracion.inventario.item.repository.InventarioRepository;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.entity.Proveedor;
import pe.edu.upeu.jdrefrigeracion.proveedores.proveedor.repository.ProveedorRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class InsertDummyData {

    private final InventarioRepository inventarioRepository;
    private final ProveedorRepository proveedorRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (inventarioRepository.count() == 0) {
            log.info("Insertando datos dummy de Inventario...");
            Inventario i1 = new Inventario();
            i1.setNombre("Tubería de cobre 1/4");
            i1.setDescripcion("Rollo de 15 metros");
            i1.setUnidadMedida("Rollo");
            i1.setPrecio(new BigDecimal("120.00"));
            i1.setStock(10);
            i1.setStockMinimo(5);

            Inventario i2 = new Inventario();
            i2.setNombre("Gas Refrigerante R410A");
            i2.setDescripcion("Balón de 11.3 kg");
            i2.setUnidadMedida("Balón");
            i2.setPrecio(new BigDecimal("350.00"));
            i2.setStock(3);
            i2.setStockMinimo(5); // Stock bajo para probar alertas

            inventarioRepository.saveAll(List.of(i1, i2));
        }

        if (proveedorRepository.count() == 0) {
            log.info("Insertando datos dummy de Proveedores...");
            Proveedor p1 = new Proveedor();
            p1.setRuc("20123456789");
            p1.setRazonSocial("Cold Import SAC");
            p1.setDireccion("Av. Argentina 123, Lima");
            p1.setTelefono("987654321");
            p1.setEmail("ventas@coldimport.pe");

            Proveedor p2 = new Proveedor();
            p2.setRuc("20987654321");
            p2.setRazonSocial("Refrimarket Peru");
            p2.setDireccion("Calle Los Pinos 456, San Isidro");
            p2.setTelefono("912345678");
            p2.setEmail("contacto@refrimarket.pe");

            proveedorRepository.saveAll(List.of(p1, p2));
        }
        log.info("Datos dummy listos.");
    }
}
