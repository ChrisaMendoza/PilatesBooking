import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IClassSession } from '../class-session.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../class-session.test-samples';

import { ClassSessionService, RestClassSession } from './class-session.service';

const requireRestSample: RestClassSession = {
  ...sampleWithRequiredData,
  startAt: sampleWithRequiredData.startAt?.toJSON(),
  endAt: sampleWithRequiredData.endAt?.toJSON(),
};

describe('ClassSession Service', () => {
  let service: ClassSessionService;
  let httpMock: HttpTestingController;
  let expectedResult: IClassSession | IClassSession[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ClassSessionService);
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

    it('should create a ClassSession', () => {
      const classSession = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(classSession).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClassSession', () => {
      const classSession = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(classSession).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClassSession', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClassSession', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ClassSession', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClassSessionToCollectionIfMissing', () => {
      it('should add a ClassSession to an empty array', () => {
        const classSession: IClassSession = sampleWithRequiredData;
        expectedResult = service.addClassSessionToCollectionIfMissing([], classSession);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(classSession);
      });

      it('should not add a ClassSession to an array that contains it', () => {
        const classSession: IClassSession = sampleWithRequiredData;
        const classSessionCollection: IClassSession[] = [
          {
            ...classSession,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClassSessionToCollectionIfMissing(classSessionCollection, classSession);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClassSession to an array that doesn't contain it", () => {
        const classSession: IClassSession = sampleWithRequiredData;
        const classSessionCollection: IClassSession[] = [sampleWithPartialData];
        expectedResult = service.addClassSessionToCollectionIfMissing(classSessionCollection, classSession);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(classSession);
      });

      it('should add only unique ClassSession to an array', () => {
        const classSessionArray: IClassSession[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const classSessionCollection: IClassSession[] = [sampleWithRequiredData];
        expectedResult = service.addClassSessionToCollectionIfMissing(classSessionCollection, ...classSessionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const classSession: IClassSession = sampleWithRequiredData;
        const classSession2: IClassSession = sampleWithPartialData;
        expectedResult = service.addClassSessionToCollectionIfMissing([], classSession, classSession2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(classSession);
        expect(expectedResult).toContain(classSession2);
      });

      it('should accept null and undefined values', () => {
        const classSession: IClassSession = sampleWithRequiredData;
        expectedResult = service.addClassSessionToCollectionIfMissing([], null, classSession, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(classSession);
      });

      it('should return initial array if no ClassSession is added', () => {
        const classSessionCollection: IClassSession[] = [sampleWithRequiredData];
        expectedResult = service.addClassSessionToCollectionIfMissing(classSessionCollection, undefined, null);
        expect(expectedResult).toEqual(classSessionCollection);
      });
    });

    describe('compareClassSession', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClassSession(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18095 };
        const entity2 = null;

        const compareResult1 = service.compareClassSession(entity1, entity2);
        const compareResult2 = service.compareClassSession(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18095 };
        const entity2 = { id: 8832 };

        const compareResult1 = service.compareClassSession(entity1, entity2);
        const compareResult2 = service.compareClassSession(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18095 };
        const entity2 = { id: 18095 };

        const compareResult1 = service.compareClassSession(entity1, entity2);
        const compareResult2 = service.compareClassSession(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
