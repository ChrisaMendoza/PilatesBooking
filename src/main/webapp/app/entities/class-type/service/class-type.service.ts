import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClassType, NewClassType } from '../class-type.model';

export type PartialUpdateClassType = Partial<IClassType> & Pick<IClassType, 'id'>;

export type EntityResponseType = HttpResponse<IClassType>;
export type EntityArrayResponseType = HttpResponse<IClassType[]>;

@Injectable({ providedIn: 'root' })
export class ClassTypeService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/class-types');

  create(classType: NewClassType): Observable<EntityResponseType> {
    return this.http.post<IClassType>(this.resourceUrl, classType, { observe: 'response' });
  }

  update(classType: IClassType): Observable<EntityResponseType> {
    return this.http.put<IClassType>(`${this.resourceUrl}/${this.getClassTypeIdentifier(classType)}`, classType, { observe: 'response' });
  }

  partialUpdate(classType: PartialUpdateClassType): Observable<EntityResponseType> {
    return this.http.patch<IClassType>(`${this.resourceUrl}/${this.getClassTypeIdentifier(classType)}`, classType, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClassType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClassType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClassTypeIdentifier(classType: Pick<IClassType, 'id'>): number {
    return classType.id;
  }

  compareClassType(o1: Pick<IClassType, 'id'> | null, o2: Pick<IClassType, 'id'> | null): boolean {
    return o1 && o2 ? this.getClassTypeIdentifier(o1) === this.getClassTypeIdentifier(o2) : o1 === o2;
  }

  addClassTypeToCollectionIfMissing<Type extends Pick<IClassType, 'id'>>(
    classTypeCollection: Type[],
    ...classTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const classTypes: Type[] = classTypesToCheck.filter(isPresent);
    if (classTypes.length > 0) {
      const classTypeCollectionIdentifiers = classTypeCollection.map(classTypeItem => this.getClassTypeIdentifier(classTypeItem));
      const classTypesToAdd = classTypes.filter(classTypeItem => {
        const classTypeIdentifier = this.getClassTypeIdentifier(classTypeItem);
        if (classTypeCollectionIdentifiers.includes(classTypeIdentifier)) {
          return false;
        }
        classTypeCollectionIdentifiers.push(classTypeIdentifier);
        return true;
      });
      return [...classTypesToAdd, ...classTypeCollection];
    }
    return classTypeCollection;
  }
}
