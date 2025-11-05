import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { GetStartedComponent } from './pages/get-started/get-started.component';
import { LearnMoreComponent } from './pages/learn-more/learn-more.component';
import { AuthComponent } from './pages/auth/auth.component';
import { SeekerDashboardComponent } from './pages/seeker-dashboard/seeker-dashboard.component';
import { HelperDashboardComponent } from './pages/helper-dashboard/helper-dashboard.component';
import { NewRequestComponent } from './pages/new-request/new-request.component'; // ✅ Import the new request page

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
    pathMatch: 'full',
  },
  {
    path: 'get-started',
    component: GetStartedComponent,
  },
  {
    path: 'helper-dashboard',
    component: HelperDashboardComponent,
  },
  {
    path: 'seeker-dashboard',
    component: SeekerDashboardComponent,
  },
  {
    path: 'new-request', // ✅ Add this route for Create New Request page
    component: NewRequestComponent,
  },
  {
    path: 'learn-more',
    component: LearnMoreComponent,
  },
  {
    path: 'auth',
    component: AuthComponent,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
