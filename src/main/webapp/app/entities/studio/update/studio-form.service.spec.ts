import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../studio.test-samples';

import { StudioFormService } from './studio-form.service';

describe('Studio Form Service', () => {
  let service: StudioFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StudioFormService);
  });

  describe('Service methods', () => {
    describe('createStudioFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createStudioFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            address: expect.any(Object),
            category: expect.any(Object),
          }),
        );
      });

      it('passing IStudio should create a new form with FormGroup', () => {
        const formGroup = service.createStudioFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            address: expect.any(Object),
            category: expect.any(Object),
          }),
        );
      });
    });

    describe('getStudio', () => {
      it('should return NewStudio for default Studio initial value', () => {
        const formGroup = service.createStudioFormGroup(sampleWithNewData);

        const studio = service.getStudio(formGroup) as any;

        expect(studio).toMatchObject(sampleWithNewData);
      });

      it('should return NewStudio for empty Studio initial value', () => {
        const formGroup = service.createStudioFormGroup();

        const studio = service.getStudio(formGroup) as any;

        expect(studio).toMatchObject({});
      });

      it('should return IStudio', () => {
        const formGroup = service.createStudioFormGroup(sampleWithRequiredData);

        const studio = service.getStudio(formGroup) as any;

        expect(studio).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IStudio should not enable id FormControl', () => {
        const formGroup = service.createStudioFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewStudio should disable id FormControl', () => {
        const formGroup = service.createStudioFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
