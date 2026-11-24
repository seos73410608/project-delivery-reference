
const positiveIdSchema = {
  type: "string",
  pattern: "^[1-9][0-9]*$",
} as const;

export const operationalScopeCiScopeParamsSchema = {
  type: "object",
  required: ["scopeId"],
  additionalProperties: false,
  properties: {
    scopeId: positiveIdSchema,
  },
} as const;

export const operationalScopeCiParamsSchema = {
  type: "object",
  required: ["scopeId", "ciId"],
  additionalProperties: false,
  properties: {
    scopeId: positiveIdSchema,
    ciId: positiveIdSchema,
  },
} as const;
