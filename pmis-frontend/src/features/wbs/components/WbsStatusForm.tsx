import { useEffect, useState } from "react";

import type {
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


interface WbsStatusFormProps {
    /**
     * 상태 변경 대상 WBS
     */
    wbs: WbsTreeResponse;

    /**
     * WBS 상태 변경
     *
     * 실제 API 호출은 부모 컴포넌트에서 수행한다.
     *
     * onSubmit()
     *   ↓
     * updateWbs()
     *   ↓
     * PUT /api/wbs/{id}
     */
    onSubmit: (
        id: number,
        status: WbsStatus,
    ) => Promise<void>;

    /**
     * Form 취소
     */
    onCancel: () => void;
}


function WbsStatusForm({
    wbs,
    onSubmit,
    onCancel,
}: WbsStatusFormProps) {

    /**
     * 현재 선택된 상태
     *
     * 기본값:
     * 현재 WBS의 상태
     */
    const [status, setStatus] =
        useState<WbsStatus>(
            wbs.status,
        );


    /**
     * API 처리 상태
     */
    const [submitting, setSubmitting] =
        useState(false);


    /**
     * 오류 메시지
     */
    const [error, setError] =
        useState<string | null>(null);


    /**
     * WBS가 변경되면
     * 현재 상태를 Form에 다시 반영한다.
     */
    useEffect(() => {

        setStatus(
            wbs.status,
        );

        setError(null);

    }, [wbs]);


    /**
     * WBS 상태 변경 Submit
     */
    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
    ) => {

        event.preventDefault();


        /**
         * 현재 상태와 동일한 상태인지 확인
         */
        if (
            status === wbs.status
        ) {

            setError(
                "현재 상태와 동일한 상태입니다.",
            );

            return;
        }


        try {

            setSubmitting(true);
            setError(null);


            /**
             * 실제 API 호출은
             * 부모 컴포넌트에서 수행한다.
             *
             * onSubmit()
             *   ↓
             * updateWbs()
             *   ↓
             * PUT /api/wbs/{id}
             */
            await onSubmit(
                wbs.id,
                status,
            );

        } catch (err) {

            console.error(
                "Failed to change WBS status:",
                err,
            );


            setError(
                "WBS 상태 변경에 실패했습니다.",
            );

        } finally {

            setSubmitting(false);
        }
    };


    return (
        <div className="wbs-status-form">

            {/* Header */}
            <div className="wbs-status-form-header">

                <h2 className="wbs-status-form-title">
                    WBS 상태 변경
                </h2>


                <p className="wbs-status-form-description">
                    {wbs.wbsCode}{" "}
                    {wbs.wbsName}
                </p>

            </div>


            <form
                id="wbs-status-form"
                onSubmit={
                    handleSubmit
                }
            >

                {/* Current Status */}
                <div className="wbs-status-form-field">

                    <label className="wbs-status-form-label">
                        현재 상태
                    </label>


                    <input
                        type="text"
                        value={
                            getStatusLabel(
                                wbs.status,
                            )
                        }
                        disabled
                        className="wbs-status-form-input"
                    />

                </div>


                {/* New Status */}
                <div className="wbs-status-form-field">

                    <label className="wbs-status-form-label">
                        변경 상태
                    </label>


                    <select
                        value={
                            status
                        }
                        onChange={(
                            event,
                        ) => {

                            setStatus(
                                event.target
                                    .value as WbsStatus,
                            );

                            setError(null);
                        }}
                        disabled={
                            submitting
                        }
                        className="wbs-status-form-select"
                    >

                        <option value="PLANNED">
                            계획
                        </option>

                        <option value="IN_PROGRESS">
                            진행 중
                        </option>

                        <option value="COMPLETED">
                            완료
                        </option>

                        <option value="ON_HOLD">
                            보류
                        </option>

                        <option value="CANCELLED">
                            취소
                        </option>

                    </select>

                </div>


                {/* Information */}
                <div className="wbs-status-form-info">
                    WBS 상태는 프로젝트 상황에 따라
                    자유롭게 변경할 수 있습니다.
                    <br />
                    필요할 경우 이전 상태로 다시
                    변경할 수 있습니다.
                </div>


                {/* Error */}
                {error && (
                    <div className="wbs-status-form-error">
                        {error}
                    </div>
                )}


                {/* Buttons */}
                <div className="wbs-status-form-buttons">

                    {/* Cancel */}
                    <button
                        type="button"
                        onClick={
                            onCancel
                        }
                        disabled={
                            submitting
                        }
                        className="wbs-status-form-button cancel"
                    >
                        취소
                    </button>


                    {/* Submit */}
                    <button
                        type="submit"
                        disabled={
                            submitting
                        }
                        className="wbs-status-form-button submit"
                    >
                        {submitting
                            ? "변경 중..."
                            : "상태 변경"}
                    </button>

                </div>

            </form>

        </div>
    );
}


/**
 * WBS Status → 화면 표시명
 */
function getStatusLabel(
    status: WbsStatus,
): string {

    switch (status) {

        case "PLANNED":
            return "계획";

        case "IN_PROGRESS":
            return "진행 중";

        case "COMPLETED":
            return "완료";

        case "ON_HOLD":
            return "보류";

        case "CANCELLED":
            return "취소";

        default:
            return status;
    }
}


export default WbsStatusForm;