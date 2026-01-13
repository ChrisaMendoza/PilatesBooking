import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IStudio, NewStudio } from '../studio.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStudio for edit and NewStudioFormGroupInput for create.
 */
type StudioFormGroupInput = IStudio | PartialWithRequiredKeyOf<NewStudio>;

type StudioFormDefaults = Pick<NewStudio, 'id'>;

type StudioFormGroupContent = {
  id: FormControl<IStudio['id'] | NewStudio['id']>;
  name: FormControl<IStudio['name']>;
  address: FormControl<IStudio['address']>;
  category: FormControl<IStudio['category']>;
};

export type StudioFormGroup = FormGroup<StudioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StudioFormService {
  createStudioFormGroup(studio: StudioFormGroupInput = { id: null }): StudioFormGroup {
    const studioRawValue = {
      ...this.getFormDefaults(),
      ...studio,
    };
    return new FormGroup<StudioFormGroupContent>({
      id: new FormControl(
        { value: studioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(studioRawValue.name, {
        validators: [Validators.required],
      }),
      address: new FormControl(studioRawValue.address),
      category: new FormControl(studioRawValue.category),
    });
  }

  getStudio(form: StudioFormGroup): IStudio | NewStudio {
    return form.getRawValue() as IStudio | NewStudio;
  }

  resetForm(form: StudioFormGroup, studio: StudioFormGroupInput): void {
    const studioRawValue = { ...this.getFormDefaults(), ...studio };
    form.reset(
      {
        ...studioRawValue,
        id: { value: studioRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StudioFormDefaults {
    return {
      id: null,
    };
  }
}
