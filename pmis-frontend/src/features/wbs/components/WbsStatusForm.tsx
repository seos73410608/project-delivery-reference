import { useEffect, useState } from "react";

import type {
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";

import { WBS_STATUS_LABEL } from "@/features/wbs/types/wbs";

import "@/styles/badge.css";
import "@/features/wbs/styles/wbs.css";


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


const statusOptions: WbsStatus[] = [
    "PLANNED",
    "IN_PROGRESS",
    "COMPLETED",
    "ON_HOLD",
    "CANCELLED",
];


function WbsStatusForm({
    wbs,
    onSubmit,
    onCancel,
}: WbsStatusFormProps) {

    /**
     * 현재 선택된 상태
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
     * 상태 Badge CSS 클래스
     */
    const currentStatusClass =
        `badge--${wbs.status
            .toLowerCase()
            .replace("_", "-")}`;


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
                onSubmit={handleSubmit}
            >

                {/* Current Status */}
                <div className="form__field">

                    <span className="form__label">
                        현재 상태
                    </span>


                    <span
                        className={[
                            "badge",
                            currentStatusClass,
                        ].join(" ")}
                    >
                        {
                            WBS_STATUS_LABEL[
                                wbs.status
                            ]
                        }
                    </span>

                </div>


                {/* New Status */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-new-status"
                        className="form__label"
                    >
                        변경 상태
                    </label>


                    <select
                        id="wbs-new-status"
                        value={status}
                        onChange={(event) => {

                            setStatus(
                                event.target.value as WbsStatus,
                            );

                            setError(null);
                        }}
                        disabled={submitting}
                        className="form__select"
                    >

                        {statusOptions.map(
                            (option) => (
                                <option
                                    key={option}
                                    value={option}
                                >
                                    {
                                        WBS_STATUS_LABEL[
                                            option
                                        ]
                                    }
                                </option>
                            ),
                        )}

                    </select>

                </div>


                {/* Error */}
                {error && (
                    <div className="state state--error wbs-status-form-error">

                        <div className="state__icon">
                            !
                        </div>

                        <p className="state__description">
                            {error}
                        </p>

                    </div>
                )}


                {/* Buttons */}
                <div className="form__actions">

                    {/* Cancel */}
                    <button
                        type="button"
                        onClick={onCancel}
                        disabled={submitting}
                        className="button button--secondary"
                    >
                        취소
                    </button>


                    {/* Submit */}
                    <button
                        type="submit"
                        disabled={submitting}
                        className="button button--primary"
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


export default WbsStatusForm;