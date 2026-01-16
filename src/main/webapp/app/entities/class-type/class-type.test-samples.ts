import { IClassType, NewClassType } from './class-type.model';

export const sampleWithRequiredData: IClassType = {
  id: 17838,
  name: 'attarder respecter regretter',
};

export const sampleWithPartialData: IClassType = {
  id: 2172,
  name: 'si',
  description: 'comme',
  capacity: 19209,
};

export const sampleWithFullData: IClassType = {
  id: 28124,
  name: 'svelte du fait que',
  description: 'de façon à ce que assez insipide',
  capacity: 25409,
};

export const sampleWithNewData: NewClassType = {
  name: 'au point que sur concurrence',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
