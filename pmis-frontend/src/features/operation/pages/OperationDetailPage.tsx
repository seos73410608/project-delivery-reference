
import {
    useCallback,
    useEffect,
    useState,
} from "react";

import {
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    useAuth,
} from "@/features/auth/context/AuthProvider";

import {
    getApiErrorMessage,
} from "@/api/error";

import {
    getOperation,
    updateOperationStatus,
} from "../api/operationApi";

import type {
    OperationResponse,
    OperationStatus,
} from "../types/operation";

import OperationDetail from "../components/OperationDetail";

import "./OperationDetailPage.css";


const OPERATION_STATUS_OPTIONS: {
    value: OperationStatus;
    label: string;
}[] = [
    {
        value: "PLANNED",
        label: "계획",
    },
    {
        value: "ACTIVE",
        label: "운영 중",
    },
    {
        value: "SUSPENDED",
        label: "중지",
    },
    {
        value: "CLOSED",
        label: "종료",
    },
];


function OperationDetailPage() {

    const navigate = useNavigate();

    const { operationId } = useParams<{
        operationId: string;
    }>();

    const { user } = useAuth();

    const isAdmin = user?.role === "ROLE_ADMIN";

    const [operation, setOperation] =
        useState<OperationResponse | null>(null);

    const [loading, setLoading] = useState(true);
    const [statusUpdating, setStatusUpdating] = useState(false);

    const [error, setError] = useState<string | null>(null);
    const [statusError, setStatusError] = useState<string | null>(null);


    /**
     * 운영 서비스 상세 조회
     */
    const fetchOperation = useCallback(async () => {

        if (!operationId || !/^[1-9]\d*$/.test(operationId)) {
            setError("올바르지 않은 운영 서비스 ID입니다.");
            setOperation(null);
            setLoading(false);
            return;
        }

        try {

            setLoading(true);
            setError(null);

            const response = await getOperation(
                Number(operationId),
            );

            setOperation(response);

        } catch (err) {

            setError(
                getApiErrorMessage(
                    err,
                    "운영 서비스 정보를 불러오는 중 오류가 발생했습니다.",
                ),
            );

            setOperation(null);

        } finally {

            setLoading(false);
        }

    }, [operationId]);


    useEffect(() => {

        fetchOperation();

    }, [fetchOperation]);


    /**
     * 운영 상태 변경
     */
    const handleStatusChange = async (
        event: React.ChangeEvent<HTMLSelectElement>,
    ) => {

        if (!operation || !isAdmin || statusUpdating) {
            return;
        }

        const nextStatus = event.target.value as OperationStatus;

        if (nextStatus === operation.status) {
            return;
        }

        const confirmed = window.confirm(
            `운영 상태를 "${OPERATION_STATUS_OPTIONS.find(
                (option) => option.value === nextStatus,
            )?.label}"(으)로 변경하시겠습니까?`,
        );

        if (!confirmed) {
            return;
        }

        try {

            setStatusUpdating(true);
            setStatusError(null);

            await updateOperationStatus(
                operation.id,
                {
                    status: nextStatus,
                },
            );

            await fetchOperation();

        } catch (err) {

            setStatusError(
                getApiErrorMessage(
                    err,
                    "운영 상태 변경 중 오류가 발생했습니다.",
                ),
            );

        } finally {

            setStatusUpdating(false);
        }
    };


    if (loading) {

        return (
            <div className="operation-detail-page">

                <div className="operation-detail-page-state">
                    운영 서비스 정보를 불러오는 중입니다.
                </div>

            </div>
        );
    }


    if (error || !operation) {

        return (
            <div className="operation-detail-page">

                <div className="operation-detail-page-error">

                    <h2>
                        운영 서비스 정보를 불러올 수 없습니다.
                    </h2>

                    <p>
                        {error || "운영 서비스 정보가 존재하지 않습니다."}
                    </p>

                    <button
                        type="button"
                        className="button button--secondary"
                        onClick={() => navigate("/operation")}
                    >
                        목록으로 돌아가기
                    </button>

                </div>

            </div>
        );
    }


    return (
        <div className="operation-detail-page">

            {/* 페이지 헤더 */}

            <div className="operation-detail-page-heading">

                <div>

                    <h1>
                        운영 서비스 상세
                    </h1>

                    <p>
                        운영 서비스의 상세 정보와 현재 상태를 확인합니다.
                    </p>

                </div>

                <div className="operation-detail-page-actions">

                    {isAdmin && (
                        <button
                            type="button"
                            className="button button--primary"
                            onClick={() =>
                                navigate(
                                    `/operation/${operation.id}/edit`,
                                )
                            }
                        >
                            수정
                        </button>
                    )}

                    <button
                        type="button"
                        className="button button--secondary"
                        onClick={() => navigate("/operation")}
                    >
                        목록으로
                    </button>

                </div>

            </div>


            {/* 운영 상태 관리 */}

            {isAdmin && (
                <section className="operation-detail-status-panel">

                    <div className="operation-detail-status-info">

                        <h3>
                            운영 상태 관리
                        </h3>

                        <p>
                            운영 서비스의 현재 상태를 변경할 수 있습니다.
                        </p>

                    </div>

                    <div className="operation-detail-status-control">

                        <label htmlFor="operationStatus">
                            운영 상태
                        </label>

                        <select
                            id="operationStatus"
                            className="form__select"
                            value={operation.status}
                            onChange={handleStatusChange}
                            disabled={statusUpdating}
                        >

                            {OPERATION_STATUS_OPTIONS.map((option) => (
                                <option
                                    key={option.value}
                                    value={option.value}
                                >
                                    {option.label}
                                </option>
                            ))}

                        </select>

                        {statusUpdating && (
                            <span className="operation-detail-status-loading">
                                상태 변경 중...
                            </span>
                        )}

                    </div>

                    {statusError && (
                        <div className="operation-detail-status-error">
                            {statusError}
                        </div>
                    )}

                </section>
            )}


            {/* 상세 정보 */}

            <OperationDetail
                operation={operation}
            />

        </div>
    );
}


export default OperationDetailPage;
