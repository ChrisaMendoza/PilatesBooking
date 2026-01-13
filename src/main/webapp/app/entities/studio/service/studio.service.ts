import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStudio, NewStudio } from '../studio.model';

export type PartialUpdateStudio = Partial<IStudio> & Pick<IStudio, 'id'>;

export type EntityResponseType = HttpResponse<IStudio>;
export type EntityArrayResponseType = HttpResponse<IStudio[]>;

@Injectable({ providedIn: 'root' })
export class StudioService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/studios');

  create(studio: NewStudio): Observable<EntityResponseType> {
    return this.http.post<IStudio>(this.resourceUrl, studio, { observe: 'response' });
  }

  update(studio: IStudio): Observable<EntityResponseType> {
    return this.http.put<IStudio>(`${this.resourceUrl}/${this.getStudioIdentifier(studio)}`, studio, { observe: 'response' });
  }

  partialUpdate(studio: PartialUpdateStudio): Observable<EntityResponseType> {
    return this.http.patch<IStudio>(`${this.resourceUrl}/${this.getStudioIdentifier(studio)}`, studio, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStudio>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStudio[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getStudioIdentifier(studio: Pick<IStudio, 'id'>): number {
    return studio.id;
  }

  compareStudio(o1: Pick<IStudio, 'id'> | null, o2: Pick<IStudio, 'id'> | null): boolean {
    return o1 && o2 ? this.getStudioIdentifier(o1) === this.getStudioIdentifier(o2) : o1 === o2;
  }

  addStudioToCollectionIfMissing<Type extends Pick<IStudio, 'id'>>(
    studioCollection: Type[],
    ...studiosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const studios: Type[] = studiosToCheck.filter(isPresent);
    if (studios.length > 0) {
      const studioCollectionIdentifiers = studioCollection.map(studioItem => this.getStudioIdentifier(studioItem));
      const studiosToAdd = studios.filter(studioItem => {
        const studioIdentifier = this.getStudioIdentifier(studioItem);
        if (studioCollectionIdentifiers.includes(studioIdentifier)) {
          return false;
        }
        studioCollectionIdentifiers.push(studioIdentifier);
        return true;
      });
      return [...studiosToAdd, ...studioCollection];
    }
    return studioCollection;
  }
}
