import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { VentaService } from '../venta.service';
import { VentaResponse } from '../venta.model';
import { ClienteService } from '../../../core/services/cliente.service';
import { Cliente } from '../../../core/models/cliente.model';

@Component({
  selector: 'app-venta-list',
  imports: [RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './venta-list.html',
  styleUrl: './venta-list.css',
})
export class VentaList implements OnInit {
  private readonly ventaService = inject(VentaService);
  private readonly clienteService = inject(ClienteService);
  
  protected ventas = signal<VentaResponse[]>([]);
  protected clientes = signal<Cliente[]>([]);
  protected clienteFiltro = signal<number | null>(null);
  protected ventaSeleccionada = signal<VentaResponse | null>(null);

  ngOnInit(): void {
    this.clienteService.listar().subscribe((data) => this.clientes.set(data));
    this.cargarVentas();
  }

  cargarVentas(): void {
    this.ventaService.listar(this.clienteFiltro() ?? undefined).subscribe((data) => {
      this.ventas.set(data);
    });
  }

  filtrar(clienteId: number): void {
    this.clienteFiltro.set(clienteId || null);
    this.cargarVentas();
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
