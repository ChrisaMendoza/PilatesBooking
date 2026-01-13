import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPeriodSubscription, NewPeriodSubscription } from '../period-subscription.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPeriodSubscription for edit and NewPeriodSubscriptionFormGroupInput for create.
 */
type PeriodSubscriptionFormGroupInput = IPeriodSubscription | PartialWithRequiredKeyOf<NewPeriodSubscription>;

type PeriodSubscriptionFormDefaults = Pick<NewPeriodSubscription, 'id'>;

type PeriodSubscriptionFormGroupContent = {
  id: FormControl<IPeriodSubscription['id'] | NewPeriodSubscription['id']>;
  subscriptionName: FormControl<IPeriodSubscription['subscriptionName']>;
  description: FormControl<IPeriodSubscription['description']>;
  price: FormControl<IPeriodSubscription['price']>;
  billingPeriod: FormControl<IPeriodSubscription['billingPeriod']>;
  creditsPerPeriod: FormControl<IPeriodSubscription['creditsPerPeriod']>;
  startDate: FormControl<IPeriodSubscription['startDate']>;
  endDate: FormControl<IPeriodSubscription['endDate']>;
  user: FormControl<IPeriodSubscription['user']>;
};

export type PeriodSubscriptionFormGroup = FormGroup<PeriodSubscriptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PeriodSubscriptionFormService {
  createPeriodSubscriptionFormGroup(periodSubscription: PeriodSubscriptionFormGroupInput = { id: null }): PeriodSubscriptionFormGroup {
    const periodSubscriptionRawValue = {
      ...this.getFormDefaults(),
      ...periodSubscription,
    };
    return new FormGroup<PeriodSubscriptionFormGroupContent>({
      id: new FormControl(
        { value: periodSubscriptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      subscriptionName: new FormControl(periodSubscriptionRawValue.subscriptionName, {
        validators: [Validators.required],
      }),
      description: new FormControl(periodSubscriptionRawValue.description),
      price: new FormControl(periodSubscriptionRawValue.price, {
        validators: [Validators.required],
      }),
      billingPeriod: new FormControl(periodSubscriptionRawValue.billingPeriod),
      creditsPerPeriod: new FormControl(periodSubscriptionRawValue.creditsPerPeriod, {
        validators: [Validators.required],
      }),
      startDate: new FormControl(periodSubscriptionRawValue.startDate),
      endDate: new FormControl(periodSubscriptionRawValue.endDate),
      user: new FormControl(periodSubscriptionRawValue.user),
    });
  }

  getPeriodSubscription(form: PeriodSubscriptionFormGroup): IPeriodSubscription | NewPeriodSubscription {
    return form.getRawValue() as IPeriodSubscription | NewPeriodSubscription;
  }

  resetForm(form: PeriodSubscriptionFormGroup, periodSubscription: PeriodSubscriptionFormGroupInput): void {
    const periodSubscriptionRawValue = { ...this.getFormDefaults(), ...periodSubscription };
    form.reset(
      {
        ...periodSubscriptionRawValue,
        id: { value: periodSubscriptionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PeriodSubscriptionFormDefaults {
    return {
      id: null,
    };
  }
}
