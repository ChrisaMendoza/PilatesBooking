import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClassSession, NewClassSession } from '../class-session.model';

export type PartialUpdateClassSession = Partial<IClassSession> & Pick<IClassSession, 'id'>;

type RestOf<T extends IClassSession | NewClassSession> = Omit<T, 'startAt' | 'endAt'> & {
  startAt?: string | null;
  endAt?: string | null;
};

export type RestClassSession = RestOf<IClassSession>;

export type NewRestClassSession = RestOf<NewClassSession>;

export type PartialUpdateRestClassSession = RestOf<PartialUpdateClassSession>;

export type EntityResponseType = HttpResponse<IClassSession>;
export type EntityArrayResponseType = HttpResponse<IClassSession[]>;

@Injectable({ providedIn: 'root' })
export class ClassSessionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/class-sessions');

  create(classSession: NewClassSession): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(classSession);
    return this.http
      .post<RestClassSession>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(classSession: IClassSession): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(classSession);
    return this.http
      .put<RestClassSession>(`${this.resourceUrl}/${this.getClassSessionIdentifier(classSession)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(classSession: PartialUpdateClassSession): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(classSession);
    return this.http
      .patch<RestClassSession>(`${this.resourceUrl}/${this.getClassSessionIdentifier(classSession)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestClassSession>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestClassSession[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClassSessionIdentifier(classSession: Pick<IClassSession, 'id'>): number {
    return classSession.id;
  }

  compareClassSession(o1: Pick<IClassSession, 'id'> | null, o2: Pick<IClassSession, 'id'> | null): boolean {
    return o1 && o2 ? this.getClassSessionIdentifier(o1) === this.getClassSessionIdentifier(o2) : o1 === o2;
  }

  addClassSessionToCollectionIfMissing<Type extends Pick<IClassSession, 'id'>>(
    classSessionCollection: Type[],
    ...classSessionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const classSessions: Type[] = classSessionsToCheck.filter(isPresent);
    if (classSessions.length > 0) {
      const classSessionCollectionIdentifiers = classSessionCollection.map(classSessionItem =>
        this.getClassSessionIdentifier(classSessionItem),
      );
      const classSessionsToAdd = classSessions.filter(classSessionItem => {
        const classSessionIdentifier = this.getClassSessionIdentifier(classSessionItem);
        if (classSessionCollectionIdentifiers.includes(classSessionIdentifier)) {
          return false;
        }
        classSessionCollectionIdentifiers.push(classSessionIdentifier);
        return true;
      });
      return [...classSessionsToAdd, ...classSessionCollection];
    }
    return classSessionCollection;
  }

  protected convertDateFromClient<T extends IClassSession | NewClassSession | PartialUpdateClassSession>(classSession: T): RestOf<T> {
    return {
      ...classSession,
      startAt: classSession.startAt?.toJSON() ?? null,
      endAt: classSession.endAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restClassSession: RestClassSession): IClassSession {
    return {
      ...restClassSession,
      startAt: restClassSession.startAt ? dayjs(restClassSession.startAt) : undefined,
      endAt: restClassSession.endAt ? dayjs(restClassSession.endAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestClassSession>): HttpResponse<IClassSession> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestClassSession[]>): HttpResponse<IClassSession[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
