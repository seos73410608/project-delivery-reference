import type { SessionStore } from "../auth/session/session-store.js";
import type { AuthService } from "../auth/services/auth.service.js";

declare module "fastify" {
  interface FastifyInstance {
    sessionStore: SessionStore;
    authService: AuthService;
  }
}
