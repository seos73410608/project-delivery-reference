
import type {
  FastifySchema,
} from "fastify";

import type {
  ProjectOperationHandoverInitialCreateRequest,
  ProjectOperationHandoverOperatorChangeCreateRequest,
  ProjectOperationHandoverReHandoverCreateRequest,
} from "../types/project-operation-handover.types.js";

const positiveIdSchema = {
  type: "integer",
  minimum: 1,
} as const;

const positiveIdParamSchema = {
  type: "string",
  pattern: "^[1-9][0-9]*$",
} as const;

const dateSchema = {
  type: "string",
  format: "date",
} as const;

const nullableDateSchema = {
  anyOf: [
    { type: "null" },
    dateSchema,
  ],
} as const;

const nullableStringSchema = {
  anyOf: [
    { type: "null" },
    { type: "string" },
  ],
} as const;

const initialCreateBodySchema = {
  type: "object",
  additionalProperties: false,
  required: [
    "projectOperationId",
    "toParticipantId",
  ],
  properties: {
    projectOperationId: positiveIdSchema,
    toParticipantId: positiveIdSchema,
    plannedHandoverDate: nullableDateSchema,
    notes: nullableStringSchema,
  },
} as const;

const operatorChangeCreateBodySchema = {
  type: "object",
  additionalProperties: false,
  required: [
    "projectOperationId",
    "fromParticipantId",
    "toParticipantId",
  ],
  properties: {
    projectOperationId: positiveIdSchema,
    fromParticipantId: positiveIdSchema,
    toParticipantId: positiveIdSchema,
    plannedHandoverDate: nullableDateSchema,
    notes: nullableStringSchema,
  },
} as const;

const reHandoverCreateBodySchema = {
  type: "object",
  additionalProperties: false,
  required: [
    "projectOperationId",
    "fromParticipantId",
    "toParticipantId",
  ],
  properties: {
    projectOperationId: positiveIdSchema,
    fromParticipantId: positiveIdSchema,
    toParticipantId: positiveIdSchema,
    plannedHandoverDate: nullableDateSchema,
    notes: nullableStringSchema,
  },
} as const;

const idParamsSchema = {
  type: "object",
  additionalProperties: false,
  required: ["id"],
  properties: {
    id: positiveIdParamSchema,
  },
} as const;

const projectOperationIdParamsSchema = {
  type: "object",
  additionalProperties: false,
  required: ["projectOperationId"],
  properties: {
    projectOperationId: positiveIdParamSchema,
  },
} as const;

export const projectOperationHandoverInitialCreateSchema: FastifySchema = {
  body: initialCreateBodySchema,
};

export const projectOperationHandoverOperatorChangeCreateSchema: FastifySchema = {
  body: operatorChangeCreateBodySchema,
};

export const projectOperationHandoverReHandoverCreateSchema: FastifySchema = {
  body: reHandoverCreateBodySchema,
};

export const projectOperationHandoverIdSchema: FastifySchema = {
  params: idParamsSchema,
};

export const projectOperationHandoverProjectOperationIdSchema: FastifySchema = {
  params: projectOperationIdParamsSchema,
};

export type {
  ProjectOperationHandoverInitialCreateRequest,
  ProjectOperationHandoverOperatorChangeCreateRequest,
  ProjectOperationHandoverReHandoverCreateRequest,
};
