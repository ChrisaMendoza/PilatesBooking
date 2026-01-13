import dayjs from 'dayjs/esm';

import { IPeriodSubscription, NewPeriodSubscription } from './period-subscription.model';

export const sampleWithRequiredData: IPeriodSubscription = {
  id: 12286,
  subscriptionName: 'caractériser',
  price: 26653,
  creditsPerPeriod: 19555,
};

export const sampleWithPartialData: IPeriodSubscription = {
  id: 27026,
  subscriptionName: "aussi d'entre davantage",
  price: 13340,
  creditsPerPeriod: 25016,
  startDate: dayjs('2026-01-13'),
  endDate: dayjs('2026-01-13'),
};

export const sampleWithFullData: IPeriodSubscription = {
  id: 20083,
  subscriptionName: 'avex environ',
  description: 'fidèle',
  price: 26913,
  billingPeriod: 'à condition que tellement',
  creditsPerPeriod: 20298,
  startDate: dayjs('2026-01-12'),
  endDate: dayjs('2026-01-13'),
};

export const sampleWithNewData: NewPeriodSubscription = {
  subscriptionName: 'debout',
  price: 3686,
  creditsPerPeriod: 17188,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
