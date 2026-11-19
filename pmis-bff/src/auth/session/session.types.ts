export interface AuthUser {
  userId: number;
  username: string;
  name: string;
  role: string;
}

export interface BffSession {
  sessionId: string;
  accessToken: string;
  refreshToken: string;
  user: AuthUser;
  expiresAt: number;
  createdAt: number;
  updatedAt: number;
}
