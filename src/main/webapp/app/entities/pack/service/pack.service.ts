import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPack, NewPack } from '../pack.model';

export type PartialUpdatePack = Partial<IPack> & Pick<IPack, 'id'>;

export type EntityResponseType = HttpResponse<IPack>;
export type EntityArrayResponseType = HttpResponse<IPack[]>;

@Injectable({ providedIn: 'root' })
export class PackService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/packs');

  create(pack: NewPack): Observable<EntityResponseType> {
    return this.http.post<IPack>(this.resourceUrl, pack, { observe: 'response' });
  }

  update(pack: IPack): Observable<EntityResponseType> {
    return this.http.put<IPack>(`${this.resourceUrl}/${this.getPackIdentifier(pack)}`, pack, { observe: 'response' });
  }

  partialUpdate(pack: PartialUpdatePack): Observable<EntityResponseType> {
    return this.http.patch<IPack>(`${this.resourceUrl}/${this.getPackIdentifier(pack)}`, pack, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPack>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPack[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPackIdentifier(pack: Pick<IPack, 'id'>): number {
    return pack.id;
  }

  comparePack(o1: Pick<IPack, 'id'> | null, o2: Pick<IPack, 'id'> | null): boolean {
    return o1 && o2 ? this.getPackIdentifier(o1) === this.getPackIdentifier(o2) : o1 === o2;
  }

  addPackToCollectionIfMissing<Type extends Pick<IPack, 'id'>>(
    packCollection: Type[],
    ...packsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const packs: Type[] = packsToCheck.filter(isPresent);
    if (packs.length > 0) {
      const packCollectionIdentifiers = packCollection.map(packItem => this.getPackIdentifier(packItem));
      const packsToAdd = packs.filter(packItem => {
        const packIdentifier = this.getPackIdentifier(packItem);
        if (packCollectionIdentifiers.includes(packIdentifier)) {
          return false;
        }
        packCollectionIdentifiers.push(packIdentifier);
        return true;
      });
      return [...packsToAdd, ...packCollection];
    }
    return packCollection;
  }
}
