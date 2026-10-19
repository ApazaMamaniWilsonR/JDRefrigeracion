import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

import { VentaService } from '../venta.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { ProductoService } from '../../../core/services/producto.service';
import { Cliente } from '../../../core/models/cliente.model';
import { Producto } from '../../../core/models/producto.model';
import { VentaRequest, DetalleVentaRequest } from '../venta.model';

@Component({
  selector: 'app-venta-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    CurrencyPipe,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './venta-form.html',
  styleUrl: './venta-form.css',
})
export class VentaForm implements OnInit {
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly ventaService = inject(VentaService);
  private readonly clienteService = inject(ClienteService);
  private readonly productoService = inject(ProductoService);

  private crearLinea(): FormGroup {
    return this.fb.group({
      productoId: [null, [Validators.required, Validators.min(1)]],
      busqueda: [''],
      cantidad: [1, [Validators.required, Validators.min(1)]]
    });
  }

  readonly form = this.fb.group({
    clienteId: [null, Validators.required],
    moneda: ['PEN', Validators.required],
    tipoCambio: [1.0, [Validators.required, Validators.min(0.01)]],
    descuento: [0, [Validators.min(0)]],
    detalles: this.fb.array([this.crearLinea()], [Validators.required, Validators.minLength(1)])
  });

  clientes = signal<Cliente[]>([]);
  productos = signal<Producto[]>([]);
  clientesLoaded = signal(false);
  error = signal<string | null>(null);
  loading = signal(false);

  get detallesFormArray(): FormArray {
    return this.form.get('detalles') as FormArray;
  }

  // Reactividad con Signals (computed)
  protected readonly valoresDetalles = toSignal(
    this.form.controls.detalles.valueChanges,
    { initialValue: this.form.controls.detalles.getRawValue() }
  );

  protected readonly opcionesPorLinea = computed(() =>
    (this.valoresDetalles() || []).map((linea: any) => {
      const texto = (linea?.busqueda ?? '').toLowerCase();
      return this.productos().filter((p) => p.nombre.toLowerCase().includes(texto));
    })
  );

  protected readonly valorMoneda = toSignal(
    this.form.controls.moneda.valueChanges,
    { initialValue: this.form.controls.moneda.value ?? 'PEN' }
  );

  protected readonly valorTipoCambio = toSignal(
    this.form.controls.tipoCambio.valueChanges,
    { initialValue: this.form.controls.tipoCambio.value ?? 1.0 }
  );

  protected readonly simboloMoneda = computed(() => {
    return this.valorMoneda() === 'USD' ? '$ ' : 'S/ ';
  });

  protected readonly preciosUnitarios = computed(() => {
    const moneda = this.valorMoneda();
    const tc = Number(this.valorTipoCambio()) || 1.0;

    return (this.valoresDetalles() || []).map((linea: any) => {
      const producto = this.productos().find((p) => p.id == linea?.productoId);
      if (!producto) return 0;
      if (moneda === 'USD' && tc > 0) {
        return Math.round((producto.precio / tc) * 100) / 100;
      }
      return producto.precio;
    });
  });

  protected readonly subtotales = computed(() =>
    (this.valoresDetalles() || []).map((linea: any, index: number) => {
      const precio = this.preciosUnitarios()[index] ?? 0;
      const cant = linea?.cantidad ?? 0;
      return Math.round((precio * cant) * 100) / 100;
    })
  );

  protected readonly valorDescuento = toSignal(
    this.form.controls.descuento.valueChanges,
    { initialValue: this.form.controls.descuento.value ?? 0 }
  );

  protected readonly subtotalGeneral = computed(() =>
    this.subtotales().reduce((acc: number, curr: number) => acc + curr, 0)
  );

  protected readonly descuentoTotal = computed(() => {
    return Number(this.valorDescuento()) || 0;
  });

  protected readonly baseImponible = computed(() => {
    return Math.max(0, this.subtotalGeneral() - this.descuentoTotal());
  });

