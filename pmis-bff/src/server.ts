import Fastify from "fastify";
import helmet from "@fastify/helmet";
import cors from "@fastify/cors";
import { env } from "./config/env.js";

const app = Fastify({
  logger: true,
});

async function startServer(): Promise<void> {
  await app.register(helmet);

  await app.register(cors, {
    origin: env.frontendOrigin,
    credentials: true,
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
}

startServer().catch((error: unknown) => {
  app.log.error(error);
  process.exit(1);
});
