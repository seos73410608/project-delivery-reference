
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { useAuth } from "@/features/auth/context/AuthProvider";

function Header() {
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const [isLoggingOut, setIsLoggingOut] = useState(false);
    const [logoutError, setLogoutError] = useState("");

    const handleLogout = async () => {
        if (isLoggingOut) {
            return;
        }

        setIsLoggingOut(true);
        setLogoutError("");

        try {
            await logout();

            navigate("/login", { replace: true });
        } catch {
            setLogoutError(
                "로그아웃에 실패했습니다. 잠시 후 다시 시도해 주세요.",
            );
        } finally {
            setIsLoggingOut(false);
        }
    };

    return (
        <header className="app-header">
            <div className="app-header__brand">
                <h1>PMIS</h1>
                <span>Project Management Information System</span>
            </div>

            <div className="app-header__user">
                <div>
                    <strong>{user?.name ?? "사용자"}</strong>
                    <span>
                        {user
                            ? `${user.username} / ${user.role.replace(/^ROLE_/, "")}`
                            : ""}
                    </span>
                </div>

                <button
                    type="button"
                    onClick={handleLogout}
                    disabled={isLoggingOut}
                >
                    {isLoggingOut ? "로그아웃 중..." : "로그아웃"}
                </button>
            </div>

            {logoutError && (
                <p role="alert">
                    {logoutError}
                </p>
            )}
        </header>
    );
}

export default Header;
