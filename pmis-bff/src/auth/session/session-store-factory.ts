import { createClient } from "redis";
import { env } from "../../config/env.js";
import { MemorySessionStore } from "./memory-session-store.js";
import { RedisSessionStore } from "./redis-session-store.js";
import type { SessionStore } from "./session-store.js";

export interface SessionStoreRuntime {
  store: SessionStore;
  close: () => Promise<void>;
}

export async function createSessionStore(
  onRedisError: (error: Error) => void,
): Promise<SessionStoreRuntime> {
  if (env.sessionStore === "memory") {
    return {
      store: new MemorySessionStore(),
      close: async () => {},
    };
  }

  if (!env.redisUrl) {
    throw new Error("REDIS_URL is required when SESSION_STORE=redis");
  }

  const client = createClient({
    url: env.redisUrl,
  });

  client.on("error", onRedisError);

  await client.connect();

  return {
    store: new RedisSessionStore(client),
    close: async () => {
      if (client.isOpen) {
        await client.quit();
      }
    },
  };
}
