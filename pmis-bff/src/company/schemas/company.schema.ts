const companyStatusValues = [
  "ACTIVE",
  "INACTIVE",
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

const countryCodeSchema = {
  type: "string",
  pattern: "^[A-Z]{3}$",
} as const;

export const companyIdParamsSchema = {
  type: "object",
  required: ["companyId"],
  additionalProperties: false,
  properties: {
    companyId: {
      type: "string",
      pattern: "^[1-9][0-9]*$",
    },
  },
} as const;

export const companyRoleAssignmentParamsSchema = {
  type: "object",
  required: ["companyId", "assignmentId"],
  additionalProperties: false,
  properties: {
    companyId: {
      type: "string",
      pattern: "^[1-9][0-9]*$",
    },
    assignmentId: {
      type: "string",
      pattern: "^[1-9][0-9]*$",
    },
  },
} as const;

export const createCompanyBodySchema = {
  type: "object",
  required: [
    "companyCode",
    "companyName",
    "countryCode",
  ],
  additionalProperties: false,
  properties: {
    companyCode: requiredTextSchema(50),
    companyName: requiredTextSchema(200),
    legalName: { type: "string", maxLength: 250 },
    businessRegistrationNumber: {
      type: "string",
      maxLength: 30,
    },
    countryCode: countryCodeSchema,
    website: { type: "string", maxLength: 500 },
    description: { type: "string" },
  },
} as const;

export const updateCompanyBodySchema = {
  type: "object",
  required: [
    "companyName",
    "countryCode",
  ],
  additionalProperties: false,
  properties: {
    companyName: requiredTextSchema(200),
    legalName: { type: "string", maxLength: 250 },
    businessRegistrationNumber: {
      type: "string",
      maxLength: 30,
    },
    countryCode: countryCodeSchema,
    website: { type: "string", maxLength: 500 },
    description: { type: "string" },
  },
} as const;

export const searchCompaniesQuerySchema = {
  type: "object",
  additionalProperties: false,
  properties: {
    companyCode: { type: "string" },
    companyName: { type: "string" },
    businessRegistrationNumber: { type: "string" },
    countryCode: countryCodeSchema,
    status: {
      type: "string",
      enum: companyStatusValues,
    },
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

export const changeCompanyStatusQuerySchema = {
  type: "object",
  required: ["status"],
  additionalProperties: false,
  properties: {
    status: {
      type: "string",
      enum: companyStatusValues,
    },
  },
} as const;

export const createCompanyRoleAssignmentBodySchema = {
  type: "object",
  required: [
    "roleCode",
    "effectiveFrom",
  ],
  additionalProperties: false,
  properties: {
    roleCode: requiredTextSchema(30),
    effectiveFrom: dateSchema,
    description: { type: "string", maxLength: 500 },
  },
} as const;

export const closeCompanyRoleAssignmentBodySchema = {
  type: "object",
  required: ["effectiveTo"],
  additionalProperties: false,
  properties: {
    effectiveTo: dateSchema,
  },
} as const;