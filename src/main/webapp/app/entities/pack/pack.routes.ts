import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PackResolve from './route/pack-routing-resolve.service';

const packRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/pack.component').then(m => m.PackComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/pack-detail.component').then(m => m.PackDetailComponent),
    resolve: {
      pack: PackResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/pack-update.component').then(m => m.PackUpdateComponent),
    resolve: {
      pack: PackResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/pack-update.component').then(m => m.PackUpdateComponent),
    resolve: {
      pack: PackResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default packRoute;
