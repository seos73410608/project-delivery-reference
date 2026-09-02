import { useEffect, useState } from "react";

import type {
    WbsCreateRequest,
    WbsStatus,
    WbsTreeResponse,
    WbsUpdateRequest,
} from "@/features/wbs/types/wbs";

import { WBS_STATUS_LABEL } from "@/features/wbs/types/wbs";

import "@/features/wbs/styles/wbs.css";


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


const statusOptions: WbsStatus[] = [
    "PLANNED",
    "IN_PROGRESS",
    "COMPLETED",
    "ON_HOLD",
    "CANCELLED",
];


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

            setWbsCode(
                wbs.wbsCode,
            );

            setWbsName(
                wbs.wbsName,
            );

            setDescription(
                wbs.description ?? "",
            );

            setStatus(
                wbs.status,
            );

            setSortOrder(
                wbs.sortOrder,
            );

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
     */
    const parentId =
        parentWbs?.id ?? null;


    /**
     * WBS Submit
     */
    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
    ) => {

        event.preventDefault();


        /**
         * 수정 모드인데
         * 수정 대상 WBS가 없는 경우
         */
        if (
            isEditMode &&
            !wbs
        ) {

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
        <div className="wbs-form">

            {/* Header */}
            <div className="wbs-form-header">

                <h2 className="wbs-form-title">
                    {formTitle}
                </h2>


                <p className="wbs-form-description">
                    {formDescription}
                </p>

            </div>


            {/* Error */}
            {error && (
                <div className="state state--error wbs-form-error">

                    <div className="state__icon">
                        !
                    </div>

                    <p className="state__description">
                        {error}
                    </p>

                </div>
            )}


            <form
                onSubmit={handleSubmit}
            >

                {/* Project ID */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-project-id"
                        className="form__label"
                    >
                        Project ID
                    </label>


                    <input
                        id="wbs-project-id"
                        type="text"
                        value={projectId}
                        disabled
                        className="form__input"
                    />

                </div>


                {/* WBS ID - Edit only */}
                {isEditMode &&
                    wbs && (
                        <div className="form__field">

                            <label
                                htmlFor="wbs-id"
                                className="form__label"
                            >
                                WBS ID
                            </label>


                            <input
                                id="wbs-id"
                                type="text"
                                value={wbs.id}
                                disabled
                                className="form__input"
                            />

                        </div>
                    )}


                {/* Parent WBS */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-parent"
                        className="form__label"
                    >
                        Parent WBS
                    </label>


                    <input
                        id="wbs-parent"
                        type="text"
                        value={
                            parentWbs
                                ? `${parentWbs.wbsCode} ${parentWbs.wbsName}`
                                : "최상위 WBS"
                        }
                        disabled
                        className="form__input"
                    />

                </div>


                {/* WBS Code */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-code"
                        className="form__label"
                    >
                        WBS Code
                    </label>


                    <input
                        id="wbs-code"
                        type="text"
                        value={wbsCode}
                        onChange={(event) =>
                            setWbsCode(
                                event.target.value,
                            )
                        }
                        placeholder="예: 3.1.1"
                        disabled={submitting}
                        className="form__input"
                    />

                </div>


                {/* WBS Name */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-name"
                        className="form__label"
                    >
                        WBS 명
                    </label>


                    <input
                        id="wbs-name"
                        type="text"
                        value={wbsName}
                        onChange={(event) =>
                            setWbsName(
                                event.target.value,
                            )
                        }
                        placeholder="WBS 이름을 입력하세요."
                        disabled={submitting}
                        className="form__input"
                    />

                </div>


                {/* Description */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-description"
                        className="form__label"
                    >
                        설명
                    </label>


                    <textarea
                        id="wbs-description"
                        value={description}
                        onChange={(event) =>
                            setDescription(
                                event.target.value,
                            )
                        }
                        placeholder="WBS 설명을 입력하세요."
                        disabled={submitting}
                        rows={4}
                        className="form__textarea"
                    />

                </div>


                {/* Status */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-status"
                        className="form__label"
                    >
                        상태
                    </label>


                    <select
                        id="wbs-status"
                        value={status}
                        onChange={(event) =>
                            setStatus(
                                event.target.value as WbsStatus,
                            )
                        }
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


                {/* Sort Order */}
                <div className="form__field">

                    <label
                        htmlFor="wbs-sort-order"
                        className="form__label"
                    >
                        정렬 순서
                    </label>


                    <input
                        id="wbs-sort-order"
                        type="number"
                        min={1}
                        value={sortOrder}
                        onChange={(event) =>
                            setSortOrder(
                                Number(
                                    event.target.value,
                                ),
                            )
                        }
                        disabled={submitting}
                        className="form__input"
                    />

                </div>


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