import {
    useEffect,
    useState,
} from "react";

import type {
    Project,
    ProjectCreateRequest,
    ProjectPriority,
    ProjectStatus,
    ProjectUpdateRequest,
} from "../types/project";

import "./ProjectForm.css";


interface ProjectFormProps {

    /**
     * Form 모드
     *
     * create:
     * 신규 프로젝트 등록
     *
     * edit:
     * 기존 프로젝트 수정
     */
    mode: "create" | "edit";


    /**
     * 수정 대상 프로젝트
     *
     * create:
     * null
     *
     * edit:
     * 선택된 Project
     */
    project?: Project | null;


    /**
     * 프로젝트 생성
     *
     * POST /api/projects
     */
    onCreate?: (
        request: ProjectCreateRequest,
    ) => Promise<void>;


    /**
     * 프로젝트 수정
     *
     * PUT /api/projects/{id}
     */
    onUpdate?: (
        id: number,
        request: ProjectUpdateRequest,
    ) => Promise<void>;


    /**
     * Form 취소
     */
    onCancel: () => void;
}


function ProjectForm({
    mode,
    project,
    onCreate,
    onUpdate,
    onCancel,
}: ProjectFormProps) {


    /**
     * 수정 모드 여부
     */
    const isEditMode =
        mode === "edit";


    /**
     * Project Code
     *
     * Create:
     * 입력 가능
     *
     * Edit:
     * 기존 값 표시 / 수정 불가
     */
    const [
        projectCode,
        setProjectCode,
    ] = useState(
        isEditMode && project
            ? project.projectCode
            : "",
    );


    /**
     * Project Name
     */
    const [
        projectName,
        setProjectName,
    ] = useState(
        isEditMode && project
            ? project.projectName
            : "",
    );


    /**
     * Customer Name
     */
    const [
        customerName,
        setCustomerName,
    ] = useState(
        isEditMode && project
            ? project.customerName
            : "",
    );


    /**
     * Project Manager
     */
    const [
        projectManager,
        setProjectManager,
    ] = useState(
        isEditMode && project
            ? project.projectManager
            : "",
    );


    /**
     * Description
     */
    const [
        description,
        setDescription,
    ] = useState(
        isEditMode && project
            ? project.description ?? ""
            : "",
    );


    /**
     * Start Date
     */
    const [
        startDate,
        setStartDate,
    ] = useState(
        isEditMode && project
            ? project.startDate
            : "",
    );


    /**
     * End Date
     */
    const [
        endDate,
        setEndDate,
    ] = useState(
        isEditMode && project
            ? project.endDate
            : "",
    );


    /**
     * Status
     *
     * Create:
     * 기본값 PLANNING
     *
     * Edit:
     * 기존 상태
     */
    const [
        status,
        setStatus,
    ] = useState<ProjectStatus>(
        isEditMode && project
            ? project.status
            : "PLANNING",
    );


    /**
     * Priority
     *
     * Create:
     * 기본값 MEDIUM
     *
     * Edit:
     * 기존 우선순위
     */
    const [
        priority,
        setPriority,
    ] = useState<ProjectPriority>(
        isEditMode && project
            ? project.priority
            : "MEDIUM",
    );


    /**
     * Submit 상태
     */
    const [
        submitting,
        setSubmitting,
    ] = useState(false);


    /**
     * Error
     */
    const [
        error,
        setError,
    ] = useState<string | null>(
        null,
    );


    /**
     * Form mode 또는
     * 수정 대상 Project가 변경되면
     * Form 값을 다시 초기화한다.
     */
    useEffect(() => {

        if (mode === "edit" && project) {

            setProjectCode(
                project.projectCode,
            );

            setProjectName(
                project.projectName,
            );

            setCustomerName(
                project.customerName,
            );

            setProjectManager(
                project.projectManager,
            );

            setDescription(
                project.description ?? "",
            );

            setStartDate(
                project.startDate,
            );

            setEndDate(
                project.endDate,
            );

            setStatus(
                project.status,
            );

            setPriority(
                project.priority,
            );

            setError(null);

            return;
        }


        /**
         * Create mode
         */
        setProjectCode("");
        setProjectName("");
        setCustomerName("");
        setProjectManager("");
        setDescription("");
        setStartDate("");
        setEndDate("");
        setStatus("PLANNING");
        setPriority("MEDIUM");
        setError(null);

    }, [
        mode,
        project,
    ]);


    /**
     * Form 제목
     */
    const formTitle =
        isEditMode
            ? "프로젝트 수정"
            : "프로젝트 등록";


    /**
     * Form 설명
     */
    const formDescription =
        isEditMode
            ? "기존 프로젝트 정보를 수정합니다."
            : "새로운 프로젝트를 등록합니다.";


    /**
     * Form Submit
     *
     * Create:
     * POST /api/projects
     *
     * Update:
     * PUT /api/projects/{id}
     */
    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>,
    ) => {

        event.preventDefault();


        /**
         * Edit mode인데
         * 수정 대상 Project가 없는 경우
         */
        if (
            isEditMode &&
            !project
        ) {

            setError(
                "수정할 프로젝트가 선택되지 않았습니다.",
            );

            return;
        }


        /**
         * Project Code validation
         *
         * 프로젝트 코드는
         * Create에서만 필수
         */
        if (
            !isEditMode &&
            !projectCode.trim()
        ) {

            setError(
                "프로젝트 코드를 입력해주세요.",
            );

            return;
        }


        /**
         * Project Name validation
         */
        if (!projectName.trim()) {

            setError(
                "프로젝트명을 입력해주세요.",
            );

            return;
        }


        /**
         * Customer validation
         */
        if (!customerName.trim()) {

            setError(
                "고객사를 입력해주세요.",
            );

            return;
        }


        /**
         * Project Manager validation
         */
        if (!projectManager.trim()) {

            setError(
                "PM을 입력해주세요.",
            );

            return;
        }


        /**
         * Start Date validation
         */
        if (!startDate) {

            setError(
                "시작일을 입력해주세요.",
            );

            return;
        }


        /**
         * End Date validation
         */
        if (!endDate) {

            setError(
                "종료일을 입력해주세요.",
            );

            return;
        }


        /**
         * Date validation
         */
        if (startDate > endDate) {

            setError(
                "종료일은 시작일보다 빠를 수 없습니다.",
            );

            return;
        }


        try {

            setSubmitting(true);
            setError(null);


            /**
             * Create
             *
             * POST /api/projects
             */
            if (!isEditMode) {

                if (!onCreate) {

                    throw new Error(
                        "프로젝트 생성 처리 함수가 없습니다.",
                    );
                }


                /**
                 * ProjectCreateRequest에는
                 * status가 없다.
                 */
                const createRequest:
                    ProjectCreateRequest = {

                    projectCode:
                        projectCode.trim(),

                    projectName:
                        projectName.trim(),

                    customerName:
                        customerName.trim(),

                    projectManager:
                        projectManager.trim(),

                    description:
                        description.trim(),

                    startDate,

                    endDate,

                    priority,

                };


                await onCreate(
                    createRequest,
                );

                return;
            }


            /**
             * Update
             *
             * PUT /api/projects/{id}
             */
            if (!onUpdate) {

                throw new Error(
                    "프로젝트 수정 처리 함수가 없습니다.",
                );
            }


            if (!project) {

                throw new Error(
                    "수정할 프로젝트가 없습니다.",
                );
            }


            /**
             * ProjectUpdateRequest에는
             * projectCode가 없다.
             */
            const updateRequest:
                ProjectUpdateRequest = {

                projectName:
                    projectName.trim(),

                customerName:
                    customerName.trim(),

                projectManager:
                    projectManager.trim(),

                description:
                    description.trim(),

                startDate,

                endDate,

                status,

                priority,

            };


            await onUpdate(
                project.id,
                updateRequest,
            );

        } catch (err) {

            console.error(
                isEditMode
                    ? "Failed to update project:"
                    : "Failed to create project:",
                err,
            );


            setError(
                isEditMode
                    ? "프로젝트 수정에 실패했습니다."
                    : "프로젝트 등록에 실패했습니다.",
            );

        } finally {

            setSubmitting(false);

        }

    };


    return (

        <section
            className="project-form"
        >

            {/* Header */}

            <div
                className="project-form__header"
            >

                <div>

                    <div
                        className="project-form__eyebrow"
                    >
                        PROJECT MANAGEMENT
                    </div>


                    <h2
                        className="project-form__title"
                    >
                        {formTitle}
                    </h2>


                    <p
                        className="project-form__description"
                    >
                        {formDescription}
                    </p>

                </div>

            </div>


            {/* Error */}

            {error && (

                <div
                    className="state state--error project-form__error"
                    role="alert"
                >

                    <span
                        className="state__icon"
                    >
                        !
                    </span>


                    <span
                        className="state__description"
                    >
                        {error}
                    </span>

                </div>

            )}


            {/* Form */}

            <form
                className="form project-form__form"
                onSubmit={handleSubmit}
            >


                {/* Project ID */}

                {isEditMode &&
                    project && (

                    <div
                        className="form__field"
                    >

                        <label
                            htmlFor="projectId"
                            className="form__label"
                        >
                            Project ID
                        </label>


                        <input
                            id="projectId"
                            type="text"
                            className="form__input"
                            value={project.id}
                            disabled
                        />

                    </div>

                )}


                {/* Project Code */}

                <div
                    className="form__field"
                >

                    <label
                        htmlFor="projectCode"
                        className="form__label"
                    >
                        프로젝트 코드
                    </label>


                    <input
                        id="projectCode"
                        type="text"
                        className="form__input"
                        value={projectCode}
                        onChange={(event) =>
                            setProjectCode(
                                event.target.value,
                            )
                        }
                        placeholder="예: PMIS-2026-001"
                        disabled={
                            submitting ||
                            isEditMode
                        }
                    />


                    {isEditMode && (

                        <span
                            className="form__help"
                        >
                            프로젝트 코드는 수정할 수 없습니다.
                        </span>

                    )}

                </div>


                {/* Project Name */}

                <div
                    className="form__field"
                >

                    <label
                        htmlFor="projectName"
                        className="form__label"
                    >
                        프로젝트명
                    </label>


                    <input
                        id="projectName"
                        type="text"
                        className="form__input"
                        value={projectName}
                        onChange={(event) =>
                            setProjectName(
                                event.target.value,
                            )
                        }
                        placeholder="프로젝트명을 입력하세요."
                        disabled={
                            submitting
                        }
                    />

                </div>


                {/* Customer */}

                <div
                    className="form__field"
                >

                    <label
                        htmlFor="customerName"
                        className="form__label"
                    >
                        고객사
                    </label>


                    <input
                        id="customerName"
                        type="text"
                        className="form__input"
                        value={customerName}
                        onChange={(event) =>
                            setCustomerName(
                                event.target.value,
                            )
                        }
                        placeholder="고객사명을 입력하세요."
                        disabled={
                            submitting
                        }
                    />

                </div>


                {/* Project Manager */}

                <div
                    className="form__field"
                >

                    <label
                        htmlFor="projectManager"
                        className="form__label"
                    >
                        PM
                    </label>


                    <input
                        id="projectManager"
                        type="text"
                        className="form__input"
                        value={projectManager}
                        onChange={(event) =>
                            setProjectManager(
                                event.target.value,
                            )
                        }
                        placeholder="프로젝트 PM을 입력하세요."
                        disabled={
                            submitting
                        }
                    />

                </div>


                {/* Status - Edit only */}

                {isEditMode && (

                    <div
                        className="form__field"
                    >

                        <label
                            htmlFor="status"
                            className="form__label"
                        >
                            상태
                        </label>


                        <select
                            id="status"
                            className="form__select"
                            value={status}
                            onChange={(event) =>
                                setStatus(
                                    event.target
                                        .value as ProjectStatus,
                                )
                            }
                            disabled={
                                submitting
                            }
                        >

                            <option value="PLANNING">
                                계획
                            </option>

                            <option value="IN_PROGRESS">
                                진행중
                            </option>

                            <option value="ON_HOLD">
                                보류
                            </option>

                            <option value="COMPLETED">
                                완료
                            </option>

                            <option value="CANCELLED">
                                취소
                            </option>

                        </select>

                    </div>

                )}


                {/* Priority */}

                <div
                    className="form__field"
                >

                    <label
                        htmlFor="priority"
                        className="form__label"
                    >
                        우선순위
                    </label>


                    <select
                        id="priority"
                        className="form__select"
                        value={priority}
                        onChange={(event) =>
                            setPriority(
                                event.target
                                    .value as ProjectPriority,
                            )
                        }
                        disabled={
                            submitting
                        }
                    >

                        <option value="LOW">
                            낮음
                        </option>

                        <option value="MEDIUM">
                            보통
                        </option>

                        <option value="HIGH">
                            높음
                        </option>

                        <option value="CRITICAL">
                            긴급
                        </option>

                    </select>

                </div>


                {/* Start Date */}

                <div
                    className="form__field"
                >

                    <label
                        htmlFor="startDate"
                        className="form__label"
                    >
                        시작일
                    </label>


                    <input
                        id="startDate"
                        type="date"
                        className="form__input"
                        value={startDate}
                        onChange={(event) =>
                            setStartDate(
                                event.target.value,
                            )
                        }
                        disabled={
                            submitting
                        }
                    />

                </div>


                {/* End Date */}

                <div
                    className="form__field"
                >

                    <label
                        htmlFor="endDate"
                        className="form__label"
                    >
                        종료일
                    </label>


                    <input
                        id="endDate"
                        type="date"
                        className="form__input"
                        value={endDate}
                        onChange={(event) =>
                            setEndDate(
                                event.target.value,
                            )
                        }
                        disabled={
                            submitting
                        }
                    />

                </div>


                {/* Description */}

                <div
                    className="form__field form__field--full"
                >

                    <label
                        htmlFor="description"
                        className="form__label"
                    >
                        설명
                    </label>


                    <textarea
                        id="description"
                        className="form__textarea"
                        value={description}
                        onChange={(event) =>
                            setDescription(
                                event.target.value,
                            )
                        }
                        placeholder="프로젝트 설명을 입력하세요."
                        rows={5}
                        disabled={
                            submitting
                        }
                    />

                </div>


                {/* Actions */}

                <div
                    className="form__actions project-form__actions"
                >

                    {/* Cancel */}

                    <button
                        type="button"
                        className="button button--secondary"
                        onClick={onCancel}
                        disabled={
                            submitting
                        }
                    >
                        취소
                    </button>


                    {/* Submit */}

                    <button
                        type="submit"
                        className="button button--primary"
                        disabled={
                            submitting
                        }
                    >

                        {submitting
                            ? isEditMode
                                ? "수정 중..."
                                : "등록 중..."
                            : isEditMode
                                ? "수정"
                                : "등록"}

                    </button>

                </div>

            </form>

        </section>

    );
}


export default ProjectForm;