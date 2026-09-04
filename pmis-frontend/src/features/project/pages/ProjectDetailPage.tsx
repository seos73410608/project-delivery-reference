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

import type {
  Project,
} from '../types/project';

import './ProjectDetailPage.css';


function ProjectDetailPage() {

  /**
   * =====================================================
   * Router
   * =====================================================
   */

  const {
    projectId,
  } = useParams<{
    projectId: string;
  }>();


  const navigate =
    useNavigate();


  /**
   * =====================================================
   * Project
   * =====================================================
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
   */

  const [
    loading,
    setLoading,
  ] = useState(true);


  /**
   * =====================================================
   * Deleting
   * =====================================================
   *
   * 프로젝트 삭제 처리 중 여부
   */

  const [
    deleting,
    setDeleting,
  ] = useState(false);


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
   * 프로젝트 상세 조회
   * =====================================================
   *
   * GET /api/projects/{id}/detail
   */

  const fetchProject =
    useCallback(
      async () => {

        /**
         * Project ID 확인
         */

        if (!projectId) {

          setProject(null);

          setError(
            '프로젝트 ID가 없습니다.',
          );

          setLoading(false);

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


          /**
           * GET /api/projects/{id}/detail
           */

          const response =
            await getProject(id);


          setProject(
            response,
          );

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
   * 최초 상세 조회
   * =====================================================
   */

  useEffect(() => {

    fetchProject();

  }, [
    fetchProject,
  ]);


  /**
   * =====================================================
   * 프로젝트 목록으로 이동
   * =====================================================
   */

  const handleBack = () => {

    navigate(
      '/project',
    );

  };


  /**
   * =====================================================
   * 프로젝트 수정 페이지 이동
   * =====================================================
   *
   * Frontend Route:
   *
   * /project/{projectId}/edit
   *
   * ProjectFormPage
   *     ↓
   * GET /api/projects/{projectId}/detail
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
   * 프로젝트 삭제
   * =====================================================
   *
   * DELETE /api/projects/{id}
   *
   * Backend 정책:
   * ADMIN 권한만 삭제 가능
   */

  const handleDelete = async () => {

    if (!project) {
      return;
    }


    /**
     * 삭제 확인
     */

    const confirmed =
      window.confirm(
        `프로젝트 "${project.projectName}"을(를) 삭제하시겠습니까?\n\n삭제한 프로젝트는 복구할 수 없습니다.`,
      );


    if (!confirmed) {
      return;
    }


    try {

      setDeleting(true);

      setError(null);


      /**
       * DELETE /api/projects/{id}
       */

      await deleteProject(
        project.id,
      );


      /**
       * 삭제 완료
       *
       * 프로젝트 목록으로 이동
       */

      navigate(
        '/project',
      );

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

      <div
        className="page project-detail-page"
      >

        <div
          className="state state--loading project-detail-page__loading"
        >

          <div
            className="spinner"
          />


          <span
            className="state__description"
          >
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

  if (
    error &&
    !project
  ) {

    return (

      <div
        className="page project-detail-page"
      >

        <div
          className="state state--error project-detail-page__error"
          role="alert"
        >

          <div
            className="state__icon"
          >
            !
          </div>


          <strong
            className="state__title"
          >
            프로젝트 정보를 불러올 수 없습니다.
          </strong>


          <span
            className="state__description"
          >
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

      <div
        className="page project-detail-page"
      >

        <div
          className="state state--error project-detail-page__error"
          role="alert"
        >

          <div
            className="state__icon"
          >
            !
          </div>


          <strong
            className="state__title"
          >
            프로젝트를 찾을 수 없습니다.
          </strong>


          <span
            className="state__description"
          >
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

    <div
      className="page project-detail-page"
    >


      {/* ================================================
          Page Header
          ================================================ */}

      <div
        className="page__header project-detail-page__header"
      >


        {/* Header Information */}

        <div
          className="page__header-content"
        >

          <div
            className="page__eyebrow"
          >
            PROJECT DETAIL
          </div>


          <h1
            className="page__title"
          >
            프로젝트 상세
          </h1>


          <p
            className="page__description"
          >
            프로젝트의 상세 정보를 확인합니다.
          </p>

        </div>


        {/* Header Actions */}

        <div
          className="project-detail-page__header-actions"
        >

          {/* Edit */}

          <button
            type="button"
            className="button button--primary"
            onClick={handleEdit}
            disabled={deleting}
          >
            수정
          </button>


          {/* Back */}

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


      {/* ================================================
          Delete Error
          ================================================ */}

      {error && (

        <div
          className="state state--error project-detail-page__action-error"
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


      {/* ================================================
          Project Detail
          ================================================ */}

      <ProjectDetail
        project={project}
        onDelete={handleDelete}
        deleting={deleting}
      />

    </div>

  );

}


export default ProjectDetailPage;