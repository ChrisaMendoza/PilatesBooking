import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import StudioResolve from './route/studio-routing-resolve.service';

const studioRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/studio.component').then(m => m.StudioComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/studio-detail.component').then(m => m.StudioDetailComponent),
    resolve: {
      studio: StudioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/studio-update.component').then(m => m.StudioUpdateComponent),
    resolve: {
      studio: StudioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/studio-update.component').then(m => m.StudioUpdateComponent),
    resolve: {
      studio: StudioResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default studioRoute;
