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
                style={{
                    backgroundColor:
                        "#ffffff",

                    border:
                        "1px solid #dddddd",

                    borderRadius:
                        "6px",

                    minHeight:
                        "300px",

                    display:
                        "flex",

                    alignItems:
                        "center",

                    justifyContent:
                        "center",

                    color:
                        "#999999",

                    padding:
                        "24px",

                    boxSizing:
                        "border-box",
                }}
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
            style={{
                backgroundColor:
                    "#ffffff",

                border:
                    "1px solid #dddddd",

                borderRadius:
                    "6px",

                overflow:
                    "hidden",
            }}
        >

            {/* Header */}
            <div
                style={{
                    display:
                        "flex",

                    alignItems:
                        "center",

                    justifyContent:
                        "space-between",

                    padding:
                        "16px",

                    borderBottom:
                        "1px solid #eeeeee",
                }}
            >

                <div>

                    <div
                        style={{
                            fontSize:
                                "12px",

                            color:
                                "#777777",

                            marginBottom:
                                "4px",
                        }}
                    >
                        WBS Detail
                    </div>


                    <h2
                        style={{
                            margin:
                                0,

                            fontSize:
                                "20px",

                            color:
                                "#333333",
                        }}
                    >
                        {
                            wbs.wbsName
                        }
                    </h2>

                </div>


                {/* Status */}
                <span
                    style={{
                        padding:
                            "6px 10px",

                        borderRadius:
                            "12px",

                        backgroundColor:
                            `${statusColor}15`,

                        color:
                            statusColor,

                        fontSize:
                            "12px",

                        fontWeight:
                            "bold",

                        whiteSpace:
                            "nowrap",
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
                style={{
                    padding:
                        "20px",
                }}
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
                    style={{
                        marginTop:
                            "20px",
                    }}
                >

                    <div
                        style={{
                            fontSize:
                                "13px",

                            fontWeight:
                                "bold",

                            color:
                                "#555555",

                            marginBottom:
                                "8px",
                        }}
                    >
                        Description
                    </div>


                    <div
                        style={{
                            minHeight:
                                "80px",

                            padding:
                                "12px",

                            backgroundColor:
                                "#f9f9f9",

                            border:
                                "1px solid #eeeeee",

                            borderRadius:
                                "4px",

                            fontSize:
                                "14px",

                            lineHeight:
                                1.6,

                            color:
                                "#444444",

                            boxSizing:
                                "border-box",

                            whiteSpace:
                                "pre-wrap",

                            wordBreak:
                                "break-word",
                        }}
                    >
                        {
                            wbs.description ||
                            "-"
                        }
                    </div>

                </div>


                {/* Metadata */}
                <div
                    style={{
                        marginTop:
                            "20px",

                        paddingTop:
                            "16px",

                        borderTop:
                            "1px solid #eeeeee",
                    }}
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
                    style={{
                        display:
                            "flex",

                        gap:
                            "8px",

                        marginTop:
                            "24px",
                    }}
                >

                    {/* Edit */}
                    <button
                        type="button"
                        onClick={() =>
                            onEdit?.(
                                wbs,
                            )
                        }
                        style={{
                            flex:
                                1,

                            padding:
                                "10px 12px",

                            border:
                                "1px solid #1976d2",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#ffffff",

                            color:
                                "#1976d2",

                            fontWeight:
                                "bold",

                            cursor:
                                "pointer",
                        }}
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
                        style={{
                            flex:
                                1,

                            padding:
                                "10px 12px",

                            border:
                                "1px solid #757575",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#ffffff",

                            color:
                                "#555555",

                            fontWeight:
                                "bold",

                            cursor:
                                "pointer",
                        }}
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
                        style={{
                            flex:
                                1,

                            padding:
                                "10px 12px",

                            border:
                                "1px solid #d32f2f",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#ffffff",

                            color:
                                "#d32f2f",

                            fontWeight:
                                "bold",

                            cursor:
                                "pointer",
                        }}
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
            style={{
                display:
                    "grid",

                gridTemplateColumns:
                    "120px 1fr",

                gap:
                    "12px",

                padding:
                    "8px 0",

                borderBottom:
                    "1px solid #f0f0f0",
            }}
        >

            <span
                style={{
                    fontSize:
                        "13px",

                    color:
                        "#777777",

                    fontWeight:
                        "bold",
                }}
            >
                {
                    label
                }
            </span>


            <span
                style={{
                    fontSize:
                        "14px",

                    color:
                        "#333333",

                    wordBreak:
                        "break-word",
                }}
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