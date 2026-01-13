export interface IStudio {
  id: number;
  name?: string | null;
  address?: string | null;
  category?: string | null;
}

export type NewStudio = Omit<IStudio, 'id'> & { id: null };
