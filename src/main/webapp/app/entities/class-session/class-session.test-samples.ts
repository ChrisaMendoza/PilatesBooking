import dayjs from 'dayjs/esm';

import { IClassSession, NewClassSession } from './class-session.model';

export const sampleWithRequiredData: IClassSession = {
  id: 29713,
  startAt: dayjs('2026-01-12T18:07'),
  endAt: dayjs('2026-01-13T00:23'),
  capacity: 3824,
};

export const sampleWithPartialData: IClassSession = {
  id: 15925,
  startAt: dayjs('2026-01-12T22:41'),
  endAt: dayjs('2026-01-13T02:59'),
  capacity: 6401,
  status: 'tenter maintenant',
};

export const sampleWithFullData: IClassSession = {
  id: 1615,
  coachName: 'biathlète jeune enfant',
  startAt: dayjs('2026-01-12T20:19'),
  endAt: dayjs('2026-01-13T06:55'),
  capacity: 5705,
  status: 'devant y miam',
};

export const sampleWithNewData: NewClassSession = {
  startAt: dayjs('2026-01-13T06:59'),
  endAt: dayjs('2026-01-13T13:34'),
  capacity: 14041,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
