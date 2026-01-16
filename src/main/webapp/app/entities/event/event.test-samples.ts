import dayjs from 'dayjs/esm';

import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 18174,
  startAt: dayjs('2026-01-16T01:53'),
  endAt: dayjs('2026-01-15T18:38'),
  capacity: 5031,
};

export const sampleWithPartialData: IEvent = {
  id: 30736,
  startAt: dayjs('2026-01-16T12:29'),
  endAt: dayjs('2026-01-16T05:54'),
  capacity: 260,
  status: 'contre toucher meuh',
};

export const sampleWithFullData: IEvent = {
  id: 14744,
  coachName: 'hurler dring',
  startAt: dayjs('2026-01-15T22:17'),
  endAt: dayjs('2026-01-15T20:12'),
  capacity: 8983,
  status: 'oh concernant',
};

export const sampleWithNewData: NewEvent = {
  startAt: dayjs('2026-01-16T10:58'),
  endAt: dayjs('2026-01-15T17:15'),
  capacity: 19647,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
