import type { WbsStatus } from "@/features/wbs/types/wbs";
import { WBS_STATUS_LABEL } from "@/features/wbs/types/wbs";


interface WbsToolbarProps {
    keyword: string;
    status: WbsStatus | "";
    onKeywordChange: (keyword: string) => void;
    onStatusChange: (status: WbsStatus | "") => void;
    onCreate: () => void;
}


const statusOptions: WbsStatus[] = [
    "PLANNED",
    "IN_PROGRESS",
    "COMPLETED",
    "ON_HOLD",
    "CANCELLED",
];


function WbsToolbar({
    keyword,
    status,
    onKeywordChange,
    onStatusChange,
    onCreate,
}: WbsToolbarProps) {
    return (
        <section
            style={{
                backgroundColor: "#ffffff",
                border: "1px solid #dddddd",
                borderRadius: "6px",
                padding: "16px",
                marginBottom: "16px",
            }}
        >
            <div
                style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "12px",
                }}
            >
                {/* Search */}
                <input
                    type="text"
                    value={keyword}
                    onChange={(event) =>
                        onKeywordChange(event.target.value)
                    }
                    placeholder="Search WBS..."
                    style={{
                        flex: 1,
                        minWidth: "200px",
                        padding: "9px 12px",
                        border: "1px solid #cccccc",
                        borderRadius: "4px",
                        fontSize: "14px",
                        boxSizing: "border-box",
                    }}
                />

                {/* Status Filter */}
                <select
                    value={status}
                    onChange={(event) =>
                        onStatusChange(
                            event.target.value as WbsStatus | "",
                        )
                    }
                    style={{
                        minWidth: "150px",
                        padding: "9px 12px",
                        border: "1px solid #cccccc",
                        borderRadius: "4px",
                        backgroundColor: "#ffffff",
                        fontSize: "14px",
                        cursor: "pointer",
                    }}
                >
                    <option value="">All Status</option>

                    {statusOptions.map((option) => (
                        <option
                            key={option}
                            value={option}
                        >
                            {WBS_STATUS_LABEL[option]}
                        </option>
                    ))}
                </select>

                {/* Create Button */}
                <button
                    type="button"
                    onClick={onCreate}
                    style={{
                        padding: "9px 16px",
                        border: "none",
                        borderRadius: "4px",
                        backgroundColor: "#1976d2",
                        color: "#ffffff",
                        fontSize: "14px",
                        fontWeight: "bold",
                        cursor: "pointer",
                        whiteSpace: "nowrap",
                    }}
                >
                    + Create WBS
                </button>
            </div>
        </section>
    );
}


export default WbsToolbar;