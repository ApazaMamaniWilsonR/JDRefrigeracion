import { Component, OnInit, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { CompraService } from '../compra.service';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { ProductoService } from '../../../core/services/producto.service';

import { Proveedor } from '../../../core/models/proveedor.model';
import { Producto } from '../../../core/models/producto.model';
import { Compra } from '../compra.model';

@Component({
  selector: 'app-compra-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './compra-form.html',
  styleUrl: './compra-form.css',
})
export class CompraFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly compraService = inject(CompraService);
  private readonly proveedorService = inject(ProveedorService);
  private readonly productoService = inject(ProductoService);
  private readonly router = inject(Router);

  protected readonly form = this.fb.group({
    proveedorId: [null as number | null, [Validators.required]],
    fechaCompra: [new Date().toISOString().split('T')[0], [Validators.required]],
    numeroFactura: ['', [Validators.maxLength(50)]],
    moneda: ['PEN' as 'PEN' | 'USD', [Validators.required]],
    detalles: this.fb.array<FormGroup>([]),
  });

  proveedores = signal<Proveedor[]>([]);
  productos = signal<Producto[]>([]);
  guardando = signal<boolean>(false);
  error = signal<string | null>(null);

  ngOnInit(): void {
    this.agregarDetalle();
    this.cargarDatos();
  }

  get detalles(): FormArray {
    return this.form.get('detalles') as FormArray;
  }

  cargarDatos(): void {
    this.proveedorService.listar().subscribe({
      next: (data) => this.proveedores.set(data),
      error: () => this.error.set('No se pudieron cargar los proveedores.'),
    });

    this.productoService.listar().subscribe({
      next: (data) => this.productos.set(data),
      error: () => this.error.set('No se pudieron cargar los productos.'),
    });
  }

  crearDetalleGroup(): FormGroup {
    return this.fb.group({
      productoId: [null as number | null, [Validators.required]],
      cantidad: [1, [Validators.required, Validators.min(1)]],
      precioUnitarioCompra: [0.0, [Validators.required, Validators.min(0.01)]],
    });
  }

  agregarDetalle(): void {
    this.detalles.push(this.crearDetalleGroup());
  }

  removerDetalle(index: number): void {
    if (this.detalles.length > 1) {
      this.detalles.removeAt(index);
    } else {
      this.detalles.at(0).reset({ productoId: null, cantidad: 1, precioUnitarioCompra: 0.0 });
    }
  }

  onProductoChange(index: number): void {
    const detalle = this.detalles.at(index);
    const prodId = detalle.get('productoId')?.value;
    if (prodId) {
      const prod = this.productos().find((p) => p.id === Number(prodId));
      if (prod && !detalle.get('precioUnitarioCompra')?.value) {
        detalle.patchValue({ precioUnitarioCompra: prod.precio });
      }
    }
  }

  calcularSubtotal(index: number): number {
    const detalle = this.detalles.at(index);
    const cantidad = Number(detalle.get('cantidad')?.value) || 0;
    const precio = Number(detalle.get('precioUnitarioCompra')?.value) || 0;
    return cantidad * precio;
  }

  calcularTotal(): number {
    let total = 0;
    for (let i = 0; i < this.detalles.length; i++) {
      total += this.calcularSubtotal(i);
    }
    return total;
  }

  getSimboloMoneda(): string {
    return this.form.get('moneda')?.value === 'USD' ? '$ ' : 'S/ ';
  }

  guardar(): void {
    if (this.form.invalid || this.detalles.length === 0) {
      this.form.markAllAsTouched();
      this.error.set('Por favor complete los campos obligatorios y asegúrese de incluir al menos un producto.');
      return;
    }

    this.guardando.set(true);
    this.error.set(null);

    const formVal = this.form.getRawValue();
    const compraData: Compra = {
      proveedorId: Number(formVal.proveedorId),
      fechaCompra: formVal.fechaCompra!,
      numeroFactura: formVal.numeroFactura || undefined,
      moneda: formVal.moneda as 'PEN' | 'USD',
      total: this.calcularTotal(),
      detalles: formVal.detalles.map((d: any) => ({
        productoId: Number(d.productoId),
        cantidad: Number(d.cantidad),
        precioUnitarioCompra: Number(d.precioUnitarioCompra),
      })),
    };

    this.compraService.registrar(compraData).subscribe({
      next: () => {
        this.guardando.set(false);
        this.router.navigate(['/compras']);
      },
      error: (err) => {
        this.guardando.set(false);
        this.error.set(err.error?.mensaje || err.message || 'Error al registrar la compra.');
      },
    });
  }
}
