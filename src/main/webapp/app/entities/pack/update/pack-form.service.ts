import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPack, NewPack } from '../pack.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPack for edit and NewPackFormGroupInput for create.
 */
type PackFormGroupInput = IPack | PartialWithRequiredKeyOf<NewPack>;

type PackFormDefaults = Pick<NewPack, 'id'>;

type PackFormGroupContent = {
  id: FormControl<IPack['id'] | NewPack['id']>;
  packName: FormControl<IPack['packName']>;
  description: FormControl<IPack['description']>;
  price: FormControl<IPack['price']>;
  billingPeriod: FormControl<IPack['billingPeriod']>;
  credits: FormControl<IPack['credits']>;
  validityDays: FormControl<IPack['validityDays']>;
  user: FormControl<IPack['user']>;
};

export type PackFormGroup = FormGroup<PackFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PackFormService {
  createPackFormGroup(pack: PackFormGroupInput = { id: null }): PackFormGroup {
    const packRawValue = {
      ...this.getFormDefaults(),
      ...pack,
    };
    return new FormGroup<PackFormGroupContent>({
      id: new FormControl(
        { value: packRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      packName: new FormControl(packRawValue.packName, {
        validators: [Validators.required],
      }),
      description: new FormControl(packRawValue.description),
      price: new FormControl(packRawValue.price, {
        validators: [Validators.required],
      }),
      billingPeriod: new FormControl(packRawValue.billingPeriod),
      credits: new FormControl(packRawValue.credits, {
        validators: [Validators.required],
      }),
      validityDays: new FormControl(packRawValue.validityDays),
      user: new FormControl(packRawValue.user),
    });
  }

  getPack(form: PackFormGroup): IPack | NewPack {
    return form.getRawValue() as IPack | NewPack;
  }

  resetForm(form: PackFormGroup, pack: PackFormGroupInput): void {
    const packRawValue = { ...this.getFormDefaults(), ...pack };
    form.reset(
      {
        ...packRawValue,
        id: { value: packRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PackFormDefaults {
    return {
      id: null,
    };
  }
}
