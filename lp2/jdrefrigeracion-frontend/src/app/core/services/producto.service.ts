import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiService } from '../services/api-service';
import { Producto, ProductoRequest } from '../models/producto.model';

@Injectable({ providedIn: 'root' })
export class ProductoService {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ApiService);
  private readonly endpoint = '/api/v1/productos';

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.api.buildUrl(this.endpoint));
  }

  obtener(id: number): Observable<Producto> {
    return this.http.get<Producto>(this.api.buildUrl(`${this.endpoint}/${id}`));
  }

  crear(request: ProductoRequest): Observable<Producto> {
    return this.http.post<Producto>(this.api.buildUrl(this.endpoint), request);
  }

  actualizar(id: number, request: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(this.api.buildUrl(`${this.endpoint}/${id}`), request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(this.api.buildUrl(`${this.endpoint}/${id}`));
  }
}
