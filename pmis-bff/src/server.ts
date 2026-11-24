import Fastify from "fastify";
import helmet from "@fastify/helmet";
import cors from "@fastify/cors";
import cookie from "@fastify/cookie";
import { env } from "./config/env.js";
import { createSessionStore } from "./auth/session/session-store-factory.js";
import { createAuthService } from "./auth/services/auth.service.js";
import authRoutes from "./auth/routes/auth.routes.js";
import projectRoutes from "./project/routes/project.routes.js";
import projectDetailRoutes from "./project/routes/project-detail.routes.js";
import projectOperationHandoverRoutes from "./project-operation-handover/routes/project-operation-handover.routes.js";
import operationScopeRoutes from "./operation-scope/routes/operation-scope.routes.js";
import operationalScopeCiRoutes from "./operational-scope-ci/routes/operational-scope-ci.routes.js";
import operationalScopeRoutes from "./operational-scope/routes/operational-scope.routes.js";

const app = Fastify({
  logger: true,
  ajv: {
    customOptions: {
      removeAdditional: false,
    },
  },
});

app.decorateRequest("backendAccessToken", null);

async function startServer(): Promise<void> {
  const sessionStoreRuntime = await createSessionStore((error) => {
    app.log.error(error, "Redis session store error");
  });

  app.decorate("sessionStore", sessionStoreRuntime.store);
  app.decorate("authService", createAuthService(sessionStoreRuntime.store));

  app.addHook("onClose", async () => {
    await sessionStoreRuntime.close();
  });

  await app.register(helmet);

  await app.register(cors, {
    origin: env.frontendOrigin,
    credentials: true,
  });

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

  app.get("/health", async () => {
    return {
      status: "UP",
      service: "pmis-bff",
    };
  });

  await app.listen({
    port: env.port,
    host: env.host,
  });

  app.log.info(
    { sessionStore: env.sessionStore },
    "Session store initialized",
  );

  const shutdown = async (signal: NodeJS.Signals): Promise<void> => {
    app.log.info({ signal }, "Shutting down server");

    try {
      await app.close();
      process.exit(0);
    } catch (error) {
      app.log.error(error, "Server shutdown failed");
      process.exit(1);
    }
  };

  process.once("SIGINT", () => {
    void shutdown("SIGINT");
  });

  process.once("SIGTERM", () => {
    void shutdown("SIGTERM");
  });
}

startServer().catch((error: unknown) => {
  app.log.error(error);
  process.exit(1);
});