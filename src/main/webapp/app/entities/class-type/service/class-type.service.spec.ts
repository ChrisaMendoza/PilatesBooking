import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IClassType } from '../class-type.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../class-type.test-samples';

import { ClassTypeService } from './class-type.service';

const requireRestSample: IClassType = {
  ...sampleWithRequiredData,
};

describe('ClassType Service', () => {
  let service: ClassTypeService;
  let httpMock: HttpTestingController;
  let expectedResult: IClassType | IClassType[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ClassTypeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ClassType', () => {
      const classType = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(classType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClassType', () => {
      const classType = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(classType).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClassType', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClassType', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ClassType', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClassTypeToCollectionIfMissing', () => {
      it('should add a ClassType to an empty array', () => {
        const classType: IClassType = sampleWithRequiredData;
        expectedResult = service.addClassTypeToCollectionIfMissing([], classType);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(classType);
      });

      it('should not add a ClassType to an array that contains it', () => {
        const classType: IClassType = sampleWithRequiredData;
        const classTypeCollection: IClassType[] = [
          {
            ...classType,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClassTypeToCollectionIfMissing(classTypeCollection, classType);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClassType to an array that doesn't contain it", () => {
        const classType: IClassType = sampleWithRequiredData;
        const classTypeCollection: IClassType[] = [sampleWithPartialData];
        expectedResult = service.addClassTypeToCollectionIfMissing(classTypeCollection, classType);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(classType);
      });

      it('should add only unique ClassType to an array', () => {
        const classTypeArray: IClassType[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const classTypeCollection: IClassType[] = [sampleWithRequiredData];
        expectedResult = service.addClassTypeToCollectionIfMissing(classTypeCollection, ...classTypeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const classType: IClassType = sampleWithRequiredData;
        const classType2: IClassType = sampleWithPartialData;
        expectedResult = service.addClassTypeToCollectionIfMissing([], classType, classType2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(classType);
        expect(expectedResult).toContain(classType2);
      });

      it('should accept null and undefined values', () => {
        const classType: IClassType = sampleWithRequiredData;
        expectedResult = service.addClassTypeToCollectionIfMissing([], null, classType, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(classType);
      });

      it('should return initial array if no ClassType is added', () => {
        const classTypeCollection: IClassType[] = [sampleWithRequiredData];
        expectedResult = service.addClassTypeToCollectionIfMissing(classTypeCollection, undefined, null);
        expect(expectedResult).toEqual(classTypeCollection);
      });
    });

    describe('compareClassType', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClassType(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20843 };
        const entity2 = null;

        const compareResult1 = service.compareClassType(entity1, entity2);
        const compareResult2 = service.compareClassType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20843 };
        const entity2 = { id: 27871 };

        const compareResult1 = service.compareClassType(entity1, entity2);
        const compareResult2 = service.compareClassType(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20843 };
        const entity2 = { id: 20843 };

        const compareResult1 = service.compareClassType(entity1, entity2);
        const compareResult2 = service.compareClassType(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
