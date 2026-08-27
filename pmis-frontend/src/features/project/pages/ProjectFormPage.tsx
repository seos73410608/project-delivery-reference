import {
    useEffect,
    useState,
} from "react";

import {
    useNavigate,
    useParams,
} from "react-router-dom";

import ProjectForm from "../components/ProjectForm";

import {
    createProject,
    getProject,
    updateProject,
} from "../api/projectApi";

import type {
    Project,
    ProjectCreateRequest,
    ProjectUpdateRequest,
} from "../types/project";

import "./ProjectFormPage.css";


function ProjectFormPage() {

    /**
     * =====================================================
     * Router
     * =====================================================
     */

    const navigate = useNavigate();

    const {
        projectId,
    } = useParams<{
        projectId: string;
    }>();


    /**
     * =====================================================
     * Form Mode
     * =====================================================
     *
     * /project/create
     *     → Create
     *
     * /project/:projectId/edit
     *     → Edit
     */

    const isEditMode =
        Boolean(projectId);


    /**
     * =====================================================
     * Project
     * =====================================================
     *
     * Edit mode에서
     * GET /api/projects/{id}/detail
     * 결과를 저장한다.
     */

    const [
        project,
        setProject,
    ] = useState<Project | null>(
        null,
    );


    /**
     * =====================================================
     * Loading
     * =====================================================
     *
     * Create:
     *     false
     *
     * Edit:
     *     Project 상세 조회 중 true
     */

    const [
        loading,
        setLoading,
    ] = useState(
        isEditMode,
    );


    /**
     * =====================================================
     * Error
     * =====================================================
     */

    const [
        error,
        setError,
    ] = useState<string | null>(
        null,
    );


    /**
     * =====================================================
     * Edit Project 조회
     * =====================================================
     *
     * GET /api/projects/{id}/detail
     */

    useEffect(() => {

        /**
         * Create mode
         *
         * Project 조회가 필요 없다.
         */

        if (!isEditMode) {

            setLoading(false);

            return;
        }


        /**
         * Project ID 확인
         */

        if (!projectId) {

            setLoading(false);

            setError(
                "프로젝트 ID가 없습니다.",
            );

            return;
        }


        /**
         * String → Number
         */

        const id =
            Number(projectId);


        /**
         * 잘못된 Project ID
         */

        if (
            !Number.isInteger(id) ||
            id <= 0
        ) {

            setLoading(false);

            setError(
                "잘못된 프로젝트 ID입니다.",
            );

            return;
        }


        /**
         * Project 상세 조회
         */

        const fetchProject =
            async () => {

                try {

                    setLoading(true);

                    setError(null);


                    const data =
                        await getProject(id);


                    setProject(
                        data,
                    );

                } catch (err) {

                    console.error(
                        "프로젝트 상세 조회 실패:",
                        err,
                    );


                    setProject(null);


                    setError(
                        "프로젝트 정보를 불러오지 못했습니다.",
                    );

                } finally {

                    setLoading(false);

                }

            };


        fetchProject();

    }, [
        isEditMode,
        projectId,
    ]);


    /**
     * =====================================================
     * Project Create
     * =====================================================
     *
     * POST /api/projects
     */

    const handleCreate = async (
        request: ProjectCreateRequest,
    ): Promise<void> => {

        const createdProject =
            await createProject(
                request,
            );


        /**
         * 생성 완료
         *
         * 생성된 프로젝트 상세 페이지로 이동
         *
         * /project/{id}
         */

        navigate(
            `/project/${createdProject.id}/detail`,
        );

    };


    /**
     * =====================================================
     * Project Update
     * =====================================================
     *
     * PUT /api/projects/{id}
     */

    const handleUpdate = async (
        id: number,
        request: ProjectUpdateRequest,
    ): Promise<void> => {

        await updateProject(
            id,
            request,
        );


        /**
         * 수정 완료
         *
         * 프로젝트 상세 페이지로 이동
         *
         * /project/{id}/detail
         */

        navigate(
            `/project/${id}/detail`,
        );

    };


    /**
     * =====================================================
     * Cancel
     * =====================================================
     */

    const handleCancel = () => {

        /**
         * Edit
         *
         * /project/{id}/edit
         *     ↓
         * /project/{id}
         */

        if (
            isEditMode &&
            projectId
        ) {

            navigate(
                `/project/${projectId}`,
            );

            return;
        }


        /**
         * Create
         *
         * /project/create
         *     ↓
         * /project
         */

        navigate(
            "/project",
        );

    };


    /**
     * =====================================================
     * Loading
     * =====================================================
     *
     * Edit mode에서만 발생한다.
     */

    if (
        isEditMode &&
        loading
    ) {

        return (

            <div
                className="project-form-page"
            >

                <div
                    className="project-form-page__state"
                >

                    <div
                        className="project-form-page__spinner"
                    />

                    <span>
                        프로젝트 정보를 불러오는 중입니다...
                    </span>

                </div>

            </div>

        );

    }


    /**
     * =====================================================
     * Error
     * =====================================================
     */

    if (error) {

        return (

            <div
                className="project-form-page"
            >

                <div
                    className="project-form-page__state project-form-page__state--error"
                >

                    <div
                        className="project-form-page__error-icon"
                    >
                        !
                    </div>


                    <strong>
                        프로젝트 정보를 불러올 수 없습니다.
                    </strong>


                    <span>
                        {error}
                    </span>


                    <button
                        type="button"
                        className="project-form-page__back-button"
                        onClick={handleCancel}
                    >
                        돌아가기
                    </button>

                </div>

            </div>

        );

    }


    /**
     * =====================================================
     * Edit mode인데 Project가 없는 경우
     * =====================================================
     */

    if (
        isEditMode &&
        !project
    ) {

        return (

            <div
                className="project-form-page"
            >

                <div
                    className="project-form-page__state project-form-page__state--error"
                >

                    <div
                        className="project-form-page__error-icon"
                    >
                        !
                    </div>


                    <strong>
                        프로젝트를 찾을 수 없습니다.
                    </strong>


                    <span>
                        수정할 프로젝트 정보가 존재하지 않습니다.
                    </span>


                    <button
                        type="button"
                        className="project-form-page__back-button"
                        onClick={handleCancel}
                    >
                        프로젝트 상세로 돌아가기
                    </button>

                </div>

            </div>

        );

    }


    /**
     * =====================================================
     * Render
     * =====================================================
     */

    return (

        <div
            className="project-form-page"
        >

            <div
                className="project-form-page__container"
            >

                <ProjectForm

                    mode={
                        isEditMode
                            ? "edit"
                            : "create"
                    }


                    project={
                        isEditMode
                            ? project
                            : null
                    }


                    onCreate={
                        handleCreate
                    }


                    onUpdate={
                        handleUpdate
                    }


                    onCancel={
                        handleCancel
                    }

                />

            </div>

        </div>

    );

}


export default ProjectFormPage;