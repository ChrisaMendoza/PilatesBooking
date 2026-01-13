import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPeriodSubscription } from '../period-subscription.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../period-subscription.test-samples';

import { PeriodSubscriptionService, RestPeriodSubscription } from './period-subscription.service';

const requireRestSample: RestPeriodSubscription = {
  ...sampleWithRequiredData,
  startDate: sampleWithRequiredData.startDate?.format(DATE_FORMAT),
  endDate: sampleWithRequiredData.endDate?.format(DATE_FORMAT),
};

describe('PeriodSubscription Service', () => {
  let service: PeriodSubscriptionService;
  let httpMock: HttpTestingController;
  let expectedResult: IPeriodSubscription | IPeriodSubscription[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PeriodSubscriptionService);
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

    it('should create a PeriodSubscription', () => {
      const periodSubscription = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(periodSubscription).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PeriodSubscription', () => {
      const periodSubscription = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(periodSubscription).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PeriodSubscription', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PeriodSubscription', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PeriodSubscription', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPeriodSubscriptionToCollectionIfMissing', () => {
      it('should add a PeriodSubscription to an empty array', () => {
        const periodSubscription: IPeriodSubscription = sampleWithRequiredData;
        expectedResult = service.addPeriodSubscriptionToCollectionIfMissing([], periodSubscription);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(periodSubscription);
      });

      it('should not add a PeriodSubscription to an array that contains it', () => {
        const periodSubscription: IPeriodSubscription = sampleWithRequiredData;
        const periodSubscriptionCollection: IPeriodSubscription[] = [
          {
            ...periodSubscription,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPeriodSubscriptionToCollectionIfMissing(periodSubscriptionCollection, periodSubscription);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PeriodSubscription to an array that doesn't contain it", () => {
        const periodSubscription: IPeriodSubscription = sampleWithRequiredData;
        const periodSubscriptionCollection: IPeriodSubscription[] = [sampleWithPartialData];
        expectedResult = service.addPeriodSubscriptionToCollectionIfMissing(periodSubscriptionCollection, periodSubscription);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(periodSubscription);
      });

      it('should add only unique PeriodSubscription to an array', () => {
        const periodSubscriptionArray: IPeriodSubscription[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const periodSubscriptionCollection: IPeriodSubscription[] = [sampleWithRequiredData];
        expectedResult = service.addPeriodSubscriptionToCollectionIfMissing(periodSubscriptionCollection, ...periodSubscriptionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const periodSubscription: IPeriodSubscription = sampleWithRequiredData;
        const periodSubscription2: IPeriodSubscription = sampleWithPartialData;
        expectedResult = service.addPeriodSubscriptionToCollectionIfMissing([], periodSubscription, periodSubscription2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(periodSubscription);
        expect(expectedResult).toContain(periodSubscription2);
      });

      it('should accept null and undefined values', () => {
        const periodSubscription: IPeriodSubscription = sampleWithRequiredData;
        expectedResult = service.addPeriodSubscriptionToCollectionIfMissing([], null, periodSubscription, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(periodSubscription);
      });

      it('should return initial array if no PeriodSubscription is added', () => {
        const periodSubscriptionCollection: IPeriodSubscription[] = [sampleWithRequiredData];
        expectedResult = service.addPeriodSubscriptionToCollectionIfMissing(periodSubscriptionCollection, undefined, null);
        expect(expectedResult).toEqual(periodSubscriptionCollection);
      });
    });

    describe('comparePeriodSubscription', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePeriodSubscription(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 25656 };
        const entity2 = null;

        const compareResult1 = service.comparePeriodSubscription(entity1, entity2);
        const compareResult2 = service.comparePeriodSubscription(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 25656 };
        const entity2 = { id: 8229 };

        const compareResult1 = service.comparePeriodSubscription(entity1, entity2);
        const compareResult2 = service.comparePeriodSubscription(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 25656 };
        const entity2 = { id: 25656 };

        const compareResult1 = service.comparePeriodSubscription(entity1, entity2);
        const compareResult2 = service.comparePeriodSubscription(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
