import { IStudio, NewStudio } from './studio.model';

export const sampleWithRequiredData: IStudio = {
  id: 8212,
  name: 'filer broum',
};

export const sampleWithPartialData: IStudio = {
  id: 23190,
  name: 'quitte à',
  category: 'guide hormis',
};

export const sampleWithFullData: IStudio = {
  id: 8442,
  name: 'durant',
  address: 'à même admirablement',
  category: 'de crainte que hé',
};

export const sampleWithNewData: NewStudio = {
  name: 'faciliter lorsque',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
