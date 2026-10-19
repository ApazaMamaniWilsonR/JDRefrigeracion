import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CompraService } from '../compra.service';
import { Compra } from '../compra.model';

@Component({
  selector: 'app-compra-list',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe, DatePipe],
  templateUrl: './compra-list.html',
  styleUrl: './compra-list.css',
})
export class CompraListComponent implements OnInit {
  private readonly compraService = inject(CompraService);

  protected compras = signal<Compra[]>([]);
  protected compraSeleccionada = signal<Compra | null>(null);
  protected cargando = signal<boolean>(false);
  protected error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarCompras();
  }

  cargarCompras(): void {
    this.cargando.set(true);
    this.compraService.listar().subscribe({
      next: (data) => {
        this.compras.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.mensaje || 'Error al cargar el historial de compras.');
        this.cargando.set(false);
      },
    });
  }

  verDetalle(compra: Compra): void {
    if (compra.id) {
      this.compraService.buscarPorId(compra.id).subscribe({
        next: (c) => this.compraSeleccionada.set(c),
        error: () => this.compraSeleccionada.set(compra),
      });
    } else {
      this.compraSeleccionada.set(compra);
    }
  }

  cerrarDetalle(): void {
    this.compraSeleccionada.set(null);
  }
}
