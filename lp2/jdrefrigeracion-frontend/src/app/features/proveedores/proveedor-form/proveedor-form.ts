import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProveedorService } from '../../../core/services/proveedor.service';
import { Proveedor, TipoProveedor } from '../../../core/models/proveedor.model';

@Component({
  selector: 'app-proveedor-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './proveedor-form.html',
  styleUrl: './proveedor-form.css',
})
export class ProveedorForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly proveedorService = inject(ProveedorService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  proveedorId?: number;
  consultando = signal<boolean>(false);
  error = signal<string | null>(null);
  mensajeConsulta = signal<string | null>(null);

  protected readonly form = this.fb.group({
    tipo: ['EMPRESA' as TipoProveedor, [Validators.required]],
    nombreORazonSocial: ['', [Validators.required, Validators.maxLength(150)]],
    ruc: [''],
    dni: [''],
    direccion: ['', [Validators.maxLength(200)]],
    telefono: ['', [Validators.maxLength(20)]],
    correo: ['', [Validators.email, Validators.maxLength(100)]],
    contactoVendedor: ['', [Validators.maxLength(150)]],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam && idParam !== 'nuevo') {
      this.proveedorId = Number(idParam);
      this.proveedorService.buscarPorId(this.proveedorId).subscribe({
        next: (prov: Proveedor) => {
          this.form.patchValue({
            tipo: prov.tipo,
            nombreORazonSocial: prov.nombreORazonSocial,
            ruc: prov.ruc || '',
            dni: prov.dni || '',
            direccion: prov.direccion || '',
            telefono: prov.telefono || '',
            correo: prov.correo || '',
            contactoVendedor: prov.contactoVendedor || '',
          });
        },
        error: () => this.error.set('No se pudo cargar la información del proveedor.'),
      });
    }

    this.form.controls.tipo.valueChanges.subscribe((tipo) => {
      this.actualizarValidadores(tipo as TipoProveedor);
    });
    this.actualizarValidadores(this.form.controls.tipo.value as TipoProveedor);
  }

  private actualizarValidadores(tipo: TipoProveedor): void {
    const rucControl = this.form.controls.ruc;
    const dniControl = this.form.controls.dni;

    if (tipo === 'EMPRESA') {
      rucControl.setValidators([Validators.required, Validators.pattern(/^[0-9]{11}$/)]);
      dniControl.clearValidators();
    } else {
      dniControl.setValidators([Validators.required, Validators.pattern(/^[0-9]{8}$/)]);
      rucControl.clearValidators();
    }
    rucControl.updateValueAndValidity();
    dniControl.updateValueAndValidity();
  }

  autocompletar(): void {
    const tipo = this.form.controls.tipo.value;
    const numDoc = tipo === 'EMPRESA'
      ? this.form.controls.ruc.value?.trim()
      : this.form.controls.dni.value?.trim();

    if (!numDoc) {
      this.error.set('Ingrese el número de documento a consultar.');
      return;
    }

    this.consultando.set(true);
    this.error.set(null);
    this.mensajeConsulta.set(null);

    const peticion = tipo === 'EMPRESA'
      ? this.proveedorService.autocompletarPorRuc(numDoc)
      : this.proveedorService.autocompletarPorDni(numDoc);

    peticion.subscribe({
      next: (resp: Proveedor) => {
        this.consultando.set(false);
        this.form.patchValue({
          nombreORazonSocial: resp.nombreORazonSocial || '',
          direccion: resp.direccion || '',
          telefono: resp.telefono || '',
          correo: resp.correo || '',
          contactoVendedor: resp.contactoVendedor || '',
        });
        this.mensajeConsulta.set('✓ Datos autocompletados correctamente.');
      },
      error: (err) => {
        this.consultando.set(false);
        this.error.set(err.error?.mensaje || 'No se encontraron datos para el documento ingresado.');
      },
    });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const val = this.form.getRawValue();
    const datos: Proveedor = {
      tipo: val.tipo as TipoProveedor,
      nombreORazonSocial: val.nombreORazonSocial!,
      ruc: val.tipo === 'EMPRESA' ? (val.ruc || undefined) : undefined,
      dni: val.tipo === 'PERSONA_NATURAL' ? (val.dni || undefined) : undefined,
      direccion: val.direccion || undefined,
      telefono: val.telefono || undefined,
      correo: val.correo || undefined,
      contactoVendedor: val.contactoVendedor || undefined,
    };

    const peticion = this.proveedorId
      ? this.proveedorService.actualizar(this.proveedorId, datos)
      : this.proveedorService.crear(datos);

    peticion.subscribe({
      next: () => this.router.navigate(['/proveedores']),
      error: (err) => {
        this.error.set(err.error?.mensaje || 'Error al guardar el proveedor.');
      },
    });
  }
}
