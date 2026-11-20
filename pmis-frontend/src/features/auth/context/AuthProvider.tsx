
import {
    createContext,
    useCallback,
    useContext,
    useEffect,
    useMemo,
    useState,
    type PropsWithChildren,
} from "react";

import {
    login as apiLogin,
    completeSessionSelection as apiCompleteSessionSelection,
    getMe,
    logout as apiLogout,
} from "@/features/auth/api/authApi";

import type {
    AuthUser,
    LoginRequest,
    LoginResult,
    SessionSelectionRequest,
} from "@/features/auth/types/auth";

interface AuthContextValue {
    isInitializing: boolean;
    isAuthenticated: boolean;
    user: AuthUser | null;

    login: (request: LoginRequest) => Promise<LoginResult>;

    completeSessionSelection: (
        request: SessionSelectionRequest,
    ) => Promise<LoginResult>;

    logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: PropsWithChildren) {
    const [isInitializing, setIsInitializing] = useState(true);
    const [user, setUser] = useState<AuthUser | null>(null);

    // 앱 시작 시 BFF 세션을 통해 인증 상태 복원
    useEffect(() => {
        let mounted = true;

        const restoreAuthentication = async () => {
            try {
                const authenticatedUser = await getMe();

                if (mounted) {
                    setUser(authenticatedUser);
                }
            } catch {
                if (mounted) {
                    setUser(null);
                }
            } finally {
                if (mounted) {
                    setIsInitializing(false);
                }
            }
        };

        void restoreAuthentication();

        return () => {
            mounted = false;
        };
    }, []);

    // 로그인
    const login = useCallback(
        async (request: LoginRequest): Promise<LoginResult> => {
            const result = await apiLogin(request);

            if (result.status === "SUCCESS") {
                setUser(result.user);
            } else {
                setUser(null);
            }

            return result;
        },
        [],
    );

    // 기존 세션 선택 및 로그인 완료
    const completeSessionSelection = useCallback(
        async (
            request: SessionSelectionRequest,
        ): Promise<LoginResult> => {
            const result = await apiCompleteSessionSelection(request);

            if (result.status === "SUCCESS") {
                setUser(result.user);
            }

            return result;
        },
        [],
    );

    // BFF 로그아웃 성공 후에만 인증 상태 초기화
    const logout = useCallback(async (): Promise<void> => {
        await apiLogout();

        setUser(null);
    }, []);

    const value = useMemo<AuthContextValue>(
        () => ({
            isInitializing,
            isAuthenticated: user !== null,
            user,
            login,
            completeSessionSelection,
            logout,
        }),
        [
            isInitializing,
            user,
            login,
            completeSessionSelection,
            logout,
        ],
    );

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(): AuthContextValue {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error(
            "useAuth must be used within an AuthProvider",
        );
    }

    return context;
}
