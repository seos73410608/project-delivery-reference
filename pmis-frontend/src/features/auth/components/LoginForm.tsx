import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { login } from "@/features/auth/api/authApi";
import { setAccessToken } from "@/features/auth/utils/authStorage";


function LoginForm() {
    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");


    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
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

            const response = await login({
                username: username.trim(),
                password,
            });


            setAccessToken(response.accessToken);

            navigate("/", {
                replace: true,
            });
        } catch (error) {
            console.error("Login failed:", error);

            setErrorMessage(
                "로그인에 실패했습니다. 아이디 또는 비밀번호를 확인해주세요.",
            );
        } finally {
            setLoading(false);
        }
    };


    return (
        <form
            onSubmit={handleSubmit}
            style={{
                width: "100%",
            }}
        >
            {/* Username */}
            <div
                style={{
                    marginBottom: "16px",
                }}
            >
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


            {/* Password */}
            <div
                style={{
                    marginBottom: "16px",
                }}
            >
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


            {/* Error */}
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


            {/* Submit */}
            <button
                type="submit"
                disabled={loading}
                style={{
                    width: "100%",
                    padding: "11px 12px",
                    border: "none",
                    borderRadius: "4px",
                    backgroundColor: loading
                        ? "#999999"
                        : "#1976d2",
                    color: "#ffffff",
                    fontSize: "15px",
                    fontWeight: "bold",
                    cursor: loading
                        ? "not-allowed"
                        : "pointer",
                }}
            >
                {loading ? "로그인 중..." : "로그인"}
            </button>
        </form>
    );
}


export default LoginForm;