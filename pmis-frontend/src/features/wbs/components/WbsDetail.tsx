
import type {
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";

import {
    WBS_STATUS_LABEL,
} from "@/features/wbs/types/wbs";

import "@/features/wbs/styles/wbs.css";


interface WbsDetailProps {
    /**
     * 현재 선택된 WBS
     *
     * null:
     * WBS가 선택되지 않은 상태
     */
    wbs: WbsTreeResponse | null;

    /**
     * WBS 수정
     */
    onEdit?: (
        wbs: WbsTreeResponse,
    ) => void;

    /**
     * WBS 상태 변경
     */
    onChangeStatus?: (
        wbs: WbsTreeResponse,
    ) => void;

    /**
     * WBS 삭제
     */
    onDelete?: (
        wbs: WbsTreeResponse,
    ) => void;
}


/**
 * 계획 공수 표시
 */
function formatManDays(
    value: number,
): string {
    return `${value.toFixed(2)} M/D`;
}


function WbsDetail({
    wbs,
    onEdit,
    onChangeStatus,
    onDelete,
}: WbsDetailProps) {

    /**
     * WBS가 선택되지 않은 경우
     */
    if (!wbs) {

        return (
            <section
                className="wbs-detail-empty"
            >
                Select a WBS item to view details.
            </section>
        );
    }


    /**
     * WBS Status에 대응하는
     * 공통 Badge modifier
     */
    const statusClass =
        `badge--${wbs.status
            .toLowerCase()
            .replace("_", "-")}`;


    /**
     * 진척률 표시
     *
     * null:
     *     산정 불가
     *
     * number:
     *     소수점 첫째 자리까지 표시
     */
    const progressLabel =
        wbs.progressRate === null
            ? "산정 불가"
            : `${wbs.progressRate.toFixed(1)}%`;


    return (
        <section
            className="wbs-detail"
        >

            {/* Header */}
            <div
                className="wbs-detail-header"
            >

                <div>

                    <div
                        className="wbs-detail-label"
                    >
                        WBS Detail
                    </div>


                    <h2
                        className="wbs-detail-title"
                    >
                        {wbs.wbsName}
                    </h2>

                </div>


                {/* Status */}
                <span
                    className={[
                        "badge",
                        statusClass,
                        "wbs-detail-status",
                    ].join(" ")}
                >
                    {
                        WBS_STATUS_LABEL[
                            wbs.status
                        ]
                    }
                </span>

            </div>


            {/* Detail */}
            <div
                className="wbs-detail-content"
            >

                {/* Progress */}
                <section
                    className="wbs-detail-progress"
                >

                    <div
                        className="wbs-detail-progress-header"
                    >

                        <span>
                            WBS Progress
                        </span>

                        <strong
                            className={
                                wbs.progressRate === null
                                    ? "unavailable"
                                    : ""
                            }
                        >
                            {progressLabel}
                        </strong>

                    </div>


                    {/* Progress Bar */}
                    {wbs.progressRate !== null && (
                        <div
                            className="wbs-detail-progress-bar"
                            role="progressbar"
                            aria-label="WBS 진척률"
                            aria-valuemin={0}
                            aria-valuemax={100}
                            aria-valuenow={wbs.progressRate}
                        >

                            <div
                                className="wbs-detail-progress-bar-fill"
                                style={{
                                    width: `${Math.min(
                                        100,
                                        Math.max(
                                            0,
                                            wbs.progressRate,
                                        ),
                                    )}%`,
                                }}
                            />

                        </div>
                    )}


                    {/* Aggregation Metrics */}
                    <div
                        className="wbs-detail-progress-metrics"
                    >

                        <div
                            className="wbs-detail-progress-metric"
                        >

                            <span>
                                총 계획 공수
                            </span>

                            <strong>
                                {
                                    formatManDays(
                                        wbs.totalPlannedManDays,
                                    )
                                }
                            </strong>

                        </div>


                        <div
                            className="wbs-detail-progress-metric"
                        >

                            <span>
                                가중 완료 공수
                            </span>

                            <strong>
                                {
                                    formatManDays(
                                        wbs.weightedCompletedManDays,
                                    )
                                }
                            </strong>

                        </div>


                        <div
                            className="wbs-detail-progress-metric"
                        >

                            <span>
                                계획 공수 미입력 Task
                            </span>

                            <strong>
                                {wbs.missingEffortTaskCount}건
                            </strong>

                        </div>

                    </div>

                </section>


                {/* WBS ID */}
                <DetailRow
                    label="WBS ID"
                    value={String(wbs.id)}
                />


                {/* Project ID */}
                <DetailRow
                    label="Project ID"
                    value={String(wbs.projectId)}
                />


                {/* WBS Code */}
                <DetailRow
                    label="WBS Code"
                    value={wbs.wbsCode}
                />


                {/* WBS Name */}
                <DetailRow
                    label="WBS Name"
                    value={wbs.wbsName}
                />


                {/* Level */}
                <DetailRow
                    label="Level"
                    value={String(wbs.level)}
                />


                {/* Parent */}
                <DetailRow
                    label="Parent ID"
                    value={
                        wbs.parentId === null
                            ? "-"
                            : String(wbs.parentId)
                    }
                />


                {/* Sort Order */}
                <DetailRow
                    label="Sort Order"
                    value={String(wbs.sortOrder)}
                />


                {/* Status */}
                <DetailRow
                    label="Status"
                    value={
                        WBS_STATUS_LABEL[
                            wbs.status
                        ]
                    }
                />


                {/* Description */}
                <div
                    className="wbs-detail-description-section"
                >

                    <div
                        className="wbs-detail-description-label"
                    >
                        Description
                    </div>


                    <div
                        className="wbs-detail-description"
                    >
                        {
                            wbs.description ||
                            "-"
                        }
                    </div>

                </div>


                {/* Metadata */}
                <div
                    className="wbs-detail-metadata"
                >

                    <DetailRow
                        label="Created At"
                        value={
                            formatDate(
                                wbs.createdAt,
                            )
                        }
                    />


                    <DetailRow
                        label="Updated At"
                        value={
                            formatDate(
                                wbs.updatedAt,
                            )
                        }
                    />

                </div>


                {/* Actions */}
                <div
                    className="form__actions wbs-detail-actions"
                >

                    {/* Edit */}
                    <button
                        type="button"
                        onClick={() =>
                            onEdit?.(
                                wbs,
                            )
                        }
                        className="button button--primary"
                    >
                        Edit
                    </button>


                    {/* Change Status */}
                    <button
                        type="button"
                        onClick={() =>
                            onChangeStatus?.(
                                wbs,
                            )
                        }
                        className="button button--secondary"
                    >
                        Change Status
                    </button>


                    {/* Delete */}
                    <button
                        type="button"
                        onClick={() =>
                            onDelete?.(
                                wbs,
                            )
                        }
                        className="button button--danger"
                    >
                        Delete
                    </button>

                </div>

            </div>

        </section>
    );
}


interface DetailRowProps {
    label: string;
    value: string;
}


function DetailRow({
    label,
    value,
}: DetailRowProps) {

    return (
        <div
            className="wbs-detail-row"
        >

            <span
                className="wbs-detail-row-label"
            >
                {label}
            </span>


            <span
                className="wbs-detail-row-value"
            >
                {value}
            </span>

        </div>
    );
}


/**
 * Backend LocalDateTime 문자열을
 * 한국어 날짜/시간 형식으로 표시한다.
 */
function formatDate(
    value: string,
): string {

    if (!value) {
        return "-";
    }


    const date =
        new Date(value);


    if (
        Number.isNaN(
            date.getTime(),
        )
    ) {
        return value;
    }


    return date.toLocaleString(
        "ko-KR",
    );
}


export default WbsDetail;
