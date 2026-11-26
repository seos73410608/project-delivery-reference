
import {
  useCallback,
  useEffect,
  useState,
} from 'react';

import {
  useNavigate,
  useParams,
} from 'react-router-dom';

import {
  deleteProject,
  getProject,
} from '../api/projectApi';

import ProjectDetail from '../components/ProjectDetail';

import { useAuth } from '@/features/auth/context/AuthProvider';

import {
  createProjectOperation,
  getOperations,
  getProjectOperationsByProject,
  updateProjectOperation,
  updateProjectOperationStatus,
} from '@/features/projectoperation/api/projectOperationApi';

import ProjectOperationList from '@/features/projectoperation/components/ProjectOperationList';
import ProjectOperationForm from '@/features/projectoperation/components/ProjectOperationForm';

import type {
  OperationResponse,
  ProjectOperationResponse,
  ProjectOperationStatus,
} from '@/features/projectoperation/types/projectOperation';

import type {
  ProjectDetail as ProjectDetailType,
} from '../types/project';

import './ProjectDetailPage.css';


interface ProjectOperationFormValues {
  operationId?: number;
  relationshipType: 'BUILD_RESULT' | 'ENHANCEMENT' | 'MIGRATION' | 'OTHER';
  effectiveFrom: string;
  effectiveTo: string;
  description: string;
}


