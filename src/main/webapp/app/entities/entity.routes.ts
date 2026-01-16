import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'studio',
    data: { pageTitle: 'Studios' },
    loadChildren: () => import('./studio/studio.routes'),
  },
  {
    path: 'class-type',
    data: { pageTitle: 'ClassTypes' },
    loadChildren: () => import('./class-type/class-type.routes'),
  },
  {
    path: 'class-session',
    data: { pageTitle: 'ClassSessions' },
    loadChildren: () => import('./class-session/class-session.routes'),
  },
  {
    path: 'booking',
    data: { pageTitle: 'Bookings' },
    loadChildren: () => import('./booking/booking.routes'),
  },
  {
    path: 'period-subscription',
    data: { pageTitle: 'PeriodSubscriptions' },
    loadChildren: () => import('./period-subscription/period-subscription.routes'),
  },
  {
    path: 'pack',
    data: { pageTitle: 'Packs' },
    loadChildren: () => import('./pack/pack.routes'),
  },
  {
    path: 'event',
    data: { pageTitle: 'Events' },
    loadChildren: () => import('./event/event.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
