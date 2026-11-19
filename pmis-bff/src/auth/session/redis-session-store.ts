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

  private getKey(sessionId: string): string {
    return `${this.keyPrefix}${sessionId}`;
  }
}
