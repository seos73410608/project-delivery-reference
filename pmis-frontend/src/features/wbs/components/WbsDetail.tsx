import type { WbsTreeResponse } from "@/features/wbs/types/wbs";
import { WBS_STATUS_LABEL } from "@/features/wbs/types/wbs";


interface WbsDetailProps {
    wbs: WbsTreeResponse | null;
    onEdit?: (wbs: WbsTreeResponse) => void;
    onChangeStatus?: (wbs: WbsTreeResponse) => void;
    onDelete?: (wbs: WbsTreeResponse) => void;
}


function WbsDetail({
    wbs,
    onEdit,
    onChangeStatus,
    onDelete,
}: WbsDetailProps) {
    if (!wbs) {
        return (
            <section
                style={{
                    backgroundColor: "#ffffff",
                    border: "1px solid #dddddd",
                    borderRadius: "6px",
                    minHeight: "300px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    color: "#999999",
                    padding: "24px",
                    boxSizing: "border-box",
                }}
            >
                Select a WBS item to view details.
            </section>
        );
    }

    return (
        <section
            style={{
                backgroundColor: "#ffffff",
                border: "1px solid #dddddd",
                borderRadius: "6px",
                overflow: "hidden",
            }}
        >
            {/* Header */}
            <div
                style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                    padding: "16px",
                    borderBottom: "1px solid #eeeeee",
                }}
            >
                <div>
                    <div
                        style={{
                            fontSize: "12px",
                            color: "#777777",
                            marginBottom: "4px",
                        }}
                    >
                        WBS Detail
                    </div>

                    <h2
                        style={{
                            margin: 0,
                            fontSize: "20px",
                            color: "#333333",
                        }}
                    >
                        {wbs.wbsName}
                    </h2>
                </div>

                {/* Status */}
                <span
                    style={{
                        padding: "6px 10px",
                        borderRadius: "12px",
                        backgroundColor: "#f5f5f5",
                        color: "#555555",
                        fontSize: "12px",
                        fontWeight: "bold",
                        whiteSpace: "nowrap",
                    }}
                >
                    {WBS_STATUS_LABEL[wbs.status]}
                </span>
            </div>

            {/* Detail */}
            <div
                style={{
                    padding: "20px",
                }}
            >
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
                    value={WBS_STATUS_LABEL[wbs.status]}
                />

                {/* Description */}
                <div
                    style={{
                        marginTop: "20px",
                    }}
                >
                    <div
                        style={{
                            fontSize: "13px",
                            fontWeight: "bold",
                            color: "#555555",
                            marginBottom: "8px",
                        }}
                    >
                        Description
                    </div>

                    <div
                        style={{
                            minHeight: "80px",
                            padding: "12px",
                            backgroundColor: "#f9f9f9",
                            border: "1px solid #eeeeee",
                            borderRadius: "4px",
                            fontSize: "14px",
                            lineHeight: 1.6,
                            color: "#444444",
                            boxSizing: "border-box",
                        }}
                    >
                        {wbs.description || "-"}
                    </div>
                </div>

                {/* Metadata */}
                <div
                    style={{
                        marginTop: "20px",
                        paddingTop: "16px",
                        borderTop: "1px solid #eeeeee",
                    }}
                >
                    <DetailRow
                        label="Created At"
                        value={formatDate(wbs.createdAt)}
                    />

                    <DetailRow
                        label="Updated At"
                        value={formatDate(wbs.updatedAt)}
                    />
                </div>

                {/* Actions */}
                <div
                    style={{
                        display: "flex",
                        gap: "8px",
                        marginTop: "24px",
                    }}
                >
                    <button
                        type="button"
                        onClick={() => onEdit?.(wbs)}
                        style={{
                            flex: 1,
                            padding: "10px 12px",
                            border: "1px solid #1976d2",
                            borderRadius: "4px",
                            backgroundColor: "#ffffff",
                            color: "#1976d2",
                            fontWeight: "bold",
                            cursor: "pointer",
                        }}
                    >
                        Edit
                    </button>

                    <button
                        type="button"
                        onClick={() => onChangeStatus?.(wbs)}
                        style={{
                            flex: 1,
                            padding: "10px 12px",
                            border: "1px solid #757575",
                            borderRadius: "4px",
                            backgroundColor: "#ffffff",
                            color: "#555555",
                            fontWeight: "bold",
                            cursor: "pointer",
                        }}
                    >
                        Change Status
                    </button>

                    <button
                        type="button"
                        onClick={() => onDelete?.(wbs)}
                        style={{
                            flex: 1,
                            padding: "10px 12px",
                            border: "1px solid #d32f2f",
                            borderRadius: "4px",
                            backgroundColor: "#ffffff",
                            color: "#d32f2f",
                            fontWeight: "bold",
                            cursor: "pointer",
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
                display: "grid",
                gridTemplateColumns: "120px 1fr",
                gap: "12px",
                padding: "8px 0",
                borderBottom: "1px solid #f0f0f0",
            }}
        >
            <span
                style={{
                    fontSize: "13px",
                    color: "#777777",
                    fontWeight: "bold",
                }}
            >
                {label}
            </span>

            <span
                style={{
                    fontSize: "14px",
                    color: "#333333",
                    wordBreak: "break-word",
                }}
            >
                {value}
            </span>
        </div>
    );
}


function formatDate(value: string): string {
    if (!value) {
        return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString("ko-KR");
}


export default WbsDetail;