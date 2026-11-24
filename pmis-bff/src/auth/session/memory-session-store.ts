import type { SessionStore } from "./session-store.js";
import type { BffSession } from "./session.types.js";

export class MemorySessionStore implements SessionStore {
  private readonly sessions = new Map<string, BffSession>();

  async get(sessionId: string): Promise<BffSession | null> {
    const session = this.sessions.get(sessionId);

    if (!session) {
      return null;
    }

    if (session.expiresAt <= Date.now()) {
      this.sessions.delete(sessionId);
      return null;
    }

    return session;
  }

  async set(session: BffSession): Promise<void> {
    this.sessions.set(session.sessionId, session);
  }

  async delete(sessionId: string): Promise<void> {
    this.sessions.delete(sessionId);
  }

  async compareAndSet(
    sessionId: string,
    expectedAccessToken: string,
    session: BffSession,
  ): Promise<boolean> {
    const currentSession = await this.get(sessionId);

    if (
      !currentSession ||
      currentSession.accessToken !== expectedAccessToken ||
      session.sessionId !== sessionId
    ) {
      return false;
    }

    if (session.expiresAt <= Date.now()) {
      return false;
    }

    this.sessions.set(sessionId, session);

    return true;
  }
}