
const projectStatusValues = [
  "PLANNING",
  "IN_PROGRESS",
  "ON_HOLD",
  "COMPLETED",
  "CANCELLED",
] as const;

const projectPriorityValues = [
  "LOW",
  "MEDIUM",
  "HIGH",
  "CRITICAL",
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

export const projectIdParamsSchema = {
  type: "object",
  required: ["projectId"],
  additionalProperties: false,
  properties: {
    projectId: {
      type: "string",
      pattern: "^[1-9][0-9]*$",
    },
  },
} as const;

export const createProjectBodySchema = {
  type: "object",
  required: [
    "projectCode",
    "projectName",
    "customerName",
    "projectManager",
    "startDate",
    "endDate",
    "priority",
  ],
  additionalProperties: false,
  properties: {
    projectCode: requiredTextSchema(30),
    projectName: requiredTextSchema(200),
    customerName: requiredTextSchema(100),
    projectManager: requiredTextSchema(100),
    description: { type: "string", maxLength: 5000 },
    startDate: dateSchema,
    endDate: dateSchema,
    priority: {
      type: "string",
      enum: projectPriorityValues,
    },
  },
} as const;

export const updateProjectBodySchema = {
  type: "object",
  required: [
    "projectName",
    "customerName",
    "projectManager",
    "startDate",
    "endDate",
    "status",
    "priority",
  ],
  additionalProperties: false,
  properties: {
    projectName: requiredTextSchema(200),
    customerName: requiredTextSchema(100),
    projectManager: requiredTextSchema(100),
    description: { type: "string", maxLength: 5000 },
    startDate: dateSchema,
    endDate: dateSchema,
    status: {
      type: "string",
      enum: projectStatusValues,
    },
    priority: {
      type: "string",
      enum: projectPriorityValues,
    },
  },
} as const;

export const searchProjectsQuerySchema = {
  type: "object",
  additionalProperties: false,
  properties: {
    projectCode: { type: "string" },
    projectName: { type: "string" },
    customerName: { type: "string" },
    projectManager: { type: "string" },
    status: {
      type: "string",
      enum: projectStatusValues,
    },
    priority: {
      type: "string",
      enum: projectPriorityValues,
    },
    startDateFrom: dateSchema,
    startDateTo: dateSchema,
    endDateFrom: dateSchema,
    endDateTo: dateSchema,
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
