import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductoService } from '../../../../core/services/producto.service';
import { CategoriaService } from '../../categoria/categoria-service';
import { Categoria } from '../../categoria/categoria.model';
import { Producto, ProductoRequest } from '../../../../core/models/producto.model';

@Component({
  selector: 'app-producto-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './producto-form.html',
  styleUrl: './producto-form.css',
})
export class ProductoForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly productoService = inject(ProductoService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  categorias = signal<Categoria[]>([]);
  productoId?: number;
  error = signal<string | null>(null);

  protected readonly form = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(150)]],
    descripcion: [''],
    precio: [0.0, [Validators.required, Validators.min(0.01)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    stockMinimo: [5, [Validators.required, Validators.min(0)]],
    categoriaId: [null as number | null, [Validators.required]],
  });

  ngOnInit(): void {
    this.categoriaService.listar().subscribe((cats: Categoria[]) => this.categorias.set(cats));

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam && idParam !== 'nuevo') {
      this.productoId = Number(idParam);
      this.productoService.obtener(this.productoId).subscribe({
        next: (prod: Producto) => {
          this.form.patchValue({
            nombre: prod.nombre,
            descripcion: prod.descripcion || '',
            precio: prod.precio,
            stock: prod.stock,
            stockMinimo: prod.stockMinimo ?? 5,
            categoriaId: prod.categoriaId ?? null,
          });
        },
        error: () => this.error.set('No se pudo cargar el producto.'),
      });
    }
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const val = this.form.getRawValue();
    const datos: ProductoRequest = {
      nombre: val.nombre!,
      descripcion: val.descripcion || '',
      precio: Number(val.precio),
      stock: Number(val.stock),
      stockMinimo: Number(val.stockMinimo),
      categoriaId: Number(val.categoriaId),
    };

    const peticion = this.productoId
      ? this.productoService.actualizar(this.productoId, datos)
      : this.productoService.crear(datos);

    peticion.subscribe({
      next: () => this.router.navigate(['/catalogo/productos']),
      error: (err: any) => {
        this.error.set(err.error?.mensaje || 'Error al guardar el producto.');
      },
    });
  }
}
