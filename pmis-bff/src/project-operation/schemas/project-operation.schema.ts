const relationshipTypeValues = [
  "BUILD_RESULT",
  "ENHANCEMENT",
  "MIGRATION",
  "OTHER",
] as const;

const projectOperationStatusValues = [
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
  anyOf: [
    dateSchema,
    { type: "null" },
  ],
} as const;

const nullableStringSchema = {
  anyOf: [
    { type: "null" },
    { type: "string" },
  ],
} as const;

const positiveIdSchema = {
  type: "string",
  pattern: "^[1-9][0-9]*$",
} as const;

export const projectOperationIdParamsSchema = {
  type: "object",
  required: ["id"],
  additionalProperties: false,
  properties: {
    id: positiveIdSchema,
  },
} as const;

export const projectOperationProjectIdParamsSchema = {
  type: "object",
  required: ["projectId"],
  additionalProperties: false,
  properties: {
    projectId: positiveIdSchema,
  },
} as const;

export const projectOperationOperationIdParamsSchema = {
  type: "object",
  required: ["operationId"],
  additionalProperties: false,
  properties: {
    operationId: positiveIdSchema,
  },
} as const;

export const createProjectOperationBodySchema = {
  type: "object",
  required: [
    "projectId",
    "operationId",
    "relationshipType",
  ],
  additionalProperties: false,
  properties: {
    projectId: {
      type: "integer",
      minimum: 1,
    },
    operationId: {
      type: "integer",
      minimum: 1,
    },
    relationshipType: {
      type: "string",
      enum: relationshipTypeValues,
    },
    effectiveFrom: nullableDateSchema,
    effectiveTo: nullableDateSchema,
    description: nullableStringSchema,
  },
} as const;

export const updateProjectOperationBodySchema = {
  type: "object",
  required: ["relationshipType"],
  additionalProperties: false,
  properties: {
    relationshipType: {
      type: "string",
      enum: relationshipTypeValues,
    },
    effectiveFrom: nullableDateSchema,
    effectiveTo: nullableDateSchema,
    description: nullableStringSchema,
  },
} as const;

export const changeProjectOperationStatusBodySchema = {
  type: "object",
  required: ["status"],
  additionalProperties: false,
  properties: {
    status: {
      type: "string",
      enum: projectOperationStatusValues,
    },
  },
} as const;