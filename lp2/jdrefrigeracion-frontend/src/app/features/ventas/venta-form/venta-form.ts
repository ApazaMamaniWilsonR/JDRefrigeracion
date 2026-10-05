import { Component, OnInit, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { CurrencyPipe } from '@angular/common';

import { VentaService } from '../venta.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { ProductoService } from '../../../core/services/producto.service';
import { Cliente } from '../../../core/models/cliente.model';
import { Producto } from '../../../core/models/producto.model';
import { VentaRequest } from '../venta.model';

@Component({
  selector: 'app-venta-form',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './venta-form.html',
  styleUrl: './venta-form.css',
})
export class VentaForm implements OnInit {
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly ventaService = inject(VentaService);
  private readonly clienteService = inject(ClienteService);
  private readonly productoService = inject(ProductoService);

  form: FormGroup;
  clientes = signal<Cliente[]>([]);
  productos = signal<Producto[]>([]);
  clientesLoaded = signal(false);

  constructor() {
    this.form = this.fb.group({
      clienteId: [null, Validators.required],
      moneda: ['PEN', Validators.required],
      tipoCambio: [1.0, [Validators.required, Validators.min(0.01)]],
      descuento: [0, [Validators.min(0)]],
      detalles: this.fb.array([], Validators.required)
    });
  }

  ngOnInit(): void {
    this.clienteService.listar().subscribe(data => {
      this.clientes.set(data);
      this.clientesLoaded.set(true);
    });
    this.productoService.listar().subscribe(data => this.productos.set(data));
    this.agregarDetalle(); // Add one row by default
  }

  get detallesFormArray() {
    return this.form.get('detalles') as FormArray;
  }

  agregarDetalle(): void {
    const detalle = this.fb.group({
      productoId: [null, Validators.required],
      cantidad: [1, [Validators.required, Validators.min(1)]]
    });
    this.detallesFormArray.push(detalle);
  }

  removerDetalle(index: number): void {
    this.detallesFormArray.removeAt(index);
  }

  getProductoPrecio(productoId: number): number {
    const producto = this.productos().find(p => p.id == productoId);
    return producto ? producto.precio : 0;
  }

  getSubtotalDetalle(index: number): number {
    const detalle = this.detallesFormArray.at(index).value;
    if (!detalle.productoId) return 0;
    return this.getProductoPrecio(detalle.productoId) * (detalle.cantidad || 0);
  }

  getSubtotalGeneral(): number {
    let subtotal = 0;
    for (let i = 0; i < this.detallesFormArray.length; i++) {
      subtotal += this.getSubtotalDetalle(i);
    }
    return subtotal;
  }

  getTotal(): number {
    const subtotal = this.getSubtotalGeneral();
    const descuento = this.form.value.descuento || 0;
    // Base calculation (Assuming IGV is added automatically in backend, or here)
    // The backend `Venta` entity handles IGV = (Subtotal - Descuento) * 0.18
    const igv = (subtotal - descuento) * 0.18;
    return (subtotal - descuento) + igv;
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: VentaRequest = this.form.value;
    this.ventaService.crear(request).subscribe({
      next: () => {
        this.router.navigate(['/ventas']);
      },
      error: (err: HttpErrorResponse) => this.manejarErrorGuardado(err)
    });
  }

  private manejarErrorGuardado(err: HttpErrorResponse): void {
    const mensaje: string = err.error?.message ?? '';

    if (err.status === 404 && mensaje.toLowerCase().includes('cliente')) {
      alert('El cliente seleccionado ya no existe. La lista se recargará.');
      this.form.controls['clienteId'].setValue(null);
      this.clienteService.listar().subscribe(data => this.clientes.set(data));
    } else if (err.status === 404 && mensaje.toLowerCase().includes('producto')) {
      alert('Uno de los productos seleccionados ya no existe. Revise su detalle.');
      this.productoService.listar().subscribe(data => this.productos.set(data));
    } else if (err.status === 400) {
      alert('Los datos enviados no son válidos. Revise los campos: ' + mensaje);
    } else {
      console.error('Error al guardar la venta', err);
      alert('No se pudo guardar la venta.');
    }
  }
}
