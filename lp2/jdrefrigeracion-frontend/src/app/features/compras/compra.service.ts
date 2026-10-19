import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Compra } from './compra.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CompraService {
  private apiUrl = `${environment.apiBaseUrl}/api/admin/compras`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Compra[]> {
    return this.http.get<Compra[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Compra> {
    return this.http.get<Compra>(`${this.apiUrl}/${id}`);
  }

  registrar(compra: Compra): Observable<Compra> {
    return this.http.post<Compra>(this.apiUrl, compra);
  }
}
