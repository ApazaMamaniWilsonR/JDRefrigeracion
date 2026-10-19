import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { Proveedor } from '../../../core/models/proveedor.model';

@Component({
  selector: 'app-proveedor-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './proveedor-list.html',
  styleUrl: './proveedor-list.css',
})
export class ProveedorList implements OnInit {
  private readonly proveedorService = inject(ProveedorService);
  protected proveedores = signal<Proveedor[]>([]);
  protected cargando = signal<boolean>(false);
  protected error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarProveedores();
  }

  cargarProveedores(): void {
    this.cargando.set(true);
    this.proveedorService.listar().subscribe({
      next: (data) => {
        this.proveedores.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.mensaje || 'Error al cargar los proveedores.');
        this.cargando.set(false);
      },
    });
  }

  eliminar(id?: number): void {
    if (!id) return;
    if (confirm('¿Está seguro de eliminar este proveedor?')) {
      this.proveedorService.eliminar(id).subscribe({
        next: () => this.cargarProveedores(),
        error: (err) => alert(err.error?.mensaje || 'No se pudo eliminar el proveedor.'),
      });
    }
  }

  getDocumento(p: Proveedor): string {
    return p.tipo === 'EMPRESA' ? (p.ruc || '-') : (p.dni || '-');
  }
}
