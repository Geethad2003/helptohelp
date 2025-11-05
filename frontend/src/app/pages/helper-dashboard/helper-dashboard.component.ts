import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../services/dashboard.service';

@Component({
  selector: 'app-helper-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './helper-dashboard.component.html',
  styleUrls: ['./helper-dashboard.component.css']
})
export class HelperDashboardComponent implements OnInit {
  activeRequestsCount = 0;
  completedRequestsCount = 0;
  availableHelpers = 0;
  activeRequests: any[] = [];
  completedRequests: any[] = [];
  helperEmail = '';

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    // Get the logged-in helper's email from localStorage
    this.helperEmail = localStorage.getItem('email') || 'helper@example.com';
    console.log('Helper dashboard - Using email:', this.helperEmail);
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.dashboardService.getHelperDashboardData().subscribe({
      next: (data) => {
        this.activeRequestsCount = data.activeRequestsCount;
        this.completedRequestsCount = data.completedRequestsCount;
        this.availableHelpers = data.availableHelpers;
        this.activeRequests = data.activeRequests;
        this.completedRequests = data.completedRequestsList;
      },
      error: (err) => {
        console.error('Error loading dashboard:', err);
      }
    });
  }

  offerHelp(requestId: string): void {
    console.log('Offering help with helper email:', this.helperEmail, 'for request:', requestId);
    this.dashboardService.offerHelp(this.helperEmail, requestId).subscribe({
      next: (response) => {
        if (response.success) {
          alert('Request completed successfully! Thank you for helping.');
          // Reload dashboard to show updated counts
          this.loadDashboardData();
        }
      },
      error: (err) => {
        console.error('Error offering help:', err);
        alert('Failed to complete request. Please try again.');
      }
    });
  }
}
