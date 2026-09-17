import { useEffect, useState } from "react";

import type {
    RiskResponse,
    RiskStatus,
} from "@/features/risk/types/risk";

import {
    RISK_STATUS_LABEL,
} from "@/features/risk/types/risk";

import "@/features/risk/styles/Risk.css";


interface RiskStatusFormProps {
    /**
     * 상태 변경 대상 Risk
     */
    risk: RiskResponse;

    /**
     * Risk 상태 변경
     *
     * 실제 API 호출은 부모 컴포넌트에서 수행한다.
     *
     * onSubmit()
     *   ↓
     * changeRiskStatus()
     *   ↓
     * PATCH /api/risks/{id}/status
     */
    onSubmit: (
        id: number,
        status: RiskStatus,
    ) => Promise<void>;

    /**
     * Form 취소
     */
    onCancel: () => void;
}


/**
 * Risk 상태 변경 옵션
 */
const statusOptions: RiskStatus[] = [
    "OPEN",
    "IN_PROGRESS",
    "MITIGATED",
    "CLOSED",
    "ACCEPTED",
    "CANCELLED",
];


function RiskStatusForm({
    risk,
    onSubmit,
    onCancel,
}: RiskStatusFormProps) {

    /**
     * 현재 선택된 상태
     */
    const [status, setStatus] =
        useState<RiskStatus>(
            risk.status,
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
     * Risk가 변경되면
     * 현재 상태를 Form에 다시 반영한다.
     */
    useEffect(() => {

        setStatus(
            risk.status,
        );

        setError(null);

    }, [risk]);


    /**
     * 상태 Badge CSS 클래스
     */
    const currentStatusClass =
        `badge--${risk.status
            .toLowerCase()
            .replace("_", "-")}`;


    /**
     * Risk 상태 변경 Submit
     */
    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
    ) => {

        event.preventDefault();


        /**
         * 현재 상태와 동일한 상태인지 확인
         */
        if (
            status === risk.status
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
                risk.id,
                status,
            );

        } catch (err) {

            console.error(
                "Failed to change Risk status:",
                err,
            );


            setError(
                "Risk 상태 변경에 실패했습니다.",
            );

        } finally {

            setSubmitting(false);
        }
    };


    return (
        <div className="risk-status-form">

            {/* Header */}
            <div className="risk-status-form-header">

                <h2 className="risk-status-form-title">
                    Risk 상태 변경
                </h2>


                <p className="risk-status-form-description">
                    {risk.riskKey}{" "}
                    {risk.title}
                </p>

            </div>


            <form
                id="risk-status-form"
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
                            RISK_STATUS_LABEL[
                                risk.status
                            ]
                        }
                    </span>

                </div>


                {/* New Status */}
                <div className="form__field">

                    <label
                        htmlFor="risk-new-status"
                        className="form__label"
                    >
                        변경 상태
                    </label>


                    <select
                        id="risk-new-status"
                        value={status}
                        onChange={(event) => {

                            setStatus(
                                event.target.value as RiskStatus,
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
                                        RISK_STATUS_LABEL[
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
                    <div className="state state--error risk-status-form-error">

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


export default RiskStatusForm;