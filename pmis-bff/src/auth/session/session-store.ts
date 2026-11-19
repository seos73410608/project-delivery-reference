import type { BffSession } from "./session.types.js";

export interface SessionStore {
  get(sessionId: string): Promise<BffSession | null>;
  set(session: BffSession): Promise<void>;
  delete(sessionId: string): Promise<void>;
}
