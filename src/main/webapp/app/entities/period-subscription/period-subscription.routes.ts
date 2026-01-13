import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PeriodSubscriptionResolve from './route/period-subscription-routing-resolve.service';

const periodSubscriptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/period-subscription.component').then(m => m.PeriodSubscriptionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/period-subscription-detail.component').then(m => m.PeriodSubscriptionDetailComponent),
    resolve: {
      periodSubscription: PeriodSubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/period-subscription-update.component').then(m => m.PeriodSubscriptionUpdateComponent),
    resolve: {
      periodSubscription: PeriodSubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/period-subscription-update.component').then(m => m.PeriodSubscriptionUpdateComponent),
    resolve: {
      periodSubscription: PeriodSubscriptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default periodSubscriptionRoute;
