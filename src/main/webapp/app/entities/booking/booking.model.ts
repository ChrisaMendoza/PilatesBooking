import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IEvent } from 'app/entities/event/event.model';

export interface IBooking {
  id: number;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  cancelledAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  event?: IEvent | null;
}

export type NewBooking = Omit<IBooking, 'id'> & { id: null };
