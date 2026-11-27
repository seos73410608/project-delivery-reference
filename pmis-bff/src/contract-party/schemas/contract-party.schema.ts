import type {
  CloseContractPartyQuery,
  ChangeContractPartyPrimaryQuery,
  CreateContractPartyRequest,
} from "../types/contract-party.types.js";

const positiveIntegerString = {
  type: "string",
  pattern: "^[1-9]\\d*$",
} as const;

const dateSchema = {
  type: "string",
  format: "date",
} as const;

export const contractPartyParamsSchema = {
  type: "object",
  additionalProperties: false,
  required: ["contractId"],
  properties: {
    contractId: positiveIntegerString,
  },
} as const;

export const companyContractPartyParamsSchema = {
  type: "object",
  additionalProperties: false,
  required: ["companyId"],
  properties: {
    companyId: positiveIntegerString,
  },
} as const;

export const contractPartyItemParamsSchema = {
  type: "object",
  additionalProperties: false,
  required: ["contractId", "partyId"],
  properties: {
    contractId: positiveIntegerString,
    partyId: positiveIntegerString,
  },
} as const;

export const createContractPartyBodySchema = {
  type: "object",
  additionalProperties: false,
  required: ["companyId", "partyRole", "effectiveFrom"],
  properties: {
    companyId: {
      type: "integer",
      minimum: 1,
    },
    organizationId: {
      type: "integer",
      minimum: 1,
    },
    partyRole: {
      type: "string",
      enum: [
        "CLIENT",
        "CONTRACTOR",
        "SUBCONTRACTOR",
        "SUPPLIER",
        "PARTNER",
      ],
    },
    primary: {
      type: "boolean",
    },
    effectiveFrom: dateSchema,
    description: {
      type: "string",
      maxLength: 500,
    },
  },
} as const;

export const closeContractPartyQuerySchema = {
  type: "object",
  additionalProperties: false,
  required: ["effectiveTo"],
  properties: {
    effectiveTo: dateSchema,
  },
} as const;

export const changeContractPartyPrimaryQuerySchema = {
  type: "object",
  additionalProperties: false,
  required: ["primary"],
  properties: {
    primary: {
      type: "boolean",
    },
  },
} as const;

export type CreateContractPartyBody =
  CreateContractPartyRequest;

export type CloseContractPartyQueryParams =
  CloseContractPartyQuery;

export type ChangeContractPartyPrimaryQueryParams =
  ChangeContractPartyPrimaryQuery;