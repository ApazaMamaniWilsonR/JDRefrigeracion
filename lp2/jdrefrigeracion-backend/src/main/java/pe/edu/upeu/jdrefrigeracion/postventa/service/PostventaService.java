package pe.edu.upeu.jdrefrigeracion.postventa.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.jdrefrigeracion.common.exception.RecursoNoEncontradoException;
import pe.edu.upeu.jdrefrigeracion.notificaciones.dto.NotificacionRequest;
import pe.edu.upeu.jdrefrigeracion.notificaciones.entity.TipoNotificacion;
import pe.edu.upeu.jdrefrigeracion.notificaciones.service.NotificacionService;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.ActualizarMantenimientoRequest;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.InstalacionRequest;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.InstalacionResponse;
import pe.edu.upeu.jdrefrigeracion.postventa.dto.MantenimientoResponse;
import pe.edu.upeu.jdrefrigeracion.postventa.entity.EstadoMantenimiento;
import pe.edu.upeu.jdrefrigeracion.postventa.entity.Instalacion;
import pe.edu.upeu.jdrefrigeracion.postventa.entity.Mantenimiento;
import pe.edu.upeu.jdrefrigeracion.postventa.repository.InstalacionRepository;
import pe.edu.upeu.jdrefrigeracion.postventa.repository.MantenimientoRepository;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.EstadoVenta;
import pe.edu.upeu.jdrefrigeracion.ventas.entity.Venta;
import pe.edu.upeu.jdrefrigeracion.ventas.service.VentaService;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostventaService {

    private final InstalacionRepository instalacionRepository;
    private final MantenimientoRepository mantenimientoRepository;
    private final VentaService ventaService;
    private final NotificacionService notificacionService;

    @Transactional
    public InstalacionResponse registrarInstalacion(InstalacionRequest request) {
        Venta venta = ventaService.buscarEntidadPorId(request.ventaId());

        if (venta.getEstado() != EstadoVenta.FACTURADA) {
            throw new IllegalStateException("Solo se puede registrar instalación de una venta facturada");
        }

        if (instalacionRepository.existsByVentaId(venta.getId())) {
            throw new IllegalStateException("Esta venta ya tiene una instalación registrada");
        }

        LocalDate fechaFinGarantia = request.fechaInstalacion().plusYears(1);
        LocalDate fechaProximoMantenimiento = request.fechaInstalacion().plusMonths(3);

        Instalacion instalacion = Instalacion.builder()
                .venta(venta)
                .fechaInstalacion(request.fechaInstalacion())
                .fechaFinGarantia(fechaFinGarantia)
                .fechaProximoMantenimiento(fechaProximoMantenimiento)
                .direccionInstalacion(request.direccionInstalacion())
                .observaciones(request.observaciones())
                .build();

        Instalacion instalacionGuardada = instalacionRepository.save(instalacion);

        Mantenimiento primerMantenimiento = Mantenimiento.builder()
                .instalacion(instalacionGuardada)
                .fechaProgramada(fechaProximoMantenimiento)
                .fechaRealizada(null)
                .estado(EstadoMantenimiento.PENDIENTE)
                .observaciones("Primer mantenimiento programado automáticamente cada 3 meses.")
                .build();

        Mantenimiento mantenimientoGuardado = mantenimientoRepository.save(primerMantenimiento);

        notificacionService.crear(new NotificacionRequest(
                TipoNotificacion.MANTENIMIENTO_PROXIMO,
                "Mantenimiento programado",
                "La venta N° " + venta.getId()
                        + " del cliente " + venta.getCliente().getNombreComercial()
                        + " tiene mantenimiento programado para el "
                        + fechaProximoMantenimiento + ".",
                "MANTENIMIENTO",
                mantenimientoGuardado.getId()
        ));

        return toInstalacionResponse(instalacionGuardada);
    }

    public List<InstalacionResponse> listarInstalaciones() {
        return instalacionRepository.findAll()
                .stream()
                .map(this::toInstalacionResponse)
                .toList();
    }

    public InstalacionResponse buscarInstalacionPorId(Long id) {
        Instalacion instalacion = instalacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Instalación no encontrada con id: " + id));

        return toInstalacionResponse(instalacion);
    }

    public InstalacionResponse buscarInstalacionPorVenta(Long ventaId) {
        Instalacion instalacion = instalacionRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe instalación para la venta id: " + ventaId));

        return toInstalacionResponse(instalacion);
    }

    public List<MantenimientoResponse> listarMantenimientos() {
        return mantenimientoRepository.findAll()
                .stream()
                .map(this::toMantenimientoResponse)
                .toList();
    }

    public List<MantenimientoResponse> listarMantenimientosPendientes() {
        return mantenimientoRepository.findByEstado(EstadoMantenimiento.PENDIENTE)
                .stream()
                .map(this::toMantenimientoResponse)
                .toList();
    }

    public List<MantenimientoResponse> listarMantenimientosProximos() {
        LocalDate hoy = LocalDate.now();
        LocalDate hasta = hoy.plusDays(15);

        return mantenimientoRepository.findByFechaProgramadaBetween(hoy, hasta)
                .stream()
                .map(this::toMantenimientoResponse)
                .toList();
    }

    public List<MantenimientoResponse> listarMantenimientosVencidos() {
        LocalDate hoy = LocalDate.now();

        return mantenimientoRepository.findByFechaProgramadaLessThanEqualAndEstado(
                        hoy,
                        EstadoMantenimiento.PENDIENTE
                )
                .stream()
                .map(this::toMantenimientoResponse)
                .toList();
    }

    @Transactional
    public MantenimientoResponse actualizarMantenimiento(Long id, ActualizarMantenimientoRequest request) {
        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mantenimiento no encontrado con id: " + id));

        mantenimiento.setEstado(request.estado());
        mantenimiento.setObservaciones(request.observaciones());

        if (request.estado() == EstadoMantenimiento.REALIZADO) {
            LocalDate fechaRealizada = request.fechaRealizada() == null
                    ? LocalDate.now()
                    : request.fechaRealizada();

            mantenimiento.setFechaRealizada(fechaRealizada);

            crearSiguienteMantenimiento(mantenimiento.getInstalacion(), fechaRealizada);
        }

        return toMantenimientoResponse(mantenimientoRepository.save(mantenimiento));
    }

    private void crearSiguienteMantenimiento(Instalacion instalacion, LocalDate fechaBase) {
        LocalDate siguienteFecha = fechaBase.plusMonths(3);

        if (siguienteFecha.isAfter(instalacion.getFechaFinGarantia())) {
            return;
        }

        instalacion.setFechaProximoMantenimiento(siguienteFecha);
        instalacionRepository.save(instalacion);

        Mantenimiento siguiente = Mantenimiento.builder()
                .instalacion(instalacion)
                .fechaProgramada(siguienteFecha)
                .fechaRealizada(null)
                .estado(EstadoMantenimiento.PENDIENTE)
                .observaciones("Mantenimiento programado automáticamente cada 3 meses.")
                .build();

        Mantenimiento siguienteGuardado = mantenimientoRepository.save(siguiente);

        notificacionService.crear(new NotificacionRequest(
                TipoNotificacion.MANTENIMIENTO_PROXIMO,
                "Nuevo mantenimiento programado",
                "La venta N° " + instalacion.getVenta().getId()
                        + " del cliente " + instalacion.getVenta().getCliente().getNombreComercial()
                        + " tiene un nuevo mantenimiento programado para el "
                        + siguienteFecha + ".",
                "MANTENIMIENTO",
                siguienteGuardado.getId()
        ));
    }

    private InstalacionResponse toInstalacionResponse(Instalacion instalacion) {
        return new InstalacionResponse(
                instalacion.getId(),
                instalacion.getVenta().getId(),
                instalacion.getVenta().getCliente().getNombreComercial(),
                instalacion.getFechaInstalacion(),
                instalacion.getFechaFinGarantia(),
                instalacion.getFechaProximoMantenimiento(),
                instalacion.getDireccionInstalacion(),
                instalacion.getObservaciones()
        );
    }

    private MantenimientoResponse toMantenimientoResponse(Mantenimiento mantenimiento) {
        return new MantenimientoResponse(
                mantenimiento.getId(),
                mantenimiento.getInstalacion().getId(),
                mantenimiento.getInstalacion().getVenta().getId(),
                mantenimiento.getInstalacion().getVenta().getCliente().getNombreComercial(),
                mantenimiento.getFechaProgramada(),
                mantenimiento.getFechaRealizada(),
                mantenimiento.getEstado(),
                mantenimiento.getObservaciones()
        );
    }
}