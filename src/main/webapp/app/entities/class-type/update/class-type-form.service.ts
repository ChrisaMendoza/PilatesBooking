import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IClassType, NewClassType } from '../class-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClassType for edit and NewClassTypeFormGroupInput for create.
 */
type ClassTypeFormGroupInput = IClassType | PartialWithRequiredKeyOf<NewClassType>;

type ClassTypeFormDefaults = Pick<NewClassType, 'id'>;

type ClassTypeFormGroupContent = {
  id: FormControl<IClassType['id'] | NewClassType['id']>;
  name: FormControl<IClassType['name']>;
  description: FormControl<IClassType['description']>;
  capacity: FormControl<IClassType['capacity']>;
};

export type ClassTypeFormGroup = FormGroup<ClassTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClassTypeFormService {
  createClassTypeFormGroup(classType: ClassTypeFormGroupInput = { id: null }): ClassTypeFormGroup {
    const classTypeRawValue = {
      ...this.getFormDefaults(),
      ...classType,
    };
    return new FormGroup<ClassTypeFormGroupContent>({
      id: new FormControl(
        { value: classTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(classTypeRawValue.name, {
        validators: [Validators.required],
      }),
      description: new FormControl(classTypeRawValue.description),
      capacity: new FormControl(classTypeRawValue.capacity),
    });
  }

  getClassType(form: ClassTypeFormGroup): IClassType | NewClassType {
    return form.getRawValue() as IClassType | NewClassType;
  }

  resetForm(form: ClassTypeFormGroup, classType: ClassTypeFormGroupInput): void {
    const classTypeRawValue = { ...this.getFormDefaults(), ...classType };
    form.reset(
      {
        ...classTypeRawValue,
        id: { value: classTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClassTypeFormDefaults {
    return {
      id: null,
    };
  }
}
