
import Fastify from "fastify";
import cookie from "@fastify/cookie";
import type { AuthService } from "../../src/auth/services/auth.service.js";
import type { SessionStore } from "../../src/auth/session/session-store.js";
import type { BffSession } from "../../src/auth/session/session.types.js";
import authRoutes from "../../src/auth/routes/auth.routes.js";

interface TestAppDependencies {
  sessionStore: SessionStore;
  authService: Pick<AuthService, "login" | "completeSessionSelection"> & {
    refresh?: (sessionId: string) => Promise<BffSession>;
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

  app.decorate("sessionStore", dependencies.sessionStore);
  app.decorate(
    "authService",
    dependencies.authService as AuthService,
  );

  await app.register(cookie);
  await app.register(authRoutes, {
    prefix: "/api/auth",
  });

  await app.ready();

  return app;
}
