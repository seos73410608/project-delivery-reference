
export type CiStatus =
  | "PLANNED"
  | "ACTIVE"
  | "INACTIVE"
  | "MAINTENANCE"
  | "RETIRED"
  | "DISPOSED";

export type CiEnvironment =
  | "DEV"
  | "TEST"
  | "STAGING"
  | "PROD";

export interface OperationalScopeCiResponse {
  id: number;
  scopeId: number;
  ciId: number;
  ciCode: string;
  ciName: string;
  ciTypeId: number;
  ciTypeCode: string;
  ciTypeName: string;
  status: CiStatus;
  environment: CiEnvironment;
  createdAt: string;
  updatedAt: string;
}
