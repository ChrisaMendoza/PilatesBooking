import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ClassTypeResolve from './route/class-type-routing-resolve.service';

const classTypeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/class-type.component').then(m => m.ClassTypeComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/class-type-detail.component').then(m => m.ClassTypeDetailComponent),
    resolve: {
      classType: ClassTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/class-type-update.component').then(m => m.ClassTypeUpdateComponent),
    resolve: {
      classType: ClassTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/class-type-update.component').then(m => m.ClassTypeUpdateComponent),
    resolve: {
      classType: ClassTypeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default classTypeRoute;
