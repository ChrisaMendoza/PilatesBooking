import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClassSession } from '../class-session.model';
import { ClassSessionService } from '../service/class-session.service';

const classSessionResolve = (route: ActivatedRouteSnapshot): Observable<null | IClassSession> => {
  const id = route.params.id;
  if (id) {
    return inject(ClassSessionService)
      .find(id)
      .pipe(
        mergeMap((classSession: HttpResponse<IClassSession>) => {
          if (classSession.body) {
            return of(classSession.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default classSessionResolve;
