
const operationTypeValues = [
  "BUSINESS_SERVICE",
  "APPLICATION_SERVICE",
  "INFRASTRUCTURE_SERVICE",
  "PLATFORM_SERVICE",
  "DATABASE_SERVICE",
  "OTHER",
] as const;

const operationStatusValues = [
  "PLANNED",
  "ACTIVE",
  "SUSPENDED",
  "CLOSED",
] as const;

const positiveIdSchema = {
  type: "string",
  pattern: "^[1-9][0-9]*$",
} as const;

const requiredTextSchema = (maxLength: number) => ({
  type: "string",
  minLength: 1,
  maxLength,
  pattern: ".*\\S.*",
} as const);

const dateSchema = {
  type: "string",
  format: "date",
} as const;

export const operationIdParamsSchema = {
  type: "object",
  required: ["operationId"],
  additionalProperties: false,
  properties: {
    operationId: positiveIdSchema,
  },
} as const;

export const operationCodeParamsSchema = {
  type: "object",
  required: ["operationCode"],
  additionalProperties: false,
  properties: {
    operationCode: requiredTextSchema(50),
  },
} as const;

export const createOperationBodySchema = {
  type: "object",
  required: ["operationCode", "operationName", "operationType"],
  additionalProperties: false,
  properties: {
    operationCode: requiredTextSchema(50),
    operationName: requiredTextSchema(200),
    operationType: {
      type: "string",
      enum: operationTypeValues,
    },
    managerUserId: {
      type: "integer",
      minimum: 1,
    },
    description: {
      type: "string",
    },
    effectiveFrom: dateSchema,
    effectiveTo: dateSchema,
  },
} as const;

export const updateOperationBodySchema = {
  type: "object",
  required: ["operationName", "operationType"],
  additionalProperties: false,
  properties: {
    operationName: requiredTextSchema(200),
    operationType: {
      type: "string",
      enum: operationTypeValues,
    },
    managerUserId: {
      type: "integer",
      minimum: 1,
    },
    description: {
      type: "string",
    },
    effectiveFrom: dateSchema,
    effectiveTo: dateSchema,
  },
} as const;

export const updateOperationStatusBodySchema = {
  type: "object",
  required: ["status"],
  additionalProperties: false,
  properties: {
    status: {
      type: "string",
      enum: operationStatusValues,
    },
  },
} as const;
