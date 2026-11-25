
import {
    useEffect,
    useState,
} from "react";

import {
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    getApiErrorMessage,
} from "@/api/error";

import {
    createOperation,
    getOperation,
    updateOperation,
} from "../api/operationApi";

import type {
    OperationResponse,
    OperationCreateRequest,
    OperationUpdateRequest,
} from "../types/operation";

import OperationForm from "../components/OperationForm";

import "./OperationFormPage.css";


function OperationFormPage() {

    const navigate = useNavigate();

    const { operationId } = useParams<{
        operationId: string;
    }>();

    const isEditMode = Boolean(operationId);

    const [operation, setOperation] =
        useState<OperationResponse | null>(null);

    const [loading, setLoading] = useState(isEditMode);
    const [error, setError] = useState<string | null>(null);


    /**
     * 수정 대상 운영 서비스 조회
     */
    useEffect(() => {

        if (!isEditMode || !operationId) {
            setLoading(false);
            return;
        }

        let active = true;

        const fetchOperation = async () => {

            try {

                setLoading(true);
                setError(null);

                const response = await getOperation(
                    Number(operationId),
                );

                if (active) {
                    setOperation(response);
                }

            } catch (err) {

                if (active) {
                    setError(
                        getApiErrorMessage(
                            err,
                            "운영 서비스 정보를 불러오는 중 오류가 발생했습니다.",
                        ),
                    );
                }

            } finally {

                if (active) {
                    setLoading(false);
                }
            }
        };

        fetchOperation();

        return () => {
            active = false;
        };

    }, [isEditMode, operationId]);


    /**
     * 신규 운영 서비스 등록
     */
    const handleCreate = async (
        request: OperationCreateRequest,
    ) => {

        const response = await createOperation(request);

        navigate(
            `/operation/${response.id}/detail`,
        );
    };


    /**
     * 운영 서비스 수정
     */
    const handleUpdate = async (
        request: OperationUpdateRequest,
    ) => {

        if (!operationId) {
            return;
        }

        await updateOperation(
            Number(operationId),
            request,
        );

        navigate(
            `/operation/${operationId}/detail`,
        );
    };


    /**
     * 취소
     */
    const handleCancel = () => {

        if (isEditMode && operationId) {

            navigate(
                `/operation/${operationId}/detail`,
            );

        } else {

            navigate("/operation");
        }
    };


    if (loading) {

        return (
            <div className="operation-form-page">

                <div className="operation-form-page-state">
                    운영 서비스 정보를 불러오는 중입니다.
                </div>

            </div>
        );
    }


    if (error) {

        return (
            <div className="operation-form-page">

                <div className="operation-form-page-error">

                    <p>
                        운영 서비스 정보를 불러오지 못했습니다.
                    </p>

                    <p>
                        {error}
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


    if (isEditMode && !operation) {

        return (
            <div className="operation-form-page">

                <div className="operation-form-page-state">
                    운영 서비스 정보를 찾을 수 없습니다.
                </div>

                <button
                    type="button"
                    className="button button--secondary"
                    onClick={() => navigate("/operation")}
                >
                    목록으로 돌아가기
                </button>

            </div>
        );
    }


    return (
        <div className="operation-form-page">

            <div className="operation-form-page-heading">

                <div>

                    <h1>
                        {isEditMode
                            ? "운영 서비스 수정"
                            : "운영 서비스 등록"}
                    </h1>

                    <p>
                        {isEditMode
                            ? "기존 운영 서비스 정보를 수정합니다."
                            : "새로운 운영 서비스를 등록합니다."}
                    </p>

                </div>

                <button
                    type="button"
                    className="button button--secondary"
                    onClick={handleCancel}
                >
                    목록으로
                </button>

            </div>


            <OperationForm
                mode={isEditMode ? "edit" : "create"}
                operation={operation}
                onCreate={handleCreate}
                onUpdate={handleUpdate}
                onCancel={handleCancel}
            />

        </div>
    );
}


export default OperationFormPage;
