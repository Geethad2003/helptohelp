import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DashboardService } from '../../services/dashboard.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-seeker-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './seeker-dashboard.component.html',
  styleUrls: ['./seeker-dashboard.component.css']
})
export class SeekerDashboardComponent implements OnInit {
  activeRequestsCount = 0;
  completedRequestsCount = 0;
  availableHelpers = 0;
  activeRequests: any[] = [];
  completedRequests: any[] = [];
  helpersList: any[] = [];
  showHelpersList = false;
  seekerEmail = '';
  
  // Feedback modal properties
  showFeedbackForm = false;
  selectedRequest: any = null;
  feedbackRating = 0;
  feedbackDescription = '';

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // ✅ Retrieve email from localStorage (set during login)
    this.seekerEmail = localStorage.getItem('email') || 'seeker@example.com';

    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.dashboardService.getSeekerDashboardData().subscribe((data) => {
      this.activeRequestsCount = data.activeRequestsCount;
      this.completedRequestsCount = data.completedRequestsCount;
      this.availableHelpers = data.availableHelpers;
      this.activeRequests = data.activeRequests;
      this.completedRequests = data.completedRequestsList;
    });
  }

  // 📝 Navigate to the "Create New Request" page
  goToNewRequest(): void {
    this.router.navigate(['/new-request']);
  }

  // 📝 Browse available helpers
  browseAvailableHelpers(): void {
    this.showHelpersList = !this.showHelpersList;
    
    if (this.showHelpersList && this.helpersList.length === 0) {
      this.dashboardService.getAvailableHelpers().subscribe({
        next: (helpers) => {
          this.helpersList = helpers;
        },
        error: (err) => {
          console.error('Error loading helpers:', err);
          alert('Failed to load helpers. Please try again.');
        }
      });
    }
  }

  // 📝 Contact a helper
  contactHelper(helperEmail: string): void {
    const message = `Hello,

I am looking for assistance and found your profile as an available helper. I would appreciate it if you could reach out to me to discuss how you might be able to help.

You can contact me at: ${this.seekerEmail}

Thank you for your time and willingness to help!

Best regards`;
    
    this.dashboardService.contactHelper(this.seekerEmail, helperEmail, message).subscribe({
      next: (response) => {
        if (response.success) {
          alert(`Message sent to ${helperEmail} successfully! They will receive your contact request.`);
        }
      },
      error: (err) => {
        console.error('Error contacting helper:', err);
        alert('Failed to send message. Please try again.');
      }
    });
  }

  // 📝 Extract display name from email
  getDisplayName(email: string): string {
    if (!email) return 'Helper';
    
    const emailPrefix = email.split('@')[0];
    // Capitalize first letter and make it more readable
    return emailPrefix.charAt(0).toUpperCase() + emailPrefix.slice(1).replace(/[._-]/g, ' ') + ' (Helper)';
  }

  // � Open Gmail to compose message
  copyEmail(email: string): void {
    if (!email || email === 'Unknown Helper') {
      alert('No email available');
      return;
    }

    // Create email content
    const subject = 'Help Request from Help to Help Platform';
    const body = `Hello,

I am reaching out from the Help to Help platform. I would appreciate your assistance with my request.

You can contact me at: ${this.seekerEmail}

Thank you for your willingness to help!

Best regards`;

    // Encode the subject and body for URL
    const encodedSubject = encodeURIComponent(subject);
    const encodedBody = encodeURIComponent(body);

    // Create Gmail compose URL
    const gmailUrl = `https://mail.google.com/mail/?view=cm&fs=1&to=${email}&su=${encodedSubject}&body=${encodedBody}`;

    // Open Gmail in new tab
    window.open(gmailUrl, '_blank');

    // Also copy email to clipboard as backup
    navigator.clipboard.writeText(email).then(() => {
      console.log('Email copied to clipboard as backup');
    }).catch(() => {
      console.log('Could not copy to clipboard');
    });
  }

  // ⭐ Show feedback modal
  showFeedbackModal(request: any): void {
    this.selectedRequest = request;
    this.showFeedbackForm = true;
    this.feedbackRating = 0;
    this.feedbackDescription = '';
  }

  // ❌ Close feedback modal
  closeFeedbackModal(): void {
    this.showFeedbackForm = false;
    this.selectedRequest = null;
    this.feedbackRating = 0;
    this.feedbackDescription = '';
  }

  // ⭐ Set rating
  setRating(rating: number): void {
    this.feedbackRating = rating;
  }

  // 📝 Get rating text
  getRatingText(rating: number): string {
    const ratingTexts: { [key: number]: string } = {
      0: 'Please select a rating',
      1: 'Poor - Very unsatisfied',
      2: 'Fair - Below expectations',
      3: 'Good - Met expectations',
      4: 'Very Good - Exceeded expectations',
      5: 'Excellent - Outstanding help!'
    };
    return ratingTexts[rating] || '';
  }

  // ✨ Submit feedback
  submitFeedback(): void {
    if (this.feedbackRating === 0) {
      alert('Please select a rating before submitting.');
      return;
    }

    const feedbackData = {
      requestId: this.selectedRequest.id,
      helperEmail: this.selectedRequest.helperEmail,
      seekerEmail: this.seekerEmail,
      rating: this.feedbackRating,
      description: this.feedbackDescription.trim(),
      requestTitle: this.selectedRequest.title,
      submittedAt: new Date().toISOString()
    };

    // Send feedback to backend
    this.dashboardService.submitFeedback(feedbackData).subscribe({
      next: (response: any) => {
        alert(`✨ Thank you for your feedback!\n\nRating: ${this.feedbackRating}/5 stars\nYour feedback helps us improve our community.`);
        this.closeFeedbackModal();
      },
      error: (error: any) => {
        console.error('Error submitting feedback:', error);
        alert('Failed to submit feedback. Please try again.');
      }
    });
  }
}
