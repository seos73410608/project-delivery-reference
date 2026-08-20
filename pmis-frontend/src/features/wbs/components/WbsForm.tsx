import { useEffect, useState } from "react";

import type {
    WbsCreateRequest,
    WbsStatus,
    WbsTreeResponse,
    WbsUpdateRequest,
} from "@/features/wbs/types/wbs";


interface WbsFormProps {
    /**
     * Form 모드
     *
     * create:
     * 신규 WBS 생성
     *
     * edit:
     * 기존 WBS 수정
     */
    mode: "create" | "edit";

    /**
     * 프로젝트 ID
     */
    projectId: number;

    /**
     * 현재 수정 대상 WBS
     *
     * create:
     * null
     *
     * edit:
     * 선택된 WBS
     */
    wbs?: WbsTreeResponse | null;

    /**
     * 현재 WBS의 Parent
     *
     * create:
     * 선택된 Parent WBS
     *
     * edit:
     * 수정 대상 WBS의 Parent
     */
    parentWbs: WbsTreeResponse | null;

    /**
     * WBS 생성
     */
    onCreate?: (
        request: WbsCreateRequest,
    ) => Promise<void>;

    /**
     * WBS 수정
     */
    onUpdate?: (
        id: number,
        request: WbsUpdateRequest,
    ) => Promise<void>;

    /**
     * Form 취소
     */
    onCancel: () => void;
}


