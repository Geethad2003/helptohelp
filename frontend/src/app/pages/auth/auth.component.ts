import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {
  private router = inject(Router);
  private authService = inject(AuthService);

  name: string = '';
  email: string = '';
  password: string = '';
  role: 'HELPER' | 'SEEKER' = 'SEEKER'; // Type-safe role
  otp: string = '';
  errorMessage: string = '';
  successMessage: string = '';

  isLoginMode: boolean = true;
  showOtpInput: boolean = false;
  isLoading: boolean = false;

  toggleMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.showOtpInput = false;
    this.errorMessage = '';
    this.successMessage = '';
  }

  async sendOtp(): Promise<void> {
    if (!this.email) {
      this.errorMessage = 'Please enter your email';
      return;
    }

    try {
      this.isLoading = true;
      const response = await this.authService.sendOtp(this.email, this.role).toPromise();
      this.showOtpInput = true;
      this.successMessage = response?.message || 'OTP sent successfully!';
      this.errorMessage = '';
    } catch (error: any) {
      this.errorMessage = error.error?.message || 'Failed to send OTP';
      this.successMessage = '';
    } finally {
      this.isLoading = false;
    }
  }

  async verifyOtpAndSignup() {
    if (!this.otp) {
      this.errorMessage = 'Please enter the OTP';
      return;
    }

    try {
      this.isLoading = true;
      const verifyResponse = await this.authService.verifyOtp(this.email, this.otp).toPromise();
      
      if (verifyResponse?.success) {
        const signupResponse = await this.authService.signup(this.name, this.email, this.password, this.role).toPromise();
        localStorage.setItem('token', signupResponse!.token || 'dummy-token');
        localStorage.setItem('role', this.role);
        localStorage.setItem('email', this.email); // Use the email from the signup form
        
        const redirectPath = signupResponse?.role === 'HELPER' ? '/helper-dashboard' : '/seeker-dashboard';

              // const redirectPath = '/get-started';

        await this.router.navigate([redirectPath]);
      } else {
        this.errorMessage = 'Invalid OTP';
      }
    } catch (error: any) {
      this.errorMessage = error.error?.message || 'Verification failed';
    } finally {
      this.isLoading = false;
    }
  }

  async login() {
    try {
      this.isLoading = true;
      const response = await this.authService.login(this.email, this.password).toPromise();

      localStorage.setItem('token', response!.token || 'dummy-token');
      localStorage.setItem('role', response!.role);
      localStorage.setItem('email', this.email); // Use the email from the login form

      // Redirect based on role
      const redirectPath =  response?.role === 'HELPER' ? '/helper-dashboard' : '/seeker-dashboard'; 
      await this.router.navigate([redirectPath]);
    } catch (error: any) {
      this.errorMessage = error.error?.message || 'Login failed';
    } finally {
      this.isLoading = false;
    }
  }

  async onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.email || !this.password) {
      this.errorMessage = 'Please fill in all fields';
      return;
    }

    try {
      if (this.isLoginMode) {
        await this.login();
      } else {
        if (!this.showOtpInput) {
          await this.sendOtp();
        } else {
          await this.verifyOtpAndSignup();
        }
      }
    } catch (error: any) {
      this.errorMessage = error.error?.message || 'Authentication failed';
    }
  }

  async resendOtp() {
    this.errorMessage = '';
    this.successMessage = '';
    await this.sendOtp();
  }
}