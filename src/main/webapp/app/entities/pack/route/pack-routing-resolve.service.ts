import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPack } from '../pack.model';
import { PackService } from '../service/pack.service';

const packResolve = (route: ActivatedRouteSnapshot): Observable<null | IPack> => {
  const id = route.params.id;
  if (id) {
    return inject(PackService)
      .find(id)
      .pipe(
        mergeMap((pack: HttpResponse<IPack>) => {
          if (pack.body) {
            return of(pack.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default packResolve;
