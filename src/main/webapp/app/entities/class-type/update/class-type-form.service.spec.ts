import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../class-type.test-samples';

import { ClassTypeFormService } from './class-type-form.service';

describe('ClassType Form Service', () => {
  let service: ClassTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassTypeFormService);
  });

  describe('Service methods', () => {
    describe('createClassTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClassTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            duration: expect.any(Object),
            capacity: expect.any(Object),
          }),
        );
      });

      it('passing IClassType should create a new form with FormGroup', () => {
        const formGroup = service.createClassTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            duration: expect.any(Object),
            capacity: expect.any(Object),
          }),
        );
      });
    });

    describe('getClassType', () => {
      it('should return NewClassType for default ClassType initial value', () => {
        const formGroup = service.createClassTypeFormGroup(sampleWithNewData);

        const classType = service.getClassType(formGroup) as any;

        expect(classType).toMatchObject(sampleWithNewData);
      });

      it('should return NewClassType for empty ClassType initial value', () => {
        const formGroup = service.createClassTypeFormGroup();

        const classType = service.getClassType(formGroup) as any;

        expect(classType).toMatchObject({});
      });

      it('should return IClassType', () => {
        const formGroup = service.createClassTypeFormGroup(sampleWithRequiredData);

        const classType = service.getClassType(formGroup) as any;

        expect(classType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClassType should not enable id FormControl', () => {
        const formGroup = service.createClassTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewClassType should disable id FormControl', () => {
        const formGroup = service.createClassTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
