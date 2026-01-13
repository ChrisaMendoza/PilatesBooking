import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../period-subscription.test-samples';

import { PeriodSubscriptionFormService } from './period-subscription-form.service';

describe('PeriodSubscription Form Service', () => {
  let service: PeriodSubscriptionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PeriodSubscriptionFormService);
  });

  describe('Service methods', () => {
    describe('createPeriodSubscriptionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPeriodSubscriptionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subscriptionName: expect.any(Object),
            description: expect.any(Object),
            price: expect.any(Object),
            billingPeriod: expect.any(Object),
            creditsPerPeriod: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });

      it('passing IPeriodSubscription should create a new form with FormGroup', () => {
        const formGroup = service.createPeriodSubscriptionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            subscriptionName: expect.any(Object),
            description: expect.any(Object),
            price: expect.any(Object),
            billingPeriod: expect.any(Object),
            creditsPerPeriod: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            user: expect.any(Object),
          }),
        );
      });
    });

    describe('getPeriodSubscription', () => {
      it('should return NewPeriodSubscription for default PeriodSubscription initial value', () => {
        const formGroup = service.createPeriodSubscriptionFormGroup(sampleWithNewData);

        const periodSubscription = service.getPeriodSubscription(formGroup) as any;

        expect(periodSubscription).toMatchObject(sampleWithNewData);
      });

      it('should return NewPeriodSubscription for empty PeriodSubscription initial value', () => {
        const formGroup = service.createPeriodSubscriptionFormGroup();

        const periodSubscription = service.getPeriodSubscription(formGroup) as any;

        expect(periodSubscription).toMatchObject({});
      });

      it('should return IPeriodSubscription', () => {
        const formGroup = service.createPeriodSubscriptionFormGroup(sampleWithRequiredData);

        const periodSubscription = service.getPeriodSubscription(formGroup) as any;

        expect(periodSubscription).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPeriodSubscription should not enable id FormControl', () => {
        const formGroup = service.createPeriodSubscriptionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPeriodSubscription should disable id FormControl', () => {
        const formGroup = service.createPeriodSubscriptionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
