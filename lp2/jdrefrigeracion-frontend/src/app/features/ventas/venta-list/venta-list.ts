import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { VentaService } from '../venta.service';
import { VentaResponse } from '../venta.model';

@Component({
  selector: 'app-venta-list',
  imports: [RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './venta-list.html',
  styleUrl: './venta-list.css',
})
export class VentaList implements OnInit {
  private readonly ventaService = inject(VentaService);
  protected ventas = signal<VentaResponse[]>([]);
  protected ventaSeleccionada = signal<VentaResponse | null>(null);

  ngOnInit(): void {
    this.cargarVentas();
  }

  cargarVentas(): void {
    this.ventaService.listar().subscribe((data) => {
      this.ventas.set(data);
    });
  }

  anular(id?: number): void {
    if (!id) return;
    if (confirm('¿Está seguro de anular esta venta? (Esta acción no se puede deshacer)')) {
      this.ventaService.anular(id).subscribe(() => {
        this.cargarVentas();
      });
    }
  }

  verDetalle(venta: VentaResponse): void {
    this.ventaSeleccionada.set(venta);
  }

  cerrarDetalle(): void {
    this.ventaSeleccionada.set(null);
  }
}
