const positiveIdSchema = {
  type: "string",
  pattern: "^[1-9][0-9]*$",
} as const;

export const operationParticipantOperationIdParamsSchema = {
  type: "object",
  required: ["operationId"],
  additionalProperties: false,
  properties: {
    operationId: positiveIdSchema,
  },
} as const;

export const operationParticipantsQuerySchema = {
  type: "object",
  additionalProperties: false,
  properties: {
    plannedHandoverDate: {
      type: "string",
      format: "date",
    },
  },
} as const;