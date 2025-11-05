import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthResponse, OtpResponse } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, { email, password });
  }

  signup(name: string, email: string, password: string, role: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/signup`, { name, email, password, role });
  }

  sendOtp(email: string, role: string): Observable<OtpResponse> {
    return this.http.post<OtpResponse>(`${this.baseUrl}/send-otp`, { email, role });
  }

  verifyOtp(email: string, otp: string): Observable<OtpResponse> {
    return this.http.post<OtpResponse>(`${this.baseUrl}/verify-otp`, { email, otp });
  }
}
