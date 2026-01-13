import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IClassSession, NewClassSession } from '../class-session.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClassSession for edit and NewClassSessionFormGroupInput for create.
 */
type ClassSessionFormGroupInput = IClassSession | PartialWithRequiredKeyOf<NewClassSession>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IClassSession | NewClassSession> = Omit<T, 'startAt' | 'endAt'> & {
  startAt?: string | null;
  endAt?: string | null;
};

type ClassSessionFormRawValue = FormValueOf<IClassSession>;

type NewClassSessionFormRawValue = FormValueOf<NewClassSession>;

type ClassSessionFormDefaults = Pick<NewClassSession, 'id' | 'startAt' | 'endAt'>;

type ClassSessionFormGroupContent = {
  id: FormControl<ClassSessionFormRawValue['id'] | NewClassSession['id']>;
  coachName: FormControl<ClassSessionFormRawValue['coachName']>;
  startAt: FormControl<ClassSessionFormRawValue['startAt']>;
  endAt: FormControl<ClassSessionFormRawValue['endAt']>;
  capacity: FormControl<ClassSessionFormRawValue['capacity']>;
  status: FormControl<ClassSessionFormRawValue['status']>;
  studio: FormControl<ClassSessionFormRawValue['studio']>;
  classType: FormControl<ClassSessionFormRawValue['classType']>;
};

export type ClassSessionFormGroup = FormGroup<ClassSessionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClassSessionFormService {
  createClassSessionFormGroup(classSession: ClassSessionFormGroupInput = { id: null }): ClassSessionFormGroup {
    const classSessionRawValue = this.convertClassSessionToClassSessionRawValue({
      ...this.getFormDefaults(),
      ...classSession,
    });
    return new FormGroup<ClassSessionFormGroupContent>({
      id: new FormControl(
        { value: classSessionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      coachName: new FormControl(classSessionRawValue.coachName),
      startAt: new FormControl(classSessionRawValue.startAt, {
        validators: [Validators.required],
      }),
      endAt: new FormControl(classSessionRawValue.endAt, {
        validators: [Validators.required],
      }),
      capacity: new FormControl(classSessionRawValue.capacity, {
        validators: [Validators.required],
      }),
      status: new FormControl(classSessionRawValue.status),
      studio: new FormControl(classSessionRawValue.studio),
      classType: new FormControl(classSessionRawValue.classType),
    });
  }

  getClassSession(form: ClassSessionFormGroup): IClassSession | NewClassSession {
    return this.convertClassSessionRawValueToClassSession(form.getRawValue() as ClassSessionFormRawValue | NewClassSessionFormRawValue);
  }

  resetForm(form: ClassSessionFormGroup, classSession: ClassSessionFormGroupInput): void {
    const classSessionRawValue = this.convertClassSessionToClassSessionRawValue({ ...this.getFormDefaults(), ...classSession });
    form.reset(
      {
        ...classSessionRawValue,
        id: { value: classSessionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClassSessionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startAt: currentTime,
      endAt: currentTime,
    };
  }

  private convertClassSessionRawValueToClassSession(
    rawClassSession: ClassSessionFormRawValue | NewClassSessionFormRawValue,
  ): IClassSession | NewClassSession {
    return {
      ...rawClassSession,
      startAt: dayjs(rawClassSession.startAt, DATE_TIME_FORMAT),
      endAt: dayjs(rawClassSession.endAt, DATE_TIME_FORMAT),
    };
  }

  private convertClassSessionToClassSessionRawValue(
    classSession: IClassSession | (Partial<NewClassSession> & ClassSessionFormDefaults),
  ): ClassSessionFormRawValue | PartialWithRequiredKeyOf<NewClassSessionFormRawValue> {
    return {
      ...classSession,
      startAt: classSession.startAt ? classSession.startAt.format(DATE_TIME_FORMAT) : undefined,
      endAt: classSession.endAt ? classSession.endAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
