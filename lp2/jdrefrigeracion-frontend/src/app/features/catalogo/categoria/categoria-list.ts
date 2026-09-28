import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CategoriaService } from './categoria-service';
import { Categoria } from './categoria.model';

@Component({
  selector: 'app-categoria-list',
  imports: [RouterLink],
  templateUrl: './categoria-list.html',
  styleUrl: './categoria-list.css',
})
export class CategoriaList implements OnInit {
  private readonly categoriaService = inject(CategoriaService);
  protected categorias = signal<Categoria[]>([]);

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias(): void {
    this.categoriaService.listar().subscribe((data) => {
      this.categorias.set(data);
    });
  }

  eliminar(id?: number): void {
    if (!id) return;
    if (confirm('¿Está seguro de eliminar esta categoría?')) {
      this.categoriaService.eliminar(id).subscribe(() => {
        this.cargarCategorias();
      });
    }
  }
}
