import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ClassSessionResolve from './route/class-session-routing-resolve.service';

const classSessionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/class-session.component').then(m => m.ClassSessionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/class-session-detail.component').then(m => m.ClassSessionDetailComponent),
    resolve: {
      classSession: ClassSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/class-session-update.component').then(m => m.ClassSessionUpdateComponent),
    resolve: {
      classSession: ClassSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/class-session-update.component').then(m => m.ClassSessionUpdateComponent),
    resolve: {
      classSession: ClassSessionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default classSessionRoute;
