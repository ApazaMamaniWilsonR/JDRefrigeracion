import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClienteService } from '../../../core/services/cliente.service';
import { Cliente, ClienteRequest, ConsultaDocumentoResponse, TipoCliente } from '../../../core/models/cliente.model';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cliente-form.html',
  styleUrl: './cliente-form.css',
})
export class ClienteForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly clienteService = inject(ClienteService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  clienteId?: number;
  consultando = signal<boolean>(false);
  error = signal<string | null>(null);
  mensajeConsulta = signal<string | null>(null);

  protected readonly form = this.fb.group({
    tipoCliente: ['PERSONA' as TipoCliente, [Validators.required]],
    numeroDocumento: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(11)]],
    nombres: [''],
    apellidos: [''],
    razonSocial: [''],
    personaContacto: [''],
    direccion: [''],
    telefono: [''],
    correo: ['', [Validators.email]],
    estadoContribuyente: [''],
    condicionContribuyente: [''],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam && idParam !== 'nuevo') {
      this.clienteId = Number(idParam);
      this.clienteService.buscarPorId(this.clienteId).subscribe({
        next: (cli: Cliente) => {
          this.form.patchValue({
            tipoCliente: cli.tipoCliente,
            numeroDocumento: cli.numeroDocumento,
            nombres: cli.nombres || '',
            apellidos: cli.apellidos || '',
            razonSocial: cli.razonSocial || '',
            personaContacto: cli.personaContacto || '',
            direccion: cli.direccion || '',
            telefono: cli.telefono || '',
            correo: cli.correo || '',
            estadoContribuyente: cli.estadoContribuyente || '',
            condicionContribuyente: cli.condicionContribuyente || '',
          });
        },
        error: () => this.error.set('No se pudo cargar la información del cliente.'),
      });
    }

    // Auto ajustar validación según tipo de cliente
    this.form.controls.tipoCliente.valueChanges.subscribe((tipo) => {
      this.actualizarValidadores(tipo as TipoCliente);
    });
    this.actualizarValidadores(this.form.controls.tipoCliente.value as TipoCliente);
  }

  private actualizarValidadores(tipo: TipoCliente): void {
    const docControl = this.form.controls.numeroDocumento;
    if (tipo === 'PERSONA') {
      docControl.setValidators([Validators.required, Validators.pattern(/^[0-9]{8}$/)]);
    } else {
      docControl.setValidators([Validators.required, Validators.pattern(/^[0-9]{11}$/)]);
    }
    docControl.updateValueAndValidity();
  }

  consultarDocumento(): void {
    const tipo = this.form.controls.tipoCliente.value;
    const doc = this.form.controls.numeroDocumento.value?.trim();

    if (!doc) {
      this.error.set('Ingrese un número de documento.');
      return;
    }

    this.consultando.set(true);
    this.error.set(null);
    this.mensajeConsulta.set(null);

    const peticion = tipo === 'PERSONA'
      ? this.clienteService.consultarDni(doc)
      : this.clienteService.consultarRuc(doc);

    peticion.subscribe({
      next: (resp: ConsultaDocumentoResponse) => {
        this.consultando.set(false);
        this.form.patchValue({
          nombres: resp.nombres || '',
          apellidos: resp.apellidos || '',
          razonSocial: resp.razonSocial || '',
          direccion: resp.direccion || '',
          estadoContribuyente: resp.estadoContribuyente || '',
          condicionContribuyente: resp.condicionContribuyente || '',
        });
        this.mensajeConsulta.set('✓ Datos obtenidos correctamente.');
      },
      error: (err) => {
        this.consultando.set(false);
        this.error.set(err.error?.mensaje || 'No se encontraron datos para el documento consultado.');
      },
    });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const val = this.form.getRawValue();
    const datos: ClienteRequest = {
      tipoCliente: val.tipoCliente as TipoCliente,
      numeroDocumento: val.numeroDocumento!,
      nombres: val.nombres || undefined,
      apellidos: val.apellidos || undefined,
      razonSocial: val.razonSocial || undefined,
      personaContacto: val.personaContacto || undefined,
      direccion: val.direccion || undefined,
      telefono: val.telefono || undefined,
      correo: val.correo || undefined,
      estadoContribuyente: val.estadoContribuyente || undefined,
      condicionContribuyente: val.condicionContribuyente || undefined,
    };

    const peticion = this.clienteId
      ? this.clienteService.actualizar(this.clienteId, datos)
      : this.clienteService.crear(datos);

    peticion.subscribe({
      next: () => this.router.navigate(['/clientes']),
      error: (err) => {
        this.error.set(err.error?.mensaje || 'Error al guardar el cliente.');
      },
    });
  }
}