  protected readonly igv = computed(() => {
    return Math.round((this.baseImponible() * 0.18) * 100) / 100;
  });

  protected readonly total = computed(() => {
    return Math.round((this.baseImponible() + this.igv()) * 100) / 100;
  });

  aplicarDescuentoPorcentaje(porcentaje: number): void {
    const desc = Math.round((this.subtotalGeneral() * (porcentaje / 100)) * 100) / 100;
    this.form.controls.descuento.setValue(desc);
  }

  establecerTipoCambioActual(): void {
    this.form.controls.tipoCambio.setValue(3.75);
  }

  ngOnInit(): void {
    // Si el usuario cambia a Dólares y el tipo de cambio está en 1.0, autocompletar con 3.75
    this.form.controls.moneda.valueChanges.subscribe((moneda) => {
      if (moneda === 'USD' && (this.form.controls.tipoCambio.value === 1.0 || !this.form.controls.tipoCambio.value)) {
        this.establecerTipoCambioActual();
      }
    });

    this.clienteService.listar().subscribe({
      next: (data) => {
        this.clientes.set(data);
        this.clientesLoaded.set(true);
      },
      error: () => this.error.set('No se pudieron cargar los clientes.')
    });

    this.productoService.listar().subscribe({
      next: (data) => this.productos.set(data),
      error: () => this.error.set('No se pudieron cargar los productos.')
    });
  }

  agregarLinea(): void {
    this.detallesFormArray.push(this.crearLinea());
  }

  quitarLinea(index: number): void {
    if (this.detallesFormArray.length > 1) {
      this.detallesFormArray.removeAt(index);
    }
  }

  seleccionarProducto(index: number, evento: MatAutocompleteSelectedEvent): void {
    const producto: Producto = evento.option.value;
    this.detallesFormArray.at(index).patchValue({
      productoId: producto.id,
      busqueda: producto.nombre
    });
  }

  confirmarYGuardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const cantidadLineas = this.detallesFormArray.length;
    const resumen = `Vas a registrar una venta de ${cantidadLineas} línea(s) por un total de ` +
      `${this.simboloMoneda()}${this.total().toFixed(2)}. ¿Confirmar?`;
    if (!confirm(resumen)) return;
    this.guardar();
  }

  private guardar(): void {
    this.error.set(null);
    this.loading.set(true);

    const formVal = this.form.getRawValue();
    const detalles: DetalleVentaRequest[] = this.detallesFormArray.getRawValue().map((d: any) => ({
      productoId: d.productoId,
      cantidad: d.cantidad
    }));

    const request: VentaRequest = {
      clienteId: Number(formVal.clienteId),
      moneda: formVal.moneda ?? 'PEN',
      tipoCambio: Number(formVal.tipoCambio) || 1.0,
      descuento: Number(formVal.descuento) || 0,
      detalles
    };

    this.ventaService.crear(request).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/ventas/reporte']);
      },
      error: (err: HttpErrorResponse) => this.manejarErrorGuardado(err)
    });
  }

  private manejarErrorGuardado(err: HttpErrorResponse): void {
    this.loading.set(false);
    const mensaje: string = err.error?.mensaje ?? err.error?.message ?? '';

    if (err.status === 404 && mensaje.toLowerCase().includes('producto')) {
      this.error.set('Uno de los productos elegidos ya no existe. Revisa las líneas de la venta.');
    } else if (err.status === 404 && mensaje.toLowerCase().includes('cliente')) {
      this.error.set('El cliente seleccionado ya no existe en el sistema. Revisa la selección.');
    } else if (err.status === 409) {
      this.error.set(mensaje || 'No hay stock suficiente para completar la venta.');
    } else if (err.status === 400) {
      this.error.set('Los datos enviados no son válidos: ' + (mensaje || 'Revisa los campos del formulario.'));
    } else {
      this.error.set('No se pudo registrar la venta.');
    }
  }
}
