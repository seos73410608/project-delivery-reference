import type { CookieSerializeOptions } from "@fastify/cookie";
import { env } from "../../config/env.js";

export const SESSION_COOKIE_NAME = "pmis_session";

export function getSessionCookieOptions(
  expiresAt: number,
): CookieSerializeOptions {
  const maxAge = Math.floor((expiresAt - Date.now()) / 1000);

  return {
    httpOnly: true,
    secure: env.nodeEnv === "production",
    sameSite: "lax",
    path: "/",
    maxAge: Math.max(0, maxAge),
  };
}

export const SESSION_COOKIE_CLEAR_OPTIONS: CookieSerializeOptions = {
  httpOnly: true,
  secure: env.nodeEnv === "production",
  sameSite: "lax",
  path: "/",
};
