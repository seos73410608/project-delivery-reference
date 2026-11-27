import Fastify from "fastify";
import cookie from "@fastify/cookie";
import type { AuthService } from "../../src/auth/services/auth.service.js";
import type { SessionStore } from "../../src/auth/session/session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import type {} from "../../src/auth/types/fastify.d.ts";
import authRoutes from "../../src/auth/routes/auth.routes.js";
import projectRoutes from "../../src/project/routes/project.routes.js";
import projectDetailRoutes from "../../src/project/routes/project-detail.routes.js";
import projectOperationRoutes from "../../src/project-operation/routes/project-operation.routes.js";
import projectOperationHandoverRoutes from "../../src/project-operation-handover/routes/project-operation-handover.routes.js";
import operationRoutes from "../../src/operation/routes/operation.routes.js";
import operationParticipantRoutes from "../../src/operation/routes/operation-participant.routes.js";
import operationScopeRoutes from "../../src/operation-scope/routes/operation-scope.routes.js";
import operationalScopeCiRoutes from "../../src/operational-scope-ci/routes/operational-scope-ci.routes.js";
import operationalScopeRoutes from "../../src/operational-scope/routes/operational-scope.routes.js";
import companyRoutes from "../../src/company/routes/company.routes.js";
import organizationRoutes from "../../src/organization/routes/organization.routes.js";
import contractRoutes from "../../src/contract/routes/contract.routes.js";

interface TestAppDependencies {
  sessionStore: SessionStore;
  authService: Pick<AuthService, "login" | "completeSessionSelection"> & {
    refresh?: (sessionId: string) => Promise<BffSession>;
    refreshAccessToken?: (
      sessionId: string,
      rejectedAccessToken: string,
    ) => Promise<BffSession>;
    logout?: (sessionId: string) => Promise<void>;
  };
}

export async function createTestApp(
  dependencies: TestAppDependencies,
) {
  const app = Fastify({
    logger: false,
    ajv: {
      customOptions: {
        removeAdditional: false,
      },
    },
  });

  app.decorateRequest("backendAccessToken", null);
  app.decorateRequest("backendSessionId", null);
  app.decorateRequest("backendAuthContext", null);

  app.decorate("sessionStore", dependencies.sessionStore);
  app.decorate(
    "authService",
    dependencies.authService as AuthService,
  );

  await app.register(cookie);

  await app.register(authRoutes, {
    prefix: "/api/auth",
  });

  await app.register(projectRoutes, {
    prefix: "/api/projects",
  });

  await app.register(projectDetailRoutes, {
    prefix: "/api/projects",
  });

  await app.register(projectOperationRoutes, {
    prefix: "/api/project-operations",
  });

  await app.register(projectOperationHandoverRoutes, {
    prefix: "/api/project-operation-handovers",
  });

  await app.register(operationScopeRoutes, {
    prefix: "/api/operation-scopes",
  });

  await app.register(operationalScopeCiRoutes, {
    prefix: "/api/operational-scopes",
  });

  await app.register(operationalScopeRoutes, {
    prefix: "/api/operational-scopes",
  });

  await app.register(operationRoutes, {
    prefix: "/api/operations",
  });

  await app.register(operationParticipantRoutes, {
    prefix: "/api/operations",
  });

  await app.register(companyRoutes, {
    prefix: "/api/companies",
  });

  await app.register(organizationRoutes, {
    prefix: "/api/companies",
  });

  await app.register(contractRoutes, {
    prefix: "/api/contracts",
  });

  await app.ready();

  return app;
}