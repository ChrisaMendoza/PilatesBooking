import { IPack, NewPack } from './pack.model';

export const sampleWithRequiredData: IPack = {
  id: 15548,
  packName: 'au défaut de spécialiste pin-pon',
  price: 16716,
  credits: 31778,
};

export const sampleWithPartialData: IPack = {
  id: 29070,
  packName: 'euh',
  price: 17209,
  billingPeriod: 'informer',
  credits: 16540,
};

export const sampleWithFullData: IPack = {
  id: 14553,
  packName: 'serviable juriste commissionnaire',
  description: 'mal guide tsoin-tsoin',
  price: 423,
  billingPeriod: 'en decà de',
  credits: 17603,
  validityDays: 27204,
};

export const sampleWithNewData: NewPack = {
  packName: 'alors placide encourager',
  price: 20639,
  credits: 15646,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
