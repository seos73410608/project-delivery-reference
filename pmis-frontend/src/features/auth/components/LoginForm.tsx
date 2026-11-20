
import { useState, type FormEvent } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import { useAuth } from "@/features/auth/context/AuthProvider";
import type {
    ActiveSession,
    SessionSelectionRequired,
} from "@/features/auth/types/auth";

function formatSessionDate(value: string): string {
    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString("ko-KR");
}

function LoginForm() {
    const navigate = useNavigate();
    const location = useLocation();

    const { login, completeSessionSelection } = useAuth();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const [pendingSelection, setPendingSelection] =
        useState<SessionSelectionRequired | null>(null);

    const [selectedSessionIds, setSelectedSessionIds] =
        useState<string[]>([]);

    const returnPath = (() => {
        const state = location.state as {
            from?: { pathname?: string };
        } | null;

        const pathname = state?.from?.pathname;

        if (
            typeof pathname === "string" &&
            pathname.startsWith("/") &&
            !pathname.startsWith("//") &&
            pathname !== "/login"
        ) {
            return pathname;
        }

        return "/";
    })();

    const handleLogin = async (
        event: FormEvent<HTMLFormElement>,
    ) => {
        event.preventDefault();
        setErrorMessage("");

        if (!username.trim()) {
            setErrorMessage("아이디를 입력해주세요.");
            return;
        }

        if (!password) {
            setErrorMessage("비밀번호를 입력해주세요.");
            return;
        }

        try {
            setLoading(true);

            const result = await login({
                username: username.trim(),
                password,
            });

            if (result.status === "SUCCESS") {
                navigate(returnPath, { replace: true });
                return;
            }

            setPendingSelection(result);
            setSelectedSessionIds([]);
        } catch {
            setErrorMessage(
                "로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요.",
            );
        } finally {
            setLoading(false);
        }
    };

    const handleSessionToggle = (sessionId: string) => {
        setSelectedSessionIds((current) => {
            if (current.includes(sessionId)) {
                return current.filter((id) => id !== sessionId);
            }

            if (
                !pendingSelection ||
                current.length >= pendingSelection.requiredTerminationCount
            ) {
                return current;
            }

            return [...current, sessionId];
        });

        setErrorMessage("");
    };

    const handleCompleteSelection = async () => {
        if (!pendingSelection || loading) {
            return;
        }

        if (
            selectedSessionIds.length !==
            pendingSelection.requiredTerminationCount
        ) {
            return;
        }

        setErrorMessage("");

        try {
            setLoading(true);

            const result = await completeSessionSelection({
                loginAttemptId: pendingSelection.loginAttemptId,
                sessionIds: selectedSessionIds,
            });

            if (result.status === "SUCCESS") {
                navigate(returnPath, { replace: true });
                return;
            }

            setPendingSelection(result);
            setSelectedSessionIds([]);
            setErrorMessage(
                "세션 선택 상태가 변경되었습니다. 다시 선택해주세요.",
            );
        } catch {
            setErrorMessage(
                "세션 선택 처리에 실패했습니다. 다시 시도해주세요.",
            );
        } finally {
            setLoading(false);
        }
    };

    const handleBackToLogin = () => {
        setPendingSelection(null);
        setSelectedSessionIds([]);
        setErrorMessage("");
        setPassword("");
    };

    if (pendingSelection) {
        return (
            <section style={{ width: "100%" }}>
                <h2>기존 로그인 세션 선택</h2>

                <p>
                    {`종료할 세션을 ${pendingSelection.requiredTerminationCount}개 선택해주세요.`}
                </p>

                <p>
                    선택한 기존 세션만 종료하고 새 로그인을 진행합니다.
                </p>

                <div
                    style={{
                        marginBottom: "16px",
                        display: "flex",
                        flexDirection: "column",
                        gap: "10px",
                    }}
                >
                    {pendingSelection.activeSessions.map(
                        (session: ActiveSession) => {
                            const checked = selectedSessionIds.includes(
                                session.sessionId,
                            );

                            return (
                                <label
                                    key={session.sessionId}
                                    style={{
                                        display: "block",
                                        padding: "12px",
                                        border: "1px solid #cccccc",
                                        borderRadius: "4px",
                                        cursor: loading
                                            ? "not-allowed"
                                            : "pointer",
                                    }}
                                >
                                    <input
                                        type="checkbox"
                                        checked={checked}
                                        disabled={
                                            loading ||
                                            (!checked &&
                                                selectedSessionIds.length >=
                                                    pendingSelection.requiredTerminationCount)
                                        }
                                        onChange={() =>
                                            handleSessionToggle(
                                                session.sessionId,
                                            )
                                        }
                                        style={{ marginRight: "8px" }}
                                    />

                                    <strong>{session.sessionId}</strong>

                                    <div
                                        style={{
                                            marginTop: "8px",
                                            marginLeft: "24px",
                                            fontSize: "13px",
                                            color: "#555555",
                                        }}
                                    >
                                        <div>
                                            로그인:{" "}
                                            {formatSessionDate(
                                                session.createdAt,
                                            )}
                                        </div>
                                        <div>
                                            최근 활동:{" "}
                                            {formatSessionDate(
                                                session.lastAccessedAt,
                                            )}
                                        </div>
                                    </div>
                                </label>
                            );
                        },
                    )}
                </div>

                <p aria-live="polite">
                    {selectedSessionIds.length} /{" "}
                    {pendingSelection.requiredTerminationCount}개 선택
                </p>

                {errorMessage && (
                    <div
                        role="alert"
                        style={{
                            marginBottom: "16px",
                            padding: "10px 12px",
                            border: "1px solid #f1b0b7",
                            borderRadius: "4px",
                            backgroundColor: "#f8d7da",
                            color: "#842029",
                            fontSize: "14px",
                        }}
                    >
                        {errorMessage}
                    </div>
                )}

                <button
                    type="button"
                    onClick={handleCompleteSelection}
                    disabled={
                        loading ||
                        selectedSessionIds.length !==
                            pendingSelection.requiredTerminationCount
                    }
                    style={{
                        width: "100%",
                        padding: "11px 12px",
                        border: "none",
                        borderRadius: "4px",
                        backgroundColor:
                            loading ||
                            selectedSessionIds.length !==
                                pendingSelection.requiredTerminationCount
                                ? "#999999"
                                : "#1976d2",
                        color: "#ffffff",
                        fontSize: "15px",
                        fontWeight: "bold",
                        cursor:
                            loading ||
                            selectedSessionIds.length !==
                                pendingSelection.requiredTerminationCount
                                ? "not-allowed"
                                : "pointer",
                    }}
                >
                    {loading
                        ? "처리 중..."
                        : "선택한 세션 종료 후 로그인"}
                </button>

                <button
                    type="button"
                    onClick={handleBackToLogin}
                    disabled={loading}
                    style={{
                        width: "100%",
                        marginTop: "10px",
                        padding: "10px",
                        border: "1px solid #cccccc",
                        borderRadius: "4px",
                        backgroundColor: "#ffffff",
                        cursor: loading ? "not-allowed" : "pointer",
                    }}
                >
                    다른 계정으로 로그인
                </button>
            </section>
        );
    }

    return (
        <form
            onSubmit={handleLogin}
            style={{ width: "100%" }}
        >
            <div style={{ marginBottom: "16px" }}>
                <label
                    htmlFor="username"
                    style={{
                        display: "block",
                        marginBottom: "6px",
                        fontWeight: "bold",
                    }}
                >
                    Username
                </label>

                <input
                    id="username"
                    type="text"
                    value={username}
                    onChange={(event) =>
                        setUsername(event.target.value)
                    }
                    placeholder="아이디를 입력하세요"
                    autoComplete="username"
                    disabled={loading}
                    style={{
                        width: "100%",
                        padding: "10px 12px",
                        boxSizing: "border-box",
                        border: "1px solid #cccccc",
                        borderRadius: "4px",
                        fontSize: "14px",
                    }}
                />
            </div>

            <div style={{ marginBottom: "16px" }}>
                <label
                    htmlFor="password"
                    style={{
                        display: "block",
                        marginBottom: "6px",
                        fontWeight: "bold",
                    }}
                >
                    Password
                </label>

                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(event) =>
                        setPassword(event.target.value)
                    }
                    placeholder="비밀번호를 입력하세요"
                    autoComplete="current-password"
                    disabled={loading}
                    style={{
                        width: "100%",
                        padding: "10px 12px",
                        boxSizing: "border-box",
                        border: "1px solid #cccccc",
                        borderRadius: "4px",
                        fontSize: "14px",
                    }}
                />
            </div>

            {errorMessage && (
                <div
                    role="alert"
                    style={{
                        marginBottom: "16px",
                        padding: "10px 12px",
                        border: "1px solid #f1b0b7",
                        borderRadius: "4px",
                        backgroundColor: "#f8d7da",
                        color: "#842029",
                        fontSize: "14px",
                    }}
                >
                    {errorMessage}
                </div>
            )}

            <button
                type="submit"
                disabled={loading}
                style={{
                    width: "100%",
                    padding: "11px 12px",
                    border: "none",
                    borderRadius: "4px",
                    backgroundColor: loading ? "#999999" : "#1976d2",
                    color: "#ffffff",
                    fontSize: "15px",
                    fontWeight: "bold",
                    cursor: loading ? "not-allowed" : "pointer",
                }}
            >
                {loading ? "로그인 중..." : "로그인"}
            </button>
        </form>
    );
}

export default LoginForm;
