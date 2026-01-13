import dayjs from 'dayjs/esm';

import { IBooking, NewBooking } from './booking.model';

export const sampleWithRequiredData: IBooking = {
  id: 4955,
};

export const sampleWithPartialData: IBooking = {
  id: 15511,
  status: 'broum ouille',
  cancelledAt: dayjs('2026-01-13T15:15'),
};

export const sampleWithFullData: IBooking = {
  id: 28482,
  status: 'tic-tac à côté de',
  createdAt: dayjs('2026-01-12T23:22'),
  cancelledAt: dayjs('2026-01-13T08:49'),
};

export const sampleWithNewData: NewBooking = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
