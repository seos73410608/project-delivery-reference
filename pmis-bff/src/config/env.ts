import "dotenv/config";

function getRequiredString(name: string): string {
  const value = process.env[name]?.trim();

  if (!value) {
    throw new Error(`Missing required environment variable: ${name}`);
  }

  return value;
}

function getPort(): number {
  const value = getRequiredString("PORT");
  const port = Number(value);

  if (!Number.isInteger(port) || port < 1 || port > 65535) {
    throw new Error("PORT must be an integer between 1 and 65535");
  }

  return port;
}

function getUrl(name: string): string {
  const value = getRequiredString(name);

  try {
    const url = new URL(value);

    if (url.protocol !== "http:" && url.protocol !== "https:") {
      throw new Error();
    }

    return url.origin;
  } catch {
    throw new Error(`${name} must be a valid HTTP or HTTPS URL`);
  }
}

function getSessionStore(): "memory" | "redis" {
  const value = getRequiredString("SESSION_STORE");

  if (value !== "memory" && value !== "redis") {
    throw new Error("SESSION_STORE must be either memory or redis");
  }

  return value;
}

function getRedisUrl(sessionStore: "memory" | "redis"): string | null {
  if (sessionStore === "memory") {
    return null;
  }

  const value = getRequiredString("REDIS_URL");

  try {
    const url = new URL(value);

    if (url.protocol !== "redis:" && url.protocol !== "rediss:") {
      throw new Error();
    }

    return url.toString();
  } catch {
    throw new Error("REDIS_URL must be a valid Redis or Redis TLS URL");
  }
}

const nodeEnv = process.env.NODE_ENV ?? "development";

if (!["development", "production", "test"].includes(nodeEnv)) {
  throw new Error("NODE_ENV must be development, production, or test");
}

const sessionStore = getSessionStore();

if (nodeEnv === "production" && sessionStore !== "redis") {
  throw new Error("SESSION_STORE=redis is required in production");
}

export const env = Object.freeze({
  nodeEnv,
  host: getRequiredString("HOST"),
  port: getPort(),
  frontendOrigin: getUrl("FRONTEND_ORIGIN"),
  backendApiUrl: getUrl("BACKEND_API_URL"),
  sessionStore,
  redisUrl: getRedisUrl(sessionStore),
});
