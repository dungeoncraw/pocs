export enum Action {
  CallReal = "CallReal",
  MockWhole = "MockWhole",
  Mutate = "Mutate",
  Exception = "Exception",
  Delay = "Delay",
}

export interface Transformation {
  field: string;
  value: string;
}

export interface Profile {
  id: string;
  action: Action;
  mockResponse?: string;
  mutations?: Transformation[];
  exceptionMessage?: string;
  delayMs?: number;
}
