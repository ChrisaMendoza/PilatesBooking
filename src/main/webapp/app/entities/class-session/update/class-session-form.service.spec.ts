import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../class-session.test-samples';

import { ClassSessionFormService } from './class-session-form.service';

describe('ClassSession Form Service', () => {
  let service: ClassSessionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClassSessionFormService);
  });

  describe('Service methods', () => {
    describe('createClassSessionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClassSessionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            coachName: expect.any(Object),
            startAt: expect.any(Object),
            endAt: expect.any(Object),
            capacity: expect.any(Object),
            status: expect.any(Object),
            studio: expect.any(Object),
            classType: expect.any(Object),
          }),
        );
      });

      it('passing IClassSession should create a new form with FormGroup', () => {
        const formGroup = service.createClassSessionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            coachName: expect.any(Object),
            startAt: expect.any(Object),
            endAt: expect.any(Object),
            capacity: expect.any(Object),
            status: expect.any(Object),
            studio: expect.any(Object),
            classType: expect.any(Object),
          }),
        );
      });
    });

    describe('getClassSession', () => {
      it('should return NewClassSession for default ClassSession initial value', () => {
        const formGroup = service.createClassSessionFormGroup(sampleWithNewData);

        const classSession = service.getClassSession(formGroup) as any;

        expect(classSession).toMatchObject(sampleWithNewData);
      });

      it('should return NewClassSession for empty ClassSession initial value', () => {
        const formGroup = service.createClassSessionFormGroup();

        const classSession = service.getClassSession(formGroup) as any;

        expect(classSession).toMatchObject({});
      });

      it('should return IClassSession', () => {
        const formGroup = service.createClassSessionFormGroup(sampleWithRequiredData);

        const classSession = service.getClassSession(formGroup) as any;

        expect(classSession).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClassSession should not enable id FormControl', () => {
        const formGroup = service.createClassSessionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewClassSession should disable id FormControl', () => {
        const formGroup = service.createClassSessionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
