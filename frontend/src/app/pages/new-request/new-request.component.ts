import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-new-request',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule],
  templateUrl: './new-request.component.html',
  styleUrls: ['./new-request.component.css']
})
export class NewRequestComponent {
  // 📝 Form data model
  request = {
    title: '',
    category: '',
    description: '',
    location: '',
    urgency: '',
    seekerEmail: ''
  };

  // Dynamic options
  categories = ['Education', 'Household', 'Projects', 'Emotional Support', 'Daily Needs'];
  urgencies = ['Low', 'Medium', 'High'];

  constructor(private router: Router, private http: HttpClient) {}

  // 📝 Handles form submission
  submitRequest(form: NgForm) {
    if (form.valid) {
      this.http.post('http://localhost:8080/api/requests/new', this.request).subscribe({
        next: (response) => {
          console.log('Request saved:', response);
          alert('✅ Your request has been submitted successfully!');
          form.resetForm(); // Reset the form
          this.router.navigate(['/seeker-dashboard']); // Navigate to dashboard
        },
        error: (error) => {
          console.error('Error:', error);
          alert('❌ Failed to submit request. Please try again.');
        }
      });
    } else {
      alert('⚠️ Please fill all fields before submitting.');
    }
  }

  // 📝 Cancel and return to dashboard
  cancel() {
    this.router.navigate(['/seeker-dashboard']);
  }
}
