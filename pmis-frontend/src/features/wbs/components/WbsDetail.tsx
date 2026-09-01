import type { WbsTreeResponse } from "@/features/wbs/types/wbs";
import {
    WBS_STATUS_LABEL,
    WBS_STATUS_COLOR,
} from "@/features/wbs/types/wbs";


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
     *
     * 실제 수정 처리는
     * 부모 컴포넌트(WbsPage)에서 수행한다.
     */
    onEdit?: (
        wbs: WbsTreeResponse,
    ) => void;

    /**
     * WBS 상태 변경
     *
     * 실제 상태 변경 처리는
     * 부모 컴포넌트(WbsPage)에서 수행한다.
     *
     * 현재는 WbsStatusForm을 표시하도록
     * WbsPage의 handleChangeStatus()가 처리한다.
     */
    onChangeStatus?: (
        wbs: WbsTreeResponse,
    ) => void;

    /**
     * WBS 삭제
     *
     * 실제 삭제 처리는
     * 부모 컴포넌트(WbsPage)에서 수행한다.
     *
     * 현재는 Backend API 연동 전이다.
     */
    onDelete?: (
        wbs: WbsTreeResponse,
    ) => void;
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
     * 현재 WBS 상태 색상
     */
    const statusColor =
        WBS_STATUS_COLOR[
            wbs.status
        ];


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
                        {
                            wbs.wbsName
                        }
                    </h2>

                </div>


                {/* Status */}
                <span
                    className="wbs-detail-status"
                    style={{
                        backgroundColor:
                            `${statusColor}15`,

                        color:
                            statusColor,
                    }}
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

                {/* WBS ID */}
                <DetailRow
                    label="WBS ID"
                    value={
                        String(
                            wbs.id,
                        )
                    }
                />


                {/* Project ID */}
                <DetailRow
                    label="Project ID"
                    value={
                        String(
                            wbs.projectId,
                        )
                    }
                />


                {/* WBS Code */}
                <DetailRow
                    label="WBS Code"
                    value={
                        wbs.wbsCode
                    }
                />


                {/* WBS Name */}
                <DetailRow
                    label="WBS Name"
                    value={
                        wbs.wbsName
                    }
                />


                {/* Level */}
                <DetailRow
                    label="Level"
                    value={
                        String(
                            wbs.level,
                        )
                    }
                />


                {/* Parent */}
                <DetailRow
                    label="Parent ID"
                    value={
                        wbs.parentId === null
                            ? "-"
                            : String(
                                  wbs.parentId,
                              )
                    }
                />


                {/* Sort Order */}
                <DetailRow
                    label="Sort Order"
                    value={
                        String(
                            wbs.sortOrder,
                        )
                    }
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
                    className="wbs-detail-actions"
                >

                    {/* Edit */}
                    <button
                        type="button"
                        onClick={() =>
                            onEdit?.(
                                wbs,
                            )
                        }
                        className="wbs-detail-button wbs-detail-button-edit"
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
                        className="wbs-detail-button wbs-detail-button-status"
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
                        className="wbs-detail-button wbs-detail-button-delete"
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
                {
                    label
                }
            </span>


            <span
                className="wbs-detail-row-value"
            >
                {
                    value
                }
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