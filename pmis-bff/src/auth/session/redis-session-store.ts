import type { RedisClientType } from "redis";
import type { SessionStore } from "./session-store.js";
import type { BffSession } from "./session.types.js";

export class RedisSessionStore implements SessionStore {
  private readonly keyPrefix = "pmis:bff:session:";

  constructor(
    private readonly client: RedisClientType,
  ) {}

  async get(sessionId: string): Promise<BffSession | null> {
    const key = this.getKey(sessionId);
    const value = await this.client.get(key);

    if (value === null) {
      return null;
    }

    const session = JSON.parse(value) as BffSession;

    if (session.expiresAt <= Date.now()) {
      await this.delete(sessionId);
      return null;
    }

    return session;
  }

  async set(session: BffSession): Promise<void> {
    const ttlMilliseconds = session.expiresAt - Date.now();

    if (ttlMilliseconds <= 0) {
      await this.delete(session.sessionId);
      return;
    }

    await this.client.set(
      this.getKey(session.sessionId),
      JSON.stringify(session),
      {
        PX: ttlMilliseconds,
      },
    );
  }

  async delete(sessionId: string): Promise<void> {
    await this.client.del(this.getKey(sessionId));
  }

  async compareAndSet(
    sessionId: string,
    expectedAccessToken: string,
    session: BffSession,
  ): Promise<boolean> {
    if (
      session.sessionId !== sessionId ||
      session.expiresAt <= Date.now()
    ) {
      return false;
    }

    const key = this.getKey(sessionId);
    const ttlMilliseconds = session.expiresAt - Date.now();

    const result = await this.client.eval(
      `
        local current = redis.call("GET", KEYS[1])

        if not current then
          return 0
        end

        local decoded = cjson.decode(current)

        if decoded.accessToken ~= ARGV[1] then
          return 0
        end

        redis.call("SET", KEYS[1], ARGV[2], "PX", ARGV[3])

        return 1
      `,
      {
        keys: [key],
        arguments: [
          expectedAccessToken,
          JSON.stringify(session),
          String(ttlMilliseconds),
        ],
      },
    );

    return result === 1;
  }

  private getKey(sessionId: string): string {
    return `${this.keyPrefix}${sessionId}`;
  }
}