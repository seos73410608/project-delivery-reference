
import {
    useEffect,
    useState,
} from "react";

import {
    getApiErrorMessage,
} from "@/api/error";

import type {
    OperationResponse,
    OperationType,
    OperationCreateRequest,
    OperationUpdateRequest,
} from "../types/operation";

import "./OperationForm.css";


interface OperationFormProps {

    /**
     * Form mode
     *
     * create:
     * 신규 운영 서비스 등록
     *
     * edit:
     * 기존 운영 서비스 수정
     */
    mode: "create" | "edit";

    /**
     * 수정 대상 운영 서비스
     */
    operation?: OperationResponse | null;

    /**
     * 신규 등록 callback
     */
    onCreate: (
        request: OperationCreateRequest,
    ) => Promise<void>;

    /**
     * 수정 callback
     */
    onUpdate: (
        request: OperationUpdateRequest,
    ) => Promise<void>;

    /**
     * 취소 callback
     */
    onCancel: () => void;
}


const OPERATION_TYPE_OPTIONS: {
    value: OperationType;
    label: string;
}[] = [
    {
        value: "BUSINESS_SERVICE",
        label: "업무 서비스",
    },
    {
        value: "APPLICATION_SERVICE",
        label: "애플리케이션 서비스",
    },
    {
        value: "INFRASTRUCTURE_SERVICE",
        label: "인프라 서비스",
    },
    {
        value: "PLATFORM_SERVICE",
        label: "플랫폼 서비스",
    },
    {
        value: "DATABASE_SERVICE",
        label: "데이터베이스 서비스",
    },
    {
        value: "OTHER",
        label: "기타",
    },
];


