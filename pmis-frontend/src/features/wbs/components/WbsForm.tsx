import { useState } from "react";

import type {
    WbsCreateRequest,
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


interface WbsFormProps {
    projectId: number;
    parentWbs: WbsTreeResponse | null;
    onSubmit: (
        request: WbsCreateRequest,
    ) => Promise<void>;
    onCancel: () => void;
}


function WbsForm({
    projectId,
    parentWbs,
    onSubmit,
    onCancel,
}: WbsFormProps) {
    const [wbsCode, setWbsCode] =
        useState("");

    const [wbsName, setWbsName] =
        useState("");

    const [description, setDescription] =
        useState("");

    const [status, setStatus] =
        useState<WbsStatus>("PLANNED");

    const [sortOrder, setSortOrder] =
        useState(1);

    const [submitting, setSubmitting] =
        useState(false);

    const [error, setError] =
        useState<string | null>(null);


    /**
     * 현재 생성 대상 Parent
     *
     * parentWbs가 있으면 해당 WBS의 하위로 생성한다.
     * parentWbs가 없으면 최상위 WBS로 생성한다.
     */
    const parentId =
        parentWbs?.id ?? null;


    /**
     * WBS 생성
     */
    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
    ) => {
        event.preventDefault();

        if (!wbsCode.trim()) {
            setError("WBS Code를 입력해주세요.");
            return;
        }

        if (!wbsName.trim()) {
            setError("WBS 명을 입력해주세요.");
            return;
        }

        if (sortOrder < 1) {
            setError("정렬 순서는 1 이상이어야 합니다.");
            return;
        }

        try {
            setSubmitting(true);
            setError(null);

            const request: WbsCreateRequest = {
                parentId,
                wbsCode: wbsCode.trim(),
                wbsName: wbsName.trim(),
                description:
                    description.trim() || null,
                status,
                sortOrder,
            };

            /**
             * 실제 API 호출은 부모 컴포넌트에서 수행한다.
             *
             * onSubmit()
             *   ↓
             * createWbs()
             *   ↓
             * POST /api/projects/{projectId}/wbs
             */
            await onSubmit(request);
        } catch (err) {
            console.error(
                "Failed to create WBS:",
                err,
            );

            setError(
                "WBS 생성에 실패했습니다.",
            );
        } finally {
            setSubmitting(false);
        }
    };


    return (
        <div
            style={{
                backgroundColor: "#ffffff",
                border: "1px solid #dddddd",
                borderRadius: "6px",
                padding: "20px",
            }}
        >
            {/* Header */}
            <div
                style={{
                    marginBottom: "20px",
                }}
            >
                <h2
                    style={{
                        margin: 0,
                        fontSize: "20px",
                    }}
                >
                    WBS 생성
                </h2>

                <p
                    style={{
                        marginTop: "8px",
                        marginBottom: 0,
                        color: "#666666",
                        fontSize: "14px",
                    }}
                >
                    {parentWbs
                        ? `${parentWbs.wbsCode} ${parentWbs.wbsName} 하위 WBS`
                        : "최상위 WBS"}
                </p>
            </div>


            {/* Error */}
            {error && (
                <div
                    style={{
                        marginBottom: "16px",
                        padding: "12px",
                        backgroundColor: "#fff3f3",
                        border: "1px solid #f0b8b8",
                        borderRadius: "4px",
                        color: "#c62828",
                        fontSize: "14px",
                    }}
                >
                    {error}
                </div>
            )}


            <form onSubmit={handleSubmit}>
                {/* Project ID */}
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <label
                        style={{
                            display: "block",
                            marginBottom: "6px",
                            fontWeight: "bold",
                        }}
                    >
                        Project ID
                    </label>

                    <input
                        type="text"
                        value={projectId}
                        disabled
                        style={{
                            width: "100%",
                            padding: "10px",
                            boxSizing: "border-box",
                            border: "1px solid #dddddd",
                            borderRadius: "4px",
                            backgroundColor: "#f5f5f5",
                            color: "#666666",
                        }}
                    />
                </div>


                {/* Parent WBS */}
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <label
                        style={{
                            display: "block",
                            marginBottom: "6px",
                            fontWeight: "bold",
                        }}
                    >
                        Parent WBS
                    </label>

                    <input
                        type="text"
                        value={
                            parentWbs
                                ? `${parentWbs.wbsCode} ${parentWbs.wbsName}`
                                : "최상위 WBS"
                        }
                        disabled
                        style={{
                            width: "100%",
                            padding: "10px",
                            boxSizing: "border-box",
                            border: "1px solid #dddddd",
                            borderRadius: "4px",
                            backgroundColor: "#f5f5f5",
                            color: "#666666",
                        }}
                    />
                </div>


                {/* WBS Code */}
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <label
                        style={{
                            display: "block",
                            marginBottom: "6px",
                            fontWeight: "bold",
                        }}
                    >
                        WBS Code
                    </label>

                    <input
                        type="text"
                        value={wbsCode}
                        onChange={(event) =>
                            setWbsCode(
                                event.target.value,
                            )
                        }
                        placeholder="예: 3.1.1"
                        disabled={submitting}
                        style={{
                            width: "100%",
                            padding: "10px",
                            boxSizing: "border-box",
                            border: "1px solid #cccccc",
                            borderRadius: "4px",
                        }}
                    />
                </div>


                {/* WBS Name */}
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <label
                        style={{
                            display: "block",
                            marginBottom: "6px",
                            fontWeight: "bold",
                        }}
                    >
                        WBS 명
                    </label>

                    <input
                        type="text"
                        value={wbsName}
                        onChange={(event) =>
                            setWbsName(
                                event.target.value,
                            )
                        }
                        placeholder="WBS 이름을 입력하세요."
                        disabled={submitting}
                        style={{
                            width: "100%",
                            padding: "10px",
                            boxSizing: "border-box",
                            border: "1px solid #cccccc",
                            borderRadius: "4px",
                        }}
                    />
                </div>


                {/* Description */}
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <label
                        style={{
                            display: "block",
                            marginBottom: "6px",
                            fontWeight: "bold",
                        }}
                    >
                        설명
                    </label>

                    <textarea
                        value={description}
                        onChange={(event) =>
                            setDescription(
                                event.target.value,
                            )
                        }
                        placeholder="WBS 설명을 입력하세요."
                        disabled={submitting}
                        rows={4}
                        style={{
                            width: "100%",
                            padding: "10px",
                            boxSizing: "border-box",
                            border: "1px solid #cccccc",
                            borderRadius: "4px",
                            resize: "vertical",
                        }}
                    />
                </div>


                {/* Status */}
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <label
                        style={{
                            display: "block",
                            marginBottom: "6px",
                            fontWeight: "bold",
                        }}
                    >
                        상태
                    </label>

                    <select
                        value={status}
                        onChange={(event) =>
                            setStatus(
                                event.target.value as WbsStatus,
                            )
                        }
                        disabled={submitting}
                        style={{
                            width: "100%",
                            padding: "10px",
                            boxSizing: "border-box",
                            border: "1px solid #cccccc",
                            borderRadius: "4px",
                            backgroundColor: "#ffffff",
                        }}
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


                {/* Sort Order */}
                <div
                    style={{
                        marginBottom: "20px",
                    }}
                >
                    <label
                        style={{
                            display: "block",
                            marginBottom: "6px",
                            fontWeight: "bold",
                        }}
                    >
                        정렬 순서
                    </label>

                    <input
                        type="number"
                        min={1}
                        value={sortOrder}
                        onChange={(event) =>
                            setSortOrder(
                                Number(event.target.value),
                            )
                        }
                        disabled={submitting}
                        style={{
                            width: "100%",
                            padding: "10px",
                            boxSizing: "border-box",
                            border: "1px solid #cccccc",
                            borderRadius: "4px",
                        }}
                    />
                </div>


                {/* Buttons */}
                <div
                    style={{
                        display: "flex",
                        justifyContent: "flex-end",
                        gap: "8px",
                    }}
                >
                    <button
                        type="button"
                        onClick={onCancel}
                        disabled={submitting}
                        style={{
                            padding: "10px 16px",
                            border: "1px solid #cccccc",
                            borderRadius: "4px",
                            backgroundColor: "#ffffff",
                            cursor: submitting
                                ? "default"
                                : "pointer",
                        }}
                    >
                        취소
                    </button>

                    <button
                        type="submit"
                        disabled={submitting}
                        style={{
                            padding: "10px 16px",
                            border: "none",
                            borderRadius: "4px",
                            backgroundColor: "#1976d2",
                            color: "#ffffff",
                            cursor: submitting
                                ? "default"
                                : "pointer",
                            fontWeight: "bold",
                        }}
                    >
                        {submitting
                            ? "생성 중..."
                            : "생성"}
                    </button>
                </div>
            </form>
        </div>
    );
}


export default WbsForm;