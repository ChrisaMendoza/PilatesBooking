import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IStudio } from '../studio.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../studio.test-samples';

import { StudioService } from './studio.service';

const requireRestSample: IStudio = {
  ...sampleWithRequiredData,
};

describe('Studio Service', () => {
  let service: StudioService;
  let httpMock: HttpTestingController;
  let expectedResult: IStudio | IStudio[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(StudioService);
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

    it('should create a Studio', () => {
      const studio = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(studio).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Studio', () => {
      const studio = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(studio).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Studio', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Studio', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Studio', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addStudioToCollectionIfMissing', () => {
      it('should add a Studio to an empty array', () => {
        const studio: IStudio = sampleWithRequiredData;
        expectedResult = service.addStudioToCollectionIfMissing([], studio);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(studio);
      });

      it('should not add a Studio to an array that contains it', () => {
        const studio: IStudio = sampleWithRequiredData;
        const studioCollection: IStudio[] = [
          {
            ...studio,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addStudioToCollectionIfMissing(studioCollection, studio);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Studio to an array that doesn't contain it", () => {
        const studio: IStudio = sampleWithRequiredData;
        const studioCollection: IStudio[] = [sampleWithPartialData];
        expectedResult = service.addStudioToCollectionIfMissing(studioCollection, studio);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(studio);
      });

      it('should add only unique Studio to an array', () => {
        const studioArray: IStudio[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const studioCollection: IStudio[] = [sampleWithRequiredData];
        expectedResult = service.addStudioToCollectionIfMissing(studioCollection, ...studioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const studio: IStudio = sampleWithRequiredData;
        const studio2: IStudio = sampleWithPartialData;
        expectedResult = service.addStudioToCollectionIfMissing([], studio, studio2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(studio);
        expect(expectedResult).toContain(studio2);
      });

      it('should accept null and undefined values', () => {
        const studio: IStudio = sampleWithRequiredData;
        expectedResult = service.addStudioToCollectionIfMissing([], null, studio, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(studio);
      });

      it('should return initial array if no Studio is added', () => {
        const studioCollection: IStudio[] = [sampleWithRequiredData];
        expectedResult = service.addStudioToCollectionIfMissing(studioCollection, undefined, null);
        expect(expectedResult).toEqual(studioCollection);
      });
    });

    describe('compareStudio', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareStudio(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24793 };
        const entity2 = null;

        const compareResult1 = service.compareStudio(entity1, entity2);
        const compareResult2 = service.compareStudio(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24793 };
        const entity2 = { id: 1489 };

        const compareResult1 = service.compareStudio(entity1, entity2);
        const compareResult2 = service.compareStudio(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 24793 };
        const entity2 = { id: 24793 };

        const compareResult1 = service.compareStudio(entity1, entity2);
        const compareResult2 = service.compareStudio(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
