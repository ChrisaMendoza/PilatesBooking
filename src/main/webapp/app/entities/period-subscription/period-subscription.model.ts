import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IPeriodSubscription {
  id: number;
  subscriptionName?: string | null;
  description?: string | null;
  price?: number | null;
  billingPeriod?: string | null;
  creditsPerPeriod?: number | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewPeriodSubscription = Omit<IPeriodSubscription, 'id'> & { id: null };
