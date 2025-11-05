import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private apiUrl = 'http://localhost:8080/api/dashboard'; // ✅ Spring Boot base URL

  constructor(private http: HttpClient) {}

  // 🔹 Fetch seeker dashboard data
  getSeekerDashboardData(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/seeker`);
  }

  // 🔹 Fetch helper dashboard data
  getHelperDashboardData(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/helper`);
  }

  // 🔹 Offer help for a request
  offerHelp(helperEmail: string, requestId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/offer-help`, {
      helperEmail: helperEmail,
      requestId: requestId
    });
  }

  // 🔹 Get list of available helpers
  getAvailableHelpers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/helpers`);
  }

  // 🔹 Contact a helper
  contactHelper(seekerEmail: string, helperEmail: string, message: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/contact-helper`, {
      seekerEmail: seekerEmail,
      helperEmail: helperEmail,
      message: message
    });
  }

  // ⭐ Submit feedback for completed request
  submitFeedback(feedbackData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/feedback`, feedbackData);
  }
}
