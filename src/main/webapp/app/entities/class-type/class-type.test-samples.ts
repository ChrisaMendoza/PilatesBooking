import { IClassType, NewClassType } from './class-type.model';

export const sampleWithRequiredData: IClassType = {
  id: 17838,
  name: 'attarder respecter regretter',
  duration: 31884,
};

export const sampleWithPartialData: IClassType = {
  id: 2172,
  name: 'si',
  description: 'comme',
  duration: 19209,
  capacity: 22502,
};

export const sampleWithFullData: IClassType = {
  id: 28124,
  name: 'svelte du fait que',
  description: 'de façon à ce que assez insipide',
  duration: 25409,
  capacity: 13454,
};

export const sampleWithNewData: NewClassType = {
  name: 'au point que sur concurrence',
  duration: 29373,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
