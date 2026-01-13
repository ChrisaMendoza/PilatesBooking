import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IClassSession } from 'app/entities/class-session/class-session.model';

export interface IBooking {
  id: number;
  status?: string | null;
  createdAt?: dayjs.Dayjs | null;
  cancelledAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  classSession?: IClassSession | null;
}

export type NewBooking = Omit<IBooking, 'id'> & { id: null };
