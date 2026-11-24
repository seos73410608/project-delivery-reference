const operationScopeStatusValues = [
  "PLANNED",
  "ACTIVE",
  "COMPLETED",
  "CANCELLED",
] as const;

const dateSchema = {
  type: "string",
  format: "date",
} as const;

const nullableDateSchema = {
  anyOf: [{ type: "null" }, dateSchema],
} as const;

const nullableStringSchema = {
  anyOf: [{ type: "null" }, { type: "string" }],
} as const;

const positiveIdSchema = {
  type: "string",
  pattern: "^[1-9][0-9]*$",
} as const;

export const operationScopeIdParamsSchema = {
  type: "object",
  required: ["id"],
  additionalProperties: false,
  properties: {
    id: positiveIdSchema,
  },
} as const;

export const operationScopeOperationIdParamsSchema = {
  type: "object",
  required: ["operationId"],
  additionalProperties: false,
  properties: {
    operationId: positiveIdSchema,
  },
} as const;

export const operationScopeOperationalScopeIdParamsSchema = {
  type: "object",
  required: ["operationalScopeId"],
  additionalProperties: false,
  properties: {
    operationalScopeId: positiveIdSchema,
  },
} as const;

export const createOperationScopeBodySchema = {
  type: "object",
  required: ["operationId", "operationalScopeId"],
  additionalProperties: false,
  properties: {
    operationId: { type: "integer", minimum: 1 },
    operationalScopeId: { type: "integer", minimum: 1 },
    effectiveFrom: nullableDateSchema,
    effectiveTo: nullableDateSchema,
    description: nullableStringSchema,
  },
} as const;

export const updateOperationScopeBodySchema = {
  type: "object",
  additionalProperties: false,
  properties: {
    effectiveFrom: nullableDateSchema,
    effectiveTo: nullableDateSchema,
    description: nullableStringSchema,
  },
} as const;

export const changeOperationScopeStatusBodySchema = {
  type: "object",
  required: ["status"],
  additionalProperties: false,
  properties: {
    status: {
      type: "string",
      enum: operationScopeStatusValues,
    },
  },
} as const;