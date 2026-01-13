import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPeriodSubscription } from '../period-subscription.model';
import { PeriodSubscriptionService } from '../service/period-subscription.service';

const periodSubscriptionResolve = (route: ActivatedRouteSnapshot): Observable<null | IPeriodSubscription> => {
  const id = route.params.id;
  if (id) {
    return inject(PeriodSubscriptionService)
      .find(id)
      .pipe(
        mergeMap((periodSubscription: HttpResponse<IPeriodSubscription>) => {
          if (periodSubscription.body) {
            return of(periodSubscription.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default periodSubscriptionResolve;
