import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CategoriaService } from '../categoria-service';
import { Categoria } from '../categoria.model';

@Component({
  selector: 'app-categoria-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './categoria-form.html',
  styleUrl: './categoria-form.css',
})
export class CategoriaForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly categoriaService = inject(CategoriaService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly form = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(80)]],
    descripcion: [''],
  });

  protected categoriaId?: number;

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.categoriaId = Number(idParam);
      this.categoriaService.obtener(this.categoriaId).subscribe((cat: Categoria) => {
        this.form.patchValue({
          nombre: cat.nombre,
          descripcion: cat.descripcion || '',
        });
      });
    }
  }

  guardar(): void {
    if (this.form.invalid) return;

    const datos: Categoria = {
      nombre: this.form.getRawValue().nombre,
      descripcion: this.form.getRawValue().descripcion,
    };

    const peticion = this.categoriaId
      ? this.categoriaService.actualizar(this.categoriaId, datos)
      : this.categoriaService.crear(datos);

    peticion.subscribe(() => {
      this.router.navigate(['/catalogo/categorias']);
    });
  }
}
