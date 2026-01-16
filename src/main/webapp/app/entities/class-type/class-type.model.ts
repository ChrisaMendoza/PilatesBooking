export interface IClassType {
  id: number;
  name?: string | null;
  description?: string | null;
  capacity?: number | null;
}

export type NewClassType = Omit<IClassType, 'id'> & { id: null };