function OperationForm({
    mode,
    operation,
    onCreate,
    onUpdate,
    onCancel,
}: OperationFormProps) {

    const [operationCode, setOperationCode] = useState("");
    const [operationName, setOperationName] = useState("");

    const [operationType, setOperationType] =
        useState<OperationType>("BUSINESS_SERVICE");

    const [managerUserId, setManagerUserId] = useState("");

    const [effectiveFrom, setEffectiveFrom] = useState("");
    const [effectiveTo, setEffectiveTo] = useState("");

    const [description, setDescription] = useState("");

    const [error, setError] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);


    /**
     * 초기 데이터 설정
     */
    useEffect(() => {

        if (mode === "edit" && operation) {

            setOperationCode(operation.operationCode);
            setOperationName(operation.operationName);
            setOperationType(operation.operationType);

            setManagerUserId(
                operation.managerUserId !== null
                    ? String(operation.managerUserId)
                    : "",
            );

            setEffectiveFrom(operation.effectiveFrom ?? "");
            setEffectiveTo(operation.effectiveTo ?? "");

            setDescription(operation.description ?? "");

        } else {

            setOperationCode("");
            setOperationName("");
            setOperationType("BUSINESS_SERVICE");

            setManagerUserId("");

            setEffectiveFrom("");
            setEffectiveTo("");

            setDescription("");
        }

        setError(null);
        setSubmitting(false);

    }, [mode, operation]);


    /**
     * 입력값 검증
     */
    const validate = (): string | null => {

        if (mode === "create" && !operationCode.trim()) {
            return "운영 코드를 입력해 주세요.";
        }

        if (!operationName.trim()) {
            return "운영 서비스명을 입력해 주세요.";
        }

        if (
            managerUserId.trim() &&
            (
                !/^[1-9]\d*$/.test(managerUserId.trim()) ||
                !Number.isSafeInteger(Number(managerUserId.trim()))
            )
        ) {
            return "담당자 사용자 ID는 양의 정수로 입력해 주세요.";
        }

        if (effectiveTo && !effectiveFrom) {
            return "종료일을 입력하려면 시작일을 먼저 입력해야 합니다.";
        }

        if (
            effectiveFrom &&
            effectiveTo &&
            effectiveFrom > effectiveTo
        ) {
            return "운영 종료일은 시작일보다 빠를 수 없습니다.";
        }

        return null;
    };


    /**
     * Submit
     */
    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
    ) => {

        event.preventDefault();

        if (submitting) {
            return;
        }

        setError(null);

        const validationError = validate();

        if (validationError) {
            setError(validationError);
            return;
        }

        const normalizedManagerUserId =
            managerUserId.trim()
                ? Number(managerUserId.trim())
                : null;

        try {

            setSubmitting(true);

            if (mode === "create") {

                const request: OperationCreateRequest = {
                    operationCode: operationCode.trim(),
                    operationName: operationName.trim(),
                    operationType,
                    managerUserId: normalizedManagerUserId,
                    effectiveFrom: effectiveFrom || null,
                    effectiveTo: effectiveTo || null,
                    description: description.trim() || null,
                };

                await onCreate(request);

            } else {

                const request: OperationUpdateRequest = {
                    operationName: operationName.trim(),
                    operationType,
                    managerUserId: normalizedManagerUserId,
                    effectiveFrom: effectiveFrom || null,
                    effectiveTo: effectiveTo || null,
                    description: description.trim() || null,
                };

                await onUpdate(request);
            }

        } catch (err) {

            setError(
                getApiErrorMessage(
                    err,
                    "운영 서비스 저장 중 오류가 발생했습니다.",
                ),
            );

        } finally {

            setSubmitting(false);
        }
    };


    return (
        <div className="operation-form-card">

            <div className="operation-form-header">

                <h2>
                    {mode === "create"
                        ? "운영 서비스 등록"
                        : "운영 서비스 수정"}
                </h2>

                <p>
                    운영 서비스의 기본 정보를 입력해 주세요.
                </p>

            </div>


            <form
                className="form"
                onSubmit={handleSubmit}
            >

                <div className="operation-form-grid">

                    {/* 운영 코드 */}

                    <div className="form__field">

                        <label htmlFor="operationCode">
                            운영 코드
                            {mode === "create" && (
                                <span className="operation-form-required">
                                    *
                                </span>
                            )}
                        </label>

                        <input
                            id="operationCode"
                            className="form__input"
                            type="text"
                            value={operationCode}
                            onChange={(event) =>
                                setOperationCode(event.target.value)
                            }
                            placeholder="예: OPS-001"
                            maxLength={50}
                            disabled={mode === "edit" || submitting}
                            required={mode === "create"}
                        />

                        {mode === "edit" && (
                            <small className="form__help">
                                운영 코드는 등록 후 변경할 수 없습니다.
                            </small>
                        )}

                    </div>


                    {/* 운영 서비스명 */}

                    <div className="form__field">

                        <label htmlFor="operationName">
                            운영 서비스명
                            <span className="operation-form-required">
                                *
                            </span>
                        </label>

                        <input
                            id="operationName"
                            className="form__input"
                            type="text"
                            value={operationName}
                            onChange={(event) =>
                                setOperationName(event.target.value)
                            }
                            placeholder="운영 서비스명을 입력하세요."
                            maxLength={200}
                            disabled={submitting}
                            required
                        />

                    </div>


                    {/* 운영 유형 */}

                    <div className="form__field">

                        <label htmlFor="operationType">
                            운영 유형
                            <span className="operation-form-required">
                                *
                            </span>
                        </label>

                        <select
                            id="operationType"
                            className="form__select"
                            value={operationType}
                            onChange={(event) =>
                                setOperationType(
                                    event.target.value as OperationType,
                                )
                            }
                            disabled={submitting}
                            required
                        >

                            {OPERATION_TYPE_OPTIONS.map((option) => (
                                <option
                                    key={option.value}
                                    value={option.value}
                                >
                                    {option.label}
                                </option>
                            ))}

                        </select>

                    </div>


                    {/* 담당자 사용자 ID */}

                    <div className="form__field">

                        <label htmlFor="managerUserId">
                            담당자 사용자 ID
                        </label>

                        <input
                            id="managerUserId"
                            className="form__input"
                            type="number"
                            min="1"
                            step="1"
                            value={managerUserId}
                            onChange={(event) =>
                                setManagerUserId(event.target.value)
                            }
                            placeholder="담당자 사용자 ID"
                            disabled={submitting}
                        />

                        <small className="form__help">
                            담당자가 없으면 비워 두세요. 사용자 ID를 직접 입력합니다.
                        </small>

                    </div>


                    {/* 운영 시작일 */}

                    <div className="form__field">

                        <label htmlFor="effectiveFrom">
                            운영 시작일
                        </label>

                        <input
                            id="effectiveFrom"
                            className="form__input"
                            type="date"
                            value={effectiveFrom}
                            onChange={(event) =>
                                setEffectiveFrom(event.target.value)
                            }
                            disabled={submitting}
                        />

                    </div>


                    {/* 운영 종료일 */}

                    <div className="form__field">

                        <label htmlFor="effectiveTo">
                            운영 종료일
                        </label>

                        <input
                            id="effectiveTo"
                            className="form__input"
                            type="date"
                            value={effectiveTo}
                            onChange={(event) =>
                                setEffectiveTo(event.target.value)
                            }
                            min={effectiveFrom || undefined}
                            disabled={submitting}
                        />

                    </div>


                    {/* 설명 */}

                    <div className="form__field operation-form-description">

                        <label htmlFor="description">
                            운영 설명
                        </label>

                        <textarea
                            id="description"
                            className="form__textarea"
                            value={description}
                            onChange={(event) =>
                                setDescription(event.target.value)
                            }
                            placeholder="운영 서비스에 대한 설명을 입력하세요."
                            rows={5}
                            disabled={submitting}
                        />

                    </div>

                </div>


                {/* 오류 메시지 */}

                {error && (
                    <div className="operation-form-error">
                        {error}
                    </div>
                )}


                {/* 버튼 */}

                <div className="form__actions">

                    <button
                        type="button"
                        className="button button--secondary"
                        onClick={onCancel}
                        disabled={submitting}
                    >
                        취소
                    </button>

                    <button
                        type="submit"
                        className="button button--primary"
                        disabled={submitting}
                    >
                        {submitting
                            ? "저장 중..."
                            : mode === "create"
                                ? "운영 서비스 등록"
                                : "변경사항 저장"}
                    </button>

                </div>

            </form>

        </div>
    );
}


export default OperationForm;
