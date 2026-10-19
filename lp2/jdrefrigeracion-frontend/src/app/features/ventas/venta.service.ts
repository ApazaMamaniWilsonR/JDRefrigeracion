import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../../core/services/api-service';
import { VentaResponse, VentaRequest } from './venta.model';

@Injectable({ providedIn: 'root' })
export class VentaService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);

  listar(clienteId?: number, estado?: string, desde?: string, hasta?: string): Observable<VentaResponse[]> {
    let params = new HttpParams();
    if (clienteId) {
      params = params.set('clienteId', clienteId);
    }
    if (estado) {
      params = params.set('estado', estado);
    }
    if (desde) {
      params = params.set('desde', desde);
    }
    if (hasta) {
      params = params.set('hasta', hasta);
    }
    return this.http.get<VentaResponse[]>(this.api.buildUrl('/api/v1/ventas'), { params });
  }

  buscarPorId(id: number): Observable<VentaResponse> {
    return this.http.get<VentaResponse>(this.api.buildUrl(`/api/v1/ventas/${id}`));
  }

  crear(request: VentaRequest): Observable<VentaResponse> {
    return this.http.post<VentaResponse>(this.api.buildUrl('/api/v1/ventas'), request);
  }

  anular(id: number): Observable<VentaResponse> {
    return this.http.patch<VentaResponse>(this.api.buildUrl(`/api/v1/ventas/${id}/anular`), {});
  }
}
