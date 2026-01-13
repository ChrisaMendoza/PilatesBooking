import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStudio } from '../studio.model';
import { StudioService } from '../service/studio.service';

const studioResolve = (route: ActivatedRouteSnapshot): Observable<null | IStudio> => {
  const id = route.params.id;
  if (id) {
    return inject(StudioService)
      .find(id)
      .pipe(
        mergeMap((studio: HttpResponse<IStudio>) => {
          if (studio.body) {
            return of(studio.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default studioResolve;
