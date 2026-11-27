const contractTypeValues = [
  "PROJECT",
  "MAINTENANCE",
  "OPERATION",
  "SUPPLY",
  "SERVICE",
  "OTHER",
] as const;

const contractStatusValues = [
  "DRAFT",
  "ACTIVE",
  "SUSPENDED",
  "COMPLETED",
  "EXPIRED",
  "TERMINATED",
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

const currencyCodeSchema = {
  type: "string",
  pattern: "^[A-Z]{3}$",
} as const;

const contractAmountSchema = {
  type: "number",
  minimum: 0,
} as const;

export const contractIdParamsSchema = {
  type: "object",
  required: ["contractId"],
  additionalProperties: false,
  properties: {
    contractId: {
      type: "string",
      pattern: "^[1-9][0-9]*$",
    },
  },
} as const;

export const createContractBodySchema = {
  type: "object",
  required: [
    "contractCode",
    "contractName",
    "contractType",
    "startDate",
    "endDate",
    "currencyCode",
  ],
  additionalProperties: false,
  properties: {
    contractCode: requiredTextSchema(50),
    contractName: requiredTextSchema(200),
    contractType: {
      type: "string",
      enum: contractTypeValues,
    },
    signedDate: dateSchema,
    startDate: dateSchema,
    endDate: dateSchema,
    currencyCode: currencyCodeSchema,
    contractAmount: contractAmountSchema,
    description: { type: "string" },
  },
} as const;

export const updateContractBodySchema = {
  type: "object",
  required: [
    "contractName",
    "contractType",
    "startDate",
    "endDate",
    "currencyCode",
  ],
  additionalProperties: false,
  properties: {
    contractName: requiredTextSchema(200),
    contractType: {
      type: "string",
      enum: contractTypeValues,
    },
    signedDate: dateSchema,
    startDate: dateSchema,
    endDate: dateSchema,
    currencyCode: currencyCodeSchema,
    contractAmount: contractAmountSchema,
    description: { type: "string" },
  },
} as const;

export const searchContractsQuerySchema = {
  type: "object",
  additionalProperties: false,
  properties: {
    contractCode: { type: "string" },
    contractName: { type: "string" },
    contractType: {
      type: "string",
      enum: contractTypeValues,
    },
    status: {
      type: "string",
      enum: contractStatusValues,
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

export const changeContractStatusQuerySchema = {
  type: "object",
  required: ["status"],
  additionalProperties: false,
  properties: {
    status: {
      type: "string",
      enum: contractStatusValues,
    },
  },
} as const;