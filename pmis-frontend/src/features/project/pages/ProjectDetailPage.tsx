import {
  useCallback,
  useEffect,
  useState,
} from 'react';

import {
  useNavigate,
  useParams,
} from 'react-router-dom';

import { getProject } from '../api/projectApi';
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
   * Loading
   * =====================================================
   */

  if (loading) {

    return (

      <div
        className="project-detail-page"
      >

        <div
          className="project-detail-page__loading"
        >

          <div
            className="project-detail-page__spinner"
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

  if (
    error ||
    !project
  ) {

    return (

      <div
        className="project-detail-page"
      >

        <div
          className="project-detail-page__error"
          role="alert"
        >

          <div
            className="project-detail-page__error-icon"
          >
            !
          </div>


          <strong>
            프로젝트 정보를 불러올 수 없습니다.
          </strong>


          <span>
            {
              error ??
              '프로젝트를 찾을 수 없습니다.'
            }
          </span>

        </div>


        <button
          type="button"
          className="project-detail-page__back-button"
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
      className="project-detail-page"
    >


      {/* ================================================
          Page Header
          ================================================ */}

      <div
        className="project-detail-page__header"
      >


        {/* Header Information */}

        <div>

          <div
            className="project-detail-page__eyebrow"
          >

            PROJECT DETAIL

          </div>


          <h1>
            프로젝트 상세
          </h1>


          <p>
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
            className="project-detail-page__edit-button"
            onClick={handleEdit}
          >

            수정

          </button>


          {/* Back */}

          <button
            type="button"
            className="project-detail-page__back-button"
            onClick={handleBack}
          >

            ← 목록으로

          </button>

        </div>

      </div>


      {/* ================================================
          Project Detail
          ================================================ */}

      <ProjectDetail
        project={project}
      />

    </div>

  );

}


export default ProjectDetailPage;