function ProjectDetailPage() {

  /**
   * =====================================================
   * Router / Auth
   * =====================================================
   */

  const {
    projectId,
  } = useParams<{
    projectId: string;
  }>();

  const navigate = useNavigate();

  const { user } = useAuth();

  const isAdmin = user?.role === 'ROLE_ADMIN';


  /**
   * =====================================================
   * Project
   * =====================================================
   */

  const [
    project,
    setProject,
  ] = useState<ProjectDetailType | null>(null);


  const [
    loading,
    setLoading,
  ] = useState(true);


  const [
    deleting,
    setDeleting,
  ] = useState(false);


  const [
    error,
    setError,
  ] = useState<string | null>(null);


  /**
   * =====================================================
   * Project Operation
   * =====================================================
   */

  const [
    projectOperations,
    setProjectOperations,
  ] = useState<ProjectOperationResponse[]>([]);


  const [
    projectOperationsLoading,
    setProjectOperationsLoading,
  ] = useState(false);


  const [
    projectOperationsError,
    setProjectOperationsError,
  ] = useState<string | null>(null);


  /**
   * =====================================================
   * Operation Master
   * =====================================================
   */

  const [
    operations,
    setOperations,
  ] = useState<OperationResponse[]>([]);


  const [
    operationsLoading,
    setOperationsLoading,
  ] = useState(false);


  /**
   * =====================================================
   * Project Operation Form
   * =====================================================
   */

  const [
    showProjectOperationForm,
    setShowProjectOperationForm,
  ] = useState(false);


  const [
    selectedProjectOperation,
    setSelectedProjectOperation,
  ] = useState<ProjectOperationResponse | null>(null);


  const [
    projectOperationSaving,
    setProjectOperationSaving,
  ] = useState(false);


  const [
    projectOperationFormError,
    setProjectOperationFormError,
  ] = useState<string | null>(null);


  /**
   * =====================================================
   * 프로젝트 상세 조회
   * =====================================================
   *
   * GET /api/projects/{id}/detail
   */

  const fetchProject = useCallback(
    async () => {

      if (!projectId) {

        setProject(null);

        setError(
          '프로젝트 ID가 없습니다.',
        );

        setLoading(false);

        return;
      }


      const id = Number(projectId);


      if (
        !Number.isInteger(id) ||
        id <= 0
      ) {

        setProject(null);

        setError(
          '올바르지 않은 프로젝트 ID입니다.',
        );

        setLoading(false);

        return;
      }


      try {

        setLoading(true);

        setError(null);

        const response = await getProject(id);

        setProject(response);

      } catch (err) {

        console.error(
          '프로젝트 상세 조회 실패:',
          err,
        );

        setProject(null);

        setError(
          '프로젝트 정보를 불러오지 못했습니다.',
        );

      } finally {

        setLoading(false);

      }

    },
    [
      projectId,
    ],
  );


  /**
   * =====================================================
   * 프로젝트 상세 최초 조회
   * =====================================================
   */

  useEffect(() => {

    fetchProject();

  }, [
    fetchProject,
  ]);


  /**
   * =====================================================
   * 프로젝트 연결 운영 서비스 조회
   * =====================================================
   *
   * GET /api/project-operations/by-project/{projectId}
   */

  const fetchProjectOperations = useCallback(
    async () => {

      if (!project) {
        return;
      }


      try {

        setProjectOperationsLoading(true);

        setProjectOperationsError(null);


        const response =
          await getProjectOperationsByProject(
            project.id,
          );


        setProjectOperations(response);

      } catch (err) {

        console.error(
          '프로젝트 운영 서비스 조회 실패:',
          err,
        );

        setProjectOperationsError(
          '연결된 운영 서비스 정보를 불러오지 못했습니다.',
        );

      } finally {

        setProjectOperationsLoading(false);

      }

    },
    [
      project,
    ],
  );


  useEffect(() => {

    if (project) {

      fetchProjectOperations();

    }

  }, [
    project,
    fetchProjectOperations,
  ]);


  /**
   * =====================================================
   * 운영 서비스 마스터 조회
   * =====================================================
   *
   * GET /api/operations
   *
   * 등록 모달을 열 때 조회
   */

  const fetchOperations = async () => {

    try {

      setOperationsLoading(true);

      setProjectOperationFormError(null);


      const response = await getOperations();

      setOperations(response);

    } catch (err) {

      console.error(
        '운영 서비스 목록 조회 실패:',
        err,
      );

      setProjectOperationFormError(
        '운영 서비스 목록을 불러오지 못했습니다.',
      );

    } finally {

      setOperationsLoading(false);

    }

  };


  /**
   * =====================================================
   * 운영 서비스 연결 등록 모달
   * =====================================================
   */

  const handleCreateProjectOperation = () => {

    if (!isAdmin || !project) {
      return;
    }

    setSelectedProjectOperation(null);

    setProjectOperationFormError(null);

    setOperations([]);

    setShowProjectOperationForm(true);

    fetchOperations();

  };


  /**
   * =====================================================
   * 운영 서비스 연결 수정 모달
   * =====================================================
   */

  const handleEditProjectOperation = (
    projectOperation: ProjectOperationResponse,
  ) => {

    if (!isAdmin) {
      return;
    }

    setSelectedProjectOperation(projectOperation);

    setProjectOperationFormError(null);

    setShowProjectOperationForm(true);

  };


  /**
   * =====================================================
   * 인수인계 이력 페이지 이동
   * =====================================================
   *
   * /project/{projectId}/operations/{projectOperationId}/handovers
   */

  const handleViewHandovers = (
    projectOperation: ProjectOperationResponse,
  ) => {

    if (!project) {
      return;
    }

    navigate(
      `/project/${project.id}/operations/${projectOperation.id}/handovers`,
    );

  };


  /**
   * =====================================================
   * 운영 서비스 연결 모달 닫기
   * =====================================================
   */

  const handleCloseProjectOperationForm = () => {

    if (projectOperationSaving) {
      return;
    }

    setShowProjectOperationForm(false);

    setSelectedProjectOperation(null);

    setProjectOperationFormError(null);

    setOperations([]);

  };


  /**
   * =====================================================
   * 운영 서비스 연결 등록 / 수정
   * =====================================================
   *
   * POST /api/project-operations
   * PUT  /api/project-operations/{id}
   */

  const handleProjectOperationSubmit = async (
    values: ProjectOperationFormValues,
  ) => {

    if (!project || !isAdmin) {
      return;
    }


    try {

      setProjectOperationSaving(true);

      setProjectOperationFormError(null);


      const optionalFields = {
        ...(values.effectiveFrom
          ? { effectiveFrom: values.effectiveFrom }
          : {}),
        ...(values.effectiveTo
          ? { effectiveTo: values.effectiveTo }
          : {}),
        ...(values.description.trim()
          ? { description: values.description.trim() }
          : {}),
      };


      if (selectedProjectOperation) {

        /**
         * 기존 연결 수정
         *
         * Project / Operation 변경 불가
         */

        await updateProjectOperation(
          selectedProjectOperation.id,
          {
            relationshipType: values.relationshipType,
            ...optionalFields,
          },
        );

      } else {

        /**
         * 신규 연결 등록
         */

        if (!values.operationId) {

          setProjectOperationFormError(
            '연결할 운영 서비스를 선택해 주세요.',
          );

          return;
        }


        await createProjectOperation({
          projectId: project.id,
          operationId: values.operationId,
          relationshipType: values.relationshipType,
          ...optionalFields,
        });

      }


      /**
       * 저장 성공 후 목록 갱신
       */

      await fetchProjectOperations();


      setShowProjectOperationForm(false);

      setSelectedProjectOperation(null);

      setOperations([]);

    } catch (err) {

      console.error(
        '프로젝트 운영 서비스 저장 실패:',
        err,
      );

      setProjectOperationFormError(
        '운영 서비스 관계 저장에 실패했습니다. 입력 정보와 권한을 확인해 주세요.',
      );

    } finally {

      setProjectOperationSaving(false);

    }

  };


  /**
   * =====================================================
   * 운영 서비스 관계 상태 변경
   * =====================================================
   *
   * PATCH /api/project-operations/{id}/status
   */

  const handleProjectOperationStatusChange = async (
    projectOperation: ProjectOperationResponse,
    status: ProjectOperationStatus,
  ) => {

    if (!isAdmin) {
      return;
    }


    const statusLabels: Record<ProjectOperationStatus, string> = {
      PLANNED: '계획',
      ACTIVE: '운영 중',
      COMPLETED: '완료',
      CANCELLED: '취소',
    };


    const confirmed = window.confirm(
      `"${projectOperation.operationName}" 관계 상태를 "${statusLabels[status]}"(으)로 변경하시겠습니까?`,
    );


    if (!confirmed) {
      return;
    }


    try {

      setProjectOperationsError(null);


      await updateProjectOperationStatus(
        projectOperation.id,
        {
          status,
        },
      );


      await fetchProjectOperations();

    } catch (err) {

      console.error(
        '운영 서비스 관계 상태 변경 실패:',
        err,
      );

      setProjectOperationsError(
        '운영 서비스 관계 상태 변경에 실패했습니다.',
      );

    }

  };


  /**
   * =====================================================
   * 모달 ESC 키 처리
   * =====================================================
   */

  useEffect(() => {

    if (!showProjectOperationForm) {
      return;
    }


    const handleKeyDown = (
      event: KeyboardEvent,
    ) => {

      if (
        event.key === 'Escape' &&
        !projectOperationSaving
      ) {

        handleCloseProjectOperationForm();

      }

    };


    window.addEventListener(
      'keydown',
      handleKeyDown,
    );


    return () => {

      window.removeEventListener(
        'keydown',
        handleKeyDown,
      );

    };

  }, [
    showProjectOperationForm,
    projectOperationSaving,
  ]);


  /**
   * =====================================================
   * 프로젝트 목록으로 이동
   * =====================================================
   */

  const handleBack = () => {

    navigate('/project');

  };


  /**
   * =====================================================
   * 프로젝트 수정 페이지 이동
   * =====================================================
   */

  const handleEdit = () => {

    if (!project) {
      return;
    }

    navigate(
      `/project/${project.id}/edit`,
    );

  };


  /**
   * =====================================================
   * 변경 관리 페이지 이동
   * =====================================================
   */

  const handleChangeManagement = () => {

    if (!project) {
      return;
    }

    navigate(
      `/project/${project.id}/change`,
    );

  };


  /**
   * =====================================================
   * 증적 관리 페이지 이동
   * =====================================================
   */

  const handleEvidenceManagement = () => {

    if (!project) {
      return;
    }

    navigate(
      `/project/${project.id}/evidence`,
    );

  };


  /**
   * =====================================================
   * 프로젝트 삭제
   * =====================================================
   *
   * DELETE /api/projects/{id}
   */

  const handleDelete = async () => {

    if (!project) {
      return;
    }


    const confirmed = window.confirm(
      `프로젝트 "${project.projectName}"을(를) 삭제하시겠습니까?\n\n삭제한 프로젝트는 복구할 수 없습니다.`,
    );


    if (!confirmed) {
      return;
    }


    try {

      setDeleting(true);

      setError(null);


      await deleteProject(
        project.id,
      );


      navigate('/project');

    } catch (err) {

      console.error(
        '프로젝트 삭제 실패:',
        err,
      );

      setError(
        '프로젝트 삭제에 실패했습니다. 삭제 권한을 확인해주세요.',
      );

    } finally {

      setDeleting(false);

    }

  };


  /**
   * =====================================================
   * Loading
   * =====================================================
   */

  if (loading) {

    return (

      <div className="page project-detail-page">

        <div className="state state--loading project-detail-page__loading">

          <div className="spinner" />

          <span className="state__description">
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

  if (error && !project) {

    return (

      <div className="page project-detail-page">

        <div
          className="state state--error project-detail-page__error"
          role="alert"
        >

          <div className="state__icon">
            !
          </div>

          <strong className="state__title">
            프로젝트 정보를 불러올 수 없습니다.
          </strong>

          <span className="state__description">
            {error}
          </span>

        </div>

        <button
          type="button"
          className="button button--secondary"
          onClick={handleBack}
        >
          프로젝트 목록
        </button>

      </div>

    );

  }


  /**
   * =====================================================
   * Project 없음
   * =====================================================
   */

  if (!project) {

    return (

      <div className="page project-detail-page">

        <div
          className="state state--error project-detail-page__error"
          role="alert"
        >

          <div className="state__icon">
            !
          </div>

          <strong className="state__title">
            프로젝트를 찾을 수 없습니다.
          </strong>

          <span className="state__description">
            요청한 프로젝트 정보가 존재하지 않습니다.
          </span>

        </div>

        <button
          type="button"
          className="button button--secondary"
          onClick={handleBack}
        >
          프로젝트 목록
        </button>

      </div>

    );

  }


  /**
   * =====================================================
   * Render
   * =====================================================
   */

  return (

    <div className="page project-detail-page">

      {/* Page Header */}

      <div className="page__header project-detail-page__header">

        <div className="page__header-content">

          <div className="page__eyebrow">
            PROJECT DETAIL
          </div>

          <h1 className="page__title">
            프로젝트 상세
          </h1>

          <p className="page__description">
            프로젝트의 상세 정보를 확인합니다.
          </p>

        </div>


        {/* Header Actions */}

        <div className="project-detail-page__header-actions">

          <button
            type="button"
            className="button button--primary"
            onClick={handleChangeManagement}
            disabled={deleting}
          >
            변경 관리
          </button>

          <button
            type="button"
            className="button button--primary"
            onClick={handleEvidenceManagement}
            disabled={deleting}
          >
            증적 관리
          </button>

          <button
            type="button"
            className="button button--secondary"
            onClick={handleEdit}
            disabled={deleting}
          >
            수정
          </button>

          <button
            type="button"
            className="button button--secondary"
            onClick={handleBack}
            disabled={deleting}
          >
            ← 목록으로
          </button>

        </div>

      </div>


      {/* Delete Error */}

      {error && (

        <div
          className="state state--error project-detail-page__action-error"
          role="alert"
        >

          <span className="state__icon">
            !
          </span>

          <span className="state__description">
            {error}
          </span>

        </div>

      )}


      {/* Project Detail */}

      <ProjectDetail
        project={project}
        onDelete={handleDelete}
        deleting={deleting}
      />


      {/* Project Operation List */}

      <ProjectOperationList
        projectOperations={projectOperations}
        loading={projectOperationsLoading}
        error={projectOperationsError}
        isAdmin={isAdmin}
        onCreate={handleCreateProjectOperation}
        onEdit={handleEditProjectOperation}
        onStatusChange={handleProjectOperationStatusChange}
        onViewHandovers={handleViewHandovers}
      />


      {/* Project Operation Form Modal */}

      {showProjectOperationForm && (

        <div
          className="project-operation-dialog__backdrop"
          onMouseDown={(event) => {

            if (
              event.target === event.currentTarget &&
              !projectOperationSaving
            ) {

              handleCloseProjectOperationForm();

            }

          }}
        >

          <section
            className="project-operation-dialog"
            role="dialog"
            aria-modal="true"
            aria-labelledby="project-operation-dialog-title"
          >

            <div className="project-operation-dialog__header">

              <h2 id="project-operation-dialog-title">
                {selectedProjectOperation
                  ? '운영 서비스 연결 수정'
                  : '운영 서비스 연결'}
              </h2>

              <button
                type="button"
                className="project-operation-dialog__close"
                onClick={handleCloseProjectOperationForm}
                disabled={projectOperationSaving}
                aria-label="닫기"
              >
                ×
              </button>

            </div>


            <div className="project-operation-dialog__content">

              <ProjectOperationForm
                projectId={project.id}
                operations={operations}
                projectOperation={selectedProjectOperation}
                loading={operationsLoading}
                saving={projectOperationSaving}
                error={projectOperationFormError}
                onSubmit={handleProjectOperationSubmit}
                onCancel={handleCloseProjectOperationForm}
              />

            </div>

          </section>

        </div>

      )}

    </div>

  );

}


export default ProjectDetailPage;
