import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api-service';
import { VentaResponse, VentaRequest } from './venta.model';

@Injectable({ providedIn: 'root' })
export class VentaService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  listar(): Observable<VentaResponse[]> {
    return this.http.get<VentaResponse[]>(this.api.buildUrl('/api/v1/ventas'));
  }

  buscarPorId(id: number): Observable<VentaResponse> {
    return this.http.get<VentaResponse>(this.api.buildUrl(`/api/v1/ventas/${id}`));
  }

  crear(request: VentaRequest): Observable<VentaResponse> {
    return this.http.post<VentaResponse>(this.api.buildUrl('/api/v1/ventas'), request);
  }

  anular(id: number): Observable<void> {
    return this.http.post<void>(this.api.buildUrl(`/api/v1/ventas/${id}/anular`), {});
  }
}
