import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ClienteService } from '../../../core/services/cliente.service';
import { Cliente } from '../../../core/models/cliente.model';

@Component({
  selector: 'app-cliente-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './cliente-list.html',
  styleUrl: './cliente-list.css',
})
export class ClienteList implements OnInit {
  private readonly clienteService = inject(ClienteService);
  protected clientes = signal<Cliente[]>([]);
  protected cargando = signal<boolean>(false);
  protected error = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarClientes();
  }

  cargarClientes(): void {
    this.cargando.set(true);
    this.clienteService.listar().subscribe({
      next: (data) => {
        this.clientes.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.mensaje || 'Error al cargar clientes.');
        this.cargando.set(false);
      },
    });
  }

  eliminar(id: number): void {
    if (confirm('¿Está seguro de eliminar este cliente?')) {
      this.clienteService.eliminar(id).subscribe({
        next: () => this.cargarClientes(),
        error: (err) => alert(err.error?.mensaje || 'No se pudo eliminar el cliente.'),
      });
    }
  }

  getNombreCompleto(c: Cliente): string {
    if (c.tipoCliente === 'EMPRESA') {
      return c.razonSocial || c.nombreComercial || '-';
    }
    return [c.nombres, c.apellidos].filter(Boolean).join(' ') || '-';
  }
}
