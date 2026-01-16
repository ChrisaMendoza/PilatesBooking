import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEvent, NewEvent } from '../event.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEvent for edit and NewEventFormGroupInput for create.
 */
type EventFormGroupInput = IEvent | PartialWithRequiredKeyOf<NewEvent>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEvent | NewEvent> = Omit<T, 'startAt' | 'endAt'> & {
  startAt?: string | null;
  endAt?: string | null;
};

type EventFormRawValue = FormValueOf<IEvent>;

type NewEventFormRawValue = FormValueOf<NewEvent>;

type EventFormDefaults = Pick<NewEvent, 'id' | 'startAt' | 'endAt'>;

type EventFormGroupContent = {
  id: FormControl<EventFormRawValue['id'] | NewEvent['id']>;
  coachName: FormControl<EventFormRawValue['coachName']>;
  startAt: FormControl<EventFormRawValue['startAt']>;
  endAt: FormControl<EventFormRawValue['endAt']>;
  capacity: FormControl<EventFormRawValue['capacity']>;
  status: FormControl<EventFormRawValue['status']>;
  studio: FormControl<EventFormRawValue['studio']>;
  classType: FormControl<EventFormRawValue['classType']>;
};

export type EventFormGroup = FormGroup<EventFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EventFormService {
  createEventFormGroup(event: EventFormGroupInput = { id: null }): EventFormGroup {
    const eventRawValue = this.convertEventToEventRawValue({
      ...this.getFormDefaults(),
      ...event,
    });
    return new FormGroup<EventFormGroupContent>({
      id: new FormControl(
        { value: eventRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      coachName: new FormControl(eventRawValue.coachName),
      startAt: new FormControl(eventRawValue.startAt, {
        validators: [Validators.required],
      }),
      endAt: new FormControl(eventRawValue.endAt, {
        validators: [Validators.required],
      }),
      capacity: new FormControl(eventRawValue.capacity, {
        validators: [Validators.required],
      }),
      status: new FormControl(eventRawValue.status),
      studio: new FormControl(eventRawValue.studio),
      classType: new FormControl(eventRawValue.classType),
    });
  }

  getEvent(form: EventFormGroup): IEvent | NewEvent {
    return this.convertEventRawValueToEvent(form.getRawValue() as EventFormRawValue | NewEventFormRawValue);
  }

  resetForm(form: EventFormGroup, event: EventFormGroupInput): void {
    const eventRawValue = this.convertEventToEventRawValue({ ...this.getFormDefaults(), ...event });
    form.reset(
      {
        ...eventRawValue,
        id: { value: eventRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EventFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startAt: currentTime,
      endAt: currentTime,
    };
  }

  private convertEventRawValueToEvent(rawEvent: EventFormRawValue | NewEventFormRawValue): IEvent | NewEvent {
    return {
      ...rawEvent,
      startAt: dayjs(rawEvent.startAt, DATE_TIME_FORMAT),
      endAt: dayjs(rawEvent.endAt, DATE_TIME_FORMAT),
    };
  }

  private convertEventToEventRawValue(
    event: IEvent | (Partial<NewEvent> & EventFormDefaults),
  ): EventFormRawValue | PartialWithRequiredKeyOf<NewEventFormRawValue> {
    return {
      ...event,
      startAt: event.startAt ? event.startAt.format(DATE_TIME_FORMAT) : undefined,
      endAt: event.endAt ? event.endAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
