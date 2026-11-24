
const operationalScopeTypeValues = [
  "SERVICE",
  "SYSTEM",
  "ENVIRONMENT",
  "PLATFORM",
  "DATABASE",
  "SERVER_GROUP",
  "CI_SCOPE",
] as const;

const operationalScopeStatusValues = [
  "PLANNED",
  "ACTIVE",
  "SUSPENDED",
  "CLOSED",
] as const;

const dateSchema = {
  type: "string",
  format: "date",
} as const;

const requiredTextSchema = (maxLength: number) => ({
  type: "string",
  minLength: 1,
  maxLength,
  pattern: ".*\\S.*",
} as const);

const optionalIdSchema = {
  type: "integer",
  minimum: 1,
} as const;

export const operationalScopeIdParamsSchema = {
  type: "object",
  required: ["id"],
  additionalProperties: false,
  properties: {
    id: {
      type: "string",
      pattern: "^[1-9][0-9]*$",
    },
  },
} as const;

export const createOperationalScopeBodySchema = {
  type: "object",
  required: ["scopeName", "scopeType"],
  additionalProperties: false,
  properties: {
    scopeName: requiredTextSchema(200),
    scopeType: {
      type: "string",
      enum: operationalScopeTypeValues,
    },
    parentScopeId: optionalIdSchema,
    ownerId: optionalIdSchema,
    description: { type: "string" },
    effectiveFrom: dateSchema,
    effectiveTo: dateSchema,
  },
} as const;

export const updateOperationalScopeBodySchema = {
  type: "object",
  required: ["version", "scopeName", "scopeType"],
  additionalProperties: false,
  properties: {
    version: {
      type: "integer",
      minimum: 0,
    },
    scopeName: requiredTextSchema(200),
    scopeType: {
      type: "string",
      enum: operationalScopeTypeValues,
    },
    parentScopeId: optionalIdSchema,
    ownerId: optionalIdSchema,
    description: { type: "string" },
    effectiveFrom: dateSchema,
    effectiveTo: dateSchema,
  },
} as const;

export const searchOperationalScopesQuerySchema = {
  type: "object",
  additionalProperties: false,
  properties: {
    scopeCode: { type: "string" },
    scopeName: { type: "string" },
    scopeType: {
      type: "string",
      enum: operationalScopeTypeValues,
    },
    status: {
      type: "string",
      enum: operationalScopeStatusValues,
    },
    parentScopeId: optionalIdSchema,
    ownerId: optionalIdSchema,
    ownerKeyword: { type: "string" },
    page: {
      type: "integer",
      minimum: 0,
    },
    size: {
      type: "integer",
      minimum: 1,
    },
    sort: {
      anyOf: [
        { type: "string", minLength: 1 },
        {
          type: "array",
          items: { type: "string", minLength: 1 },
        },
      ],
    },
  },
} as const;
