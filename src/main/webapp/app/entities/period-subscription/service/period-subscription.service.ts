import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPeriodSubscription, NewPeriodSubscription } from '../period-subscription.model';

export type PartialUpdatePeriodSubscription = Partial<IPeriodSubscription> & Pick<IPeriodSubscription, 'id'>;

type RestOf<T extends IPeriodSubscription | NewPeriodSubscription> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestPeriodSubscription = RestOf<IPeriodSubscription>;

export type NewRestPeriodSubscription = RestOf<NewPeriodSubscription>;

export type PartialUpdateRestPeriodSubscription = RestOf<PartialUpdatePeriodSubscription>;

export type EntityResponseType = HttpResponse<IPeriodSubscription>;
export type EntityArrayResponseType = HttpResponse<IPeriodSubscription[]>;

@Injectable({ providedIn: 'root' })
export class PeriodSubscriptionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/period-subscriptions');

  create(periodSubscription: NewPeriodSubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(periodSubscription);
    return this.http
      .post<RestPeriodSubscription>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(periodSubscription: IPeriodSubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(periodSubscription);
    return this.http
      .put<RestPeriodSubscription>(`${this.resourceUrl}/${this.getPeriodSubscriptionIdentifier(periodSubscription)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(periodSubscription: PartialUpdatePeriodSubscription): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(periodSubscription);
    return this.http
      .patch<RestPeriodSubscription>(`${this.resourceUrl}/${this.getPeriodSubscriptionIdentifier(periodSubscription)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestPeriodSubscription>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPeriodSubscription[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPeriodSubscriptionIdentifier(periodSubscription: Pick<IPeriodSubscription, 'id'>): number {
    return periodSubscription.id;
  }

  comparePeriodSubscription(o1: Pick<IPeriodSubscription, 'id'> | null, o2: Pick<IPeriodSubscription, 'id'> | null): boolean {
    return o1 && o2 ? this.getPeriodSubscriptionIdentifier(o1) === this.getPeriodSubscriptionIdentifier(o2) : o1 === o2;
  }

  addPeriodSubscriptionToCollectionIfMissing<Type extends Pick<IPeriodSubscription, 'id'>>(
    periodSubscriptionCollection: Type[],
    ...periodSubscriptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const periodSubscriptions: Type[] = periodSubscriptionsToCheck.filter(isPresent);
    if (periodSubscriptions.length > 0) {
      const periodSubscriptionCollectionIdentifiers = periodSubscriptionCollection.map(periodSubscriptionItem =>
        this.getPeriodSubscriptionIdentifier(periodSubscriptionItem),
      );
      const periodSubscriptionsToAdd = periodSubscriptions.filter(periodSubscriptionItem => {
        const periodSubscriptionIdentifier = this.getPeriodSubscriptionIdentifier(periodSubscriptionItem);
        if (periodSubscriptionCollectionIdentifiers.includes(periodSubscriptionIdentifier)) {
          return false;
        }
        periodSubscriptionCollectionIdentifiers.push(periodSubscriptionIdentifier);
        return true;
      });
      return [...periodSubscriptionsToAdd, ...periodSubscriptionCollection];
    }
    return periodSubscriptionCollection;
  }

  protected convertDateFromClient<T extends IPeriodSubscription | NewPeriodSubscription | PartialUpdatePeriodSubscription>(
    periodSubscription: T,
  ): RestOf<T> {
    return {
      ...periodSubscription,
      startDate: periodSubscription.startDate?.format(DATE_FORMAT) ?? null,
      endDate: periodSubscription.endDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restPeriodSubscription: RestPeriodSubscription): IPeriodSubscription {
    return {
      ...restPeriodSubscription,
      startDate: restPeriodSubscription.startDate ? dayjs(restPeriodSubscription.startDate) : undefined,
      endDate: restPeriodSubscription.endDate ? dayjs(restPeriodSubscription.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestPeriodSubscription>): HttpResponse<IPeriodSubscription> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestPeriodSubscription[]>): HttpResponse<IPeriodSubscription[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