function WbsForm({
    mode,
    projectId,
    wbs,
    parentWbs,
    onCreate,
    onUpdate,
    onCancel,
}: WbsFormProps) {

    /**
     * Form 초기값
     *
     * 생성:
     * 빈 값
     *
     * 수정:
     * 기존 WBS 값
     */
    const [wbsCode, setWbsCode] =
        useState(
            mode === "edit" && wbs
                ? wbs.wbsCode
                : "",
        );

    const [wbsName, setWbsName] =
        useState(
            mode === "edit" && wbs
                ? wbs.wbsName
                : "",
        );

    const [description, setDescription] =
        useState(
            mode === "edit" && wbs
                ? wbs.description ?? ""
                : "",
        );

    const [status, setStatus] =
        useState<WbsStatus>(
            mode === "edit" && wbs
                ? wbs.status
                : "PLANNED",
        );

    const [sortOrder, setSortOrder] =
        useState(
            mode === "edit" && wbs
                ? wbs.sortOrder
                : 1,
        );

    const [submitting, setSubmitting] =
        useState(false);

    const [error, setError] =
        useState<string | null>(null);


    /**
     * 수정 대상 WBS 또는 Form mode가
     * 변경되면 기존 값을 다시 반영한다.
     */
    useEffect(() => {

        if (mode === "edit" && wbs) {
            setWbsCode(wbs.wbsCode);
            setWbsName(wbs.wbsName);
            setDescription(
                wbs.description ?? "",
            );
            setStatus(wbs.status);
            setSortOrder(wbs.sortOrder);
            setError(null);

            return;
        }

        /**
         * Create mode
         */
        setWbsCode("");
        setWbsName("");
        setDescription("");
        setStatus("PLANNED");
        setSortOrder(1);
        setError(null);

    }, [mode, wbs]);


    /**
     * 현재 Form이 수정 모드인지 여부
     */
    const isEditMode =
        mode === "edit";


    /**
     * 현재 Form 제목
     */
    const formTitle =
        isEditMode
            ? "WBS 수정"
            : "WBS 생성";


    /**
     * 현재 Form 설명
     */
    const formDescription =
        isEditMode
            ? "기존 WBS 정보를 수정합니다."
            : parentWbs
              ? `${parentWbs.wbsCode} ${parentWbs.wbsName} 하위 WBS`
              : "최상위 WBS";


    /**
     * 현재 Parent ID
     *
     * Parent WBS가 있으면 해당 ID
     * 없으면 최상위 WBS이므로 null
     */
    const parentId =
        parentWbs?.id ?? null;


    /**
     * WBS Submit
     *
     * Create:
     * POST /api/projects/{projectId}/wbs
     *
     * Update:
     * PUT /api/wbs/{id}
     */
    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
    ) => {
        event.preventDefault();


        /**
         * 수정 모드인데
         * 수정 대상 WBS가 없는 경우
         */
        if (isEditMode && !wbs) {
            setError(
                "수정할 WBS가 선택되지 않았습니다.",
            );

            return;
        }


        /**
         * WBS Code validation
         */
        if (!wbsCode.trim()) {
            setError(
                "WBS Code를 입력해주세요.",
            );

            return;
        }


        /**
         * WBS Name validation
         */
        if (!wbsName.trim()) {
            setError(
                "WBS 명을 입력해주세요.",
            );

            return;
        }


        /**
         * Sort Order validation
         */
        if (
            !Number.isInteger(sortOrder) ||
            sortOrder < 1
        ) {
            setError(
                "정렬 순서는 1 이상의 정수여야 합니다.",
            );

            return;
        }


        try {
            setSubmitting(true);
            setError(null);


            /**
             * Create / Update 공통 Request
             */
            const request = {
                parentId,

                wbsCode:
                    wbsCode.trim(),

                wbsName:
                    wbsName.trim(),

                description:
                    description.trim() || null,

                status,

                sortOrder,
            };


            /**
             * Create
             *
             * POST /api/projects/{projectId}/wbs
             */
            if (!isEditMode) {

                if (!onCreate) {
                    throw new Error(
                        "WBS 생성 처리 함수가 없습니다.",
                    );
                }


                const createRequest:
                    WbsCreateRequest = {
                        ...request,
                    };


                await onCreate(
                    createRequest,
                );

                return;
            }


            /**
             * Update
             *
             * PUT /api/wbs/{id}
             */
            if (!onUpdate) {
                throw new Error(
                    "WBS 수정 처리 함수가 없습니다.",
                );
            }


            if (!wbs) {
                throw new Error(
                    "수정할 WBS가 없습니다.",
                );
            }


            const updateRequest:
                WbsUpdateRequest = {
                    ...request,
                };


            await onUpdate(
                wbs.id,
                updateRequest,
            );

        } catch (err) {
            console.error(
                isEditMode
                    ? "Failed to update WBS:"
                    : "Failed to create WBS:",
                err,
            );


            setError(
                isEditMode
                    ? "WBS 수정에 실패했습니다."
                    : "WBS 생성에 실패했습니다.",
            );

        } finally {
            setSubmitting(false);
        }
    };


    return (
        <div
            style={{
                backgroundColor:
                    "#ffffff",

                border:
                    "1px solid #dddddd",

                borderRadius:
                    "6px",

                padding:
                    "20px",
            }}
        >

            {/* Header */}
            <div
                style={{
                    marginBottom:
                        "20px",
                }}
            >

                <h2
                    style={{
                        margin: 0,
                        fontSize: "20px",
                    }}
                >
                    {formTitle}
                </h2>


                <p
                    style={{
                        marginTop:
                            "8px",

                        marginBottom:
                            0,

                        color:
                            "#666666",

                        fontSize:
                            "14px",
                    }}
                >
                    {formDescription}
                </p>

            </div>


            {/* Error */}
            {error && (
                <div
                    style={{
                        marginBottom:
                            "16px",

                        padding:
                            "12px",

                        backgroundColor:
                            "#fff3f3",

                        border:
                            "1px solid #f0b8b8",

                        borderRadius:
                            "4px",

                        color:
                            "#c62828",

                        fontSize:
                            "14px",
                    }}
                >
                    {error}
                </div>
            )}


            <form
                onSubmit={
                    handleSubmit
                }
            >

                {/* Project ID */}
                <div
                    style={{
                        marginBottom:
                            "16px",
                    }}
                >

                    <label
                        style={{
                            display:
                                "block",

                            marginBottom:
                                "6px",

                            fontWeight:
                                "bold",
                        }}
                    >
                        Project ID
                    </label>


                    <input
                        type="text"
                        value={
                            projectId
                        }
                        disabled
                        style={{
                            width:
                                "100%",

                            padding:
                                "10px",

                            boxSizing:
                                "border-box",

                            border:
                                "1px solid #dddddd",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#f5f5f5",

                            color:
                                "#666666",
                        }}
                    />

                </div>


                {/* WBS ID - Edit only */}
                {isEditMode &&
                    wbs && (
                        <div
                            style={{
                                marginBottom:
                                    "16px",
                            }}
                        >

                            <label
                                style={{
                                    display:
                                        "block",

                                    marginBottom:
                                        "6px",

                                    fontWeight:
                                        "bold",
                                }}
                            >
                                WBS ID
                            </label>


                            <input
                                type="text"
                                value={
                                    wbs.id
                                }
                                disabled
                                style={{
                                    width:
                                        "100%",

                                    padding:
                                        "10px",

                                    boxSizing:
                                        "border-box",

                                    border:
                                        "1px solid #dddddd",

                                    borderRadius:
                                        "4px",

                                    backgroundColor:
                                        "#f5f5f5",

                                    color:
                                        "#666666",
                                }}
                            />

                        </div>
                    )}


                {/* Parent WBS */}
                <div
                    style={{
                        marginBottom:
                            "16px",
                    }}
                >

                    <label
                        style={{
                            display:
                                "block",

                            marginBottom:
                                "6px",

                            fontWeight:
                                "bold",
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
                            width:
                                "100%",

                            padding:
                                "10px",

                            boxSizing:
                                "border-box",

                            border:
                                "1px solid #dddddd",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#f5f5f5",

                            color:
                                "#666666",
                        }}
                    />

                </div>


                {/* WBS Code */}
                <div
                    style={{
                        marginBottom:
                            "16px",
                    }}
                >

                    <label
                        style={{
                            display:
                                "block",

                            marginBottom:
                                "6px",

                            fontWeight:
                                "bold",
                        }}
                    >
                        WBS Code
                    </label>


                    <input
                        type="text"
                        value={
                            wbsCode
                        }
                        onChange={(
                            event,
                        ) =>
                            setWbsCode(
                                event.target
                                    .value,
                            )
                        }
                        placeholder="예: 3.1.1"
                        disabled={
                            submitting
                        }
                        style={{
                            width:
                                "100%",

                            padding:
                                "10px",

                            boxSizing:
                                "border-box",

                            border:
                                "1px solid #cccccc",

                            borderRadius:
                                "4px",
                        }}
                    />

                </div>


                {/* WBS Name */}
                <div
                    style={{
                        marginBottom:
                            "16px",
                    }}
                >

                    <label
                        style={{
                            display:
                                "block",

                            marginBottom:
                                "6px",

                            fontWeight:
                                "bold",
                        }}
                    >
                        WBS 명
                    </label>


                    <input
                        type="text"
                        value={
                            wbsName
                        }
                        onChange={(
                            event,
                        ) =>
                            setWbsName(
                                event.target
                                    .value,
                            )
                        }
                        placeholder="WBS 이름을 입력하세요."
                        disabled={
                            submitting
                        }
                        style={{
                            width:
                                "100%",

                            padding:
                                "10px",

                            boxSizing:
                                "border-box",

                            border:
                                "1px solid #cccccc",

                            borderRadius:
                                "4px",
                        }}
                    />

                </div>


                {/* Description */}
                <div
                    style={{
                        marginBottom:
                            "16px",
                    }}
                >

                    <label
                        style={{
                            display:
                                "block",

                            marginBottom:
                                "6px",

                            fontWeight:
                                "bold",
                        }}
                    >
                        설명
                    </label>


                    <textarea
                        value={
                            description
                        }
                        onChange={(
                            event,
                        ) =>
                            setDescription(
                                event.target
                                    .value,
                            )
                        }
                        placeholder="WBS 설명을 입력하세요."
                        disabled={
                            submitting
                        }
                        rows={4}
                        style={{
                            width:
                                "100%",

                            padding:
                                "10px",

                            boxSizing:
                                "border-box",

                            border:
                                "1px solid #cccccc",

                            borderRadius:
                                "4px",

                            resize:
                                "vertical",
                        }}
                    />

                </div>


                {/* Status */}
                <div
                    style={{
                        marginBottom:
                            "16px",
                    }}
                >

                    <label
                        style={{
                            display:
                                "block",

                            marginBottom:
                                "6px",

                            fontWeight:
                                "bold",
                        }}
                    >
                        상태
                    </label>


                    <select
                        value={
                            status
                        }
                        onChange={(
                            event,
                        ) =>
                            setStatus(
                                event.target
                                    .value as WbsStatus,
                            )
                        }
                        disabled={
                            submitting
                        }
                        style={{
                            width:
                                "100%",

                            padding:
                                "10px",

                            boxSizing:
                                "border-box",

                            border:
                                "1px solid #cccccc",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#ffffff",
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
                        marginBottom:
                            "20px",
                    }}
                >

                    <label
                        style={{
                            display:
                                "block",

                            marginBottom:
                                "6px",

                            fontWeight:
                                "bold",
                        }}
                    >
                        정렬 순서
                    </label>


                    <input
                        type="number"
                        min={1}
                        value={
                            sortOrder
                        }
                        onChange={(
                            event,
                        ) =>
                            setSortOrder(
                                Number(
                                    event.target
                                        .value,
                                ),
                            )
                        }
                        disabled={
                            submitting
                        }
                        style={{
                            width:
                                "100%",

                            padding:
                                "10px",

                            boxSizing:
                                "border-box",

                            border:
                                "1px solid #cccccc",

                            borderRadius:
                                "4px",
                        }}
                    />

                </div>


                {/* Buttons */}
                <div
                    style={{
                        display:
                            "flex",

                        justifyContent:
                            "flex-end",

                        gap:
                            "8px",
                    }}
                >

                    {/* Cancel */}
                    <button
                        type="button"
                        onClick={
                            onCancel
                        }
                        disabled={
                            submitting
                        }
                        style={{
                            padding:
                                "10px 16px",

                            border:
                                "1px solid #cccccc",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#ffffff",

                            cursor:
                                submitting
                                    ? "default"
                                    : "pointer",
                        }}
                    >
                        취소
                    </button>


                    {/* Submit */}
                    <button
                        type="submit"
                        disabled={
                            submitting
                        }
                        style={{
                            padding:
                                "10px 16px",

                            border:
                                "none",

                            borderRadius:
                                "4px",

                            backgroundColor:
                                "#1976d2",

                            color:
                                "#ffffff",

                            cursor:
                                submitting
                                    ? "default"
                                    : "pointer",

                            fontWeight:
                                "bold",
                        }}
                    >
                        {submitting
                            ? isEditMode
                                ? "수정 중..."
                                : "생성 중..."
                            : isEditMode
                              ? "수정"
                              : "생성"}
                    </button>

                </div>

            </form>

        </div>
    );
}


export default WbsForm;