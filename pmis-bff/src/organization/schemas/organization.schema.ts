
import type {
  OrganizationStatus,
  OrganizationType,
} from "../types/organization.types.js";

const positiveIdSchema = {
  type: "string",
  pattern: "^[1-9][0-9]*$",
} as const;

const organizationTypeValues: OrganizationType[] = [
  "HEADQUARTERS",
  "DIVISION",
  "DEPARTMENT",
  "TEAM",
  "BRANCH",
  "CENTER",
  "OTHER",
];

const organizationStatusValues: OrganizationStatus[] = [
  "ACTIVE",
  "INACTIVE",
];

const optionalPositiveIdSchema = {
  anyOf: [
    {
      type: "integer",
      minimum: 1,
    },
    {
      type: "null",
    },
  ],
} as const;

const optionalDateSchema = {
  anyOf: [
    {
      type: "string",
      format: "date",
    },
    {
      type: "null",
    },
  ],
} as const;

const optionalDescriptionSchema = {
  anyOf: [
    {
      type: "string",
  },
    {
      type: "null",
    },
  ],
} as const;

export const companyOrganizationParamsSchema = {
  type: "object",
  additionalProperties: false,
  required: ["companyId"],
  properties: {
    companyId: positiveIdSchema,
  },
} as const;

export const organizationParamsSchema = {
  type: "object",
  additionalProperties: false,
  required: ["companyId", "organizationId"],
  properties: {
    companyId: positiveIdSchema,
    organizationId: positiveIdSchema,
  },
} as const;

export const createOrganizationBodySchema = {
  type: "object",
  additionalProperties: false,
  required: [
    "organizationCode",
    "organizationName",
    "organizationType",
  ],
  properties: {
    organizationCode: {
      type: "string",
      minLength: 1,
      maxLength: 50,
    },
    organizationName: {
      type: "string",
      minLength: 1,
      maxLength: 200,
    },
    organizationType: {
      type: "string",
      enum: organizationTypeValues,
    },
    parentOrganizationId: optionalPositiveIdSchema,
    managerUserId: optionalPositiveIdSchema,
    effectiveFrom: optionalDateSchema,
    effectiveTo: optionalDateSchema,
    description: optionalDescriptionSchema,
  },
} as const;

export const updateOrganizationBodySchema = {
  type: "object",
  additionalProperties: false,
  required: ["organizationName", "organizationType"],
  properties: {
    organizationName: {
      type: "string",
      minLength: 1,
      maxLength: 200,
    },
    organizationType: {
      type: "string",
      enum: organizationTypeValues,
    },
    parentOrganizationId: optionalPositiveIdSchema,
    managerUserId: optionalPositiveIdSchema,
    effectiveFrom: optionalDateSchema,
    effectiveTo: optionalDateSchema,
    description: optionalDescriptionSchema,
  },
} as const;

export const changeOrganizationStatusQuerySchema = {
  type: "object",
  additionalProperties: false,
  required: ["status"],
  properties: {
    status: {
      type: "string",
      enum: organizationStatusValues,
    },
  },
} as const;
