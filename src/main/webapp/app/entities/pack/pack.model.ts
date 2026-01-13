import { IUser } from 'app/entities/user/user.model';

export interface IPack {
  id: number;
  packName?: string | null;
  description?: string | null;
  price?: number | null;
  billingPeriod?: string | null;
  credits?: number | null;
  validityDays?: number | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewPack = Omit<IPack, 'id'> & { id: null };
