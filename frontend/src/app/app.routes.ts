import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LearnMoreComponent } from './pages/learn-more/learn-more.component';
import { GetStartedComponent } from './pages/get-started/get-started.component';
import { AuthComponent } from './pages/auth/auth.component';
import { HelperDashboardComponent } from './pages/helper-dashboard/helper-dashboard.component';
import { SeekerDashboardComponent } from './pages/seeker-dashboard/seeker-dashboard.component';
import { NewRequestComponent } from './pages/new-request/new-request.component';


export const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'learn-more', component: LearnMoreComponent },
  { path: 'get-started', component: GetStartedComponent },
  { path: 'helper-dashboard', component: HelperDashboardComponent },
  { path: 'seeker-dashboard', component: SeekerDashboardComponent },
  { path: 'auth', component: AuthComponent },
  { path: 'new-request', component: NewRequestComponent },
];
