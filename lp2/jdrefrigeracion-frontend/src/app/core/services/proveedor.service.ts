import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from './api-service';
import { Proveedor } from '../models/proveedor.model';

@Injectable({ providedIn: 'root' })
export class ProveedorService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);
  private readonly endpoint = '/api/admin/proveedores';

  listar(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(this.api.buildUrl(this.endpoint));
  }

  buscarPorId(id: number): Observable<Proveedor> {
    return this.http.get<Proveedor>(this.api.buildUrl(`${this.endpoint}/${id}`));
  }

  crear(proveedor: Proveedor): Observable<Proveedor> {
    return this.http.post<Proveedor>(this.api.buildUrl(this.endpoint), proveedor);
  }

  actualizar(id: number, proveedor: Proveedor): Observable<Proveedor> {
    return this.http.put<Proveedor>(this.api.buildUrl(`${this.endpoint}/${id}`), proveedor);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(this.api.buildUrl(`${this.endpoint}/${id}`));
  }

  autocompletarPorRuc(ruc: string): Observable<Proveedor> {
    return this.http.get<Proveedor>(this.api.buildUrl(`${this.endpoint}/autocompletar/ruc/${ruc}`));
  }

  autocompletarPorDni(dni: string): Observable<Proveedor> {
    return this.http.get<Proveedor>(this.api.buildUrl(`${this.endpoint}/autocompletar/dni/${dni}`));
  }
}
