import dayjs from 'dayjs/esm';
import { IStudio } from 'app/entities/studio/studio.model';
import { IClassType } from 'app/entities/class-type/class-type.model';

export interface IClassSession {
  id: number;
  coachName?: string | null;
  startAt?: dayjs.Dayjs | null;
  endAt?: dayjs.Dayjs | null;
  capacity?: number | null;
  status?: string | null;
  studio?: IStudio | null;
  classType?: IClassType | null;
}

export type NewClassSession = Omit<IClassSession, 'id'> & { id: null };
