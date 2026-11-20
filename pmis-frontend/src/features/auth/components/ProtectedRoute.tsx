
import { Navigate, Outlet, useLocation } from "react-router-dom";

import { useAuth } from "@/features/auth/context/AuthProvider";

function ProtectedRoute() {
    const location = useLocation();
    const { isInitializing, isAuthenticated } = useAuth();

    // 앱 시작 시 BFF 세션 인증 복원 대기
    if (isInitializing) {
        return (
            <div role="status" aria-live="polite">
                인증 정보를 확인하고 있습니다...
            </div>
        );
    }

    // 인증되지 않은 사용자는 로그인 페이지로 이동
    if (!isAuthenticated) {
        return (
            <Navigate
                to="/login"
                replace
                state={{ from: location }}
            />
        );
    }

    return <Outlet />;
}

export default ProtectedRoute;
