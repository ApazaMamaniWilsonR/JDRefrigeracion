import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from './api-service';
import { Cliente, ClienteRequest, ConsultaDocumentoResponse } from '../models/cliente.model';

@Injectable({ providedIn: 'root' })
export class ClienteService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);
  private readonly endpoint = '/api/v1/clientes';

  listar(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.api.buildUrl(this.endpoint));
  }

  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(this.api.buildUrl(`${this.endpoint}/${id}`));
  }

  crear(request: ClienteRequest): Observable<Cliente> {
    return this.http.post<Cliente>(this.api.buildUrl(this.endpoint), request);
  }

  actualizar(id: number, request: ClienteRequest): Observable<Cliente> {
    return this.http.put<Cliente>(this.api.buildUrl(`${this.endpoint}/${id}`), request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(this.api.buildUrl(`${this.endpoint}/${id}`));
  }

  consultarDni(dni: string): Observable<ConsultaDocumentoResponse> {
    return this.http.get<ConsultaDocumentoResponse>(this.api.buildUrl(`${this.endpoint}/consultar-dni/${dni}`));
  }

  consultarRuc(ruc: string): Observable<ConsultaDocumentoResponse> {
    return this.http.get<ConsultaDocumentoResponse>(this.api.buildUrl(`${this.endpoint}/consultar-ruc/${ruc}`));
  }
}
