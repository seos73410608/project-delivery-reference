import {
  useCallback,
  useEffect,
  useState,
} from 'react';

import { useNavigate } from 'react-router-dom';

import ProjectList from '../components/ProjectList';
import ProjectSearch from '../components/ProjectSearch';

import { getProjects } from '../api/projectApi';

import type {
  Project,
  ProjectSearchParams,
} from '../types/project';

import './ProjectPage.css';


const DEFAULT_PAGE_SIZE = 10;


const INITIAL_SEARCH_PARAMS: ProjectSearchParams = {
  projectCode: undefined,
  projectName: undefined,
  customerName: undefined,
  status: undefined,
  priority: undefined,
  page: 0,
  size: DEFAULT_PAGE_SIZE,
};


function ProjectPage() {

  /**
   * Router
   */
  const navigate = useNavigate();


  /**
   * 프로젝트 목록
   */
  const [projects, setProjects] =
    useState<Project[]>([]);


  /**
   * 현재 검색 조건
   */
  const [searchParams, setSearchParams] =
    useState<ProjectSearchParams>(
      INITIAL_SEARCH_PARAMS,
    );


  /**
   * 현재 페이지
   */
  const [page, setPage] =
    useState(0);


  /**
   * 전체 페이지 수
   */
  const [totalPages, setTotalPages] =
    useState(0);


  /**
   * 전체 프로젝트 수
   */
  const [totalElements, setTotalElements] =
    useState(0);


  /**
   * Loading
   */
  const [loading, setLoading] =
    useState(false);


  /**
   * Error
   */
  const [error, setError] =
    useState<string | null>(null);


  /**
   * 프로젝트 목록 조회
   */
  const fetchProjects = useCallback(
    async (
      params: ProjectSearchParams,
    ) => {

      try {

        setLoading(true);
        setError(null);


        const response =
          await getProjects(params);


        setProjects(
          response.content,
        );


        setPage(
          response.number,
        );


        setTotalPages(
          response.totalPages,
        );


        setTotalElements(
          response.totalElements,
        );

      } catch (err) {

        console.error(
          '프로젝트 목록 조회 실패:',
          err,
        );


        setProjects([]);


        setError(
          '프로젝트 목록을 불러오지 못했습니다.',
        );

      } finally {

        setLoading(false);

      }

    },
    [],
  );


  /**
   * 최초 프로젝트 목록 조회
   */
  useEffect(() => {

    fetchProjects(
      INITIAL_SEARCH_PARAMS,
    );

  }, [fetchProjects]);


  /**
   * 프로젝트 등록 페이지 이동
   *
   * Frontend Route:
   * /project/create
   */
  const handleCreate = () => {

    navigate(
      '/project/create',
    );

  };


  /**
   * 프로젝트 상세 페이지 이동
   *
   * ProjectList에서 프로젝트를 선택하면
   * 프로젝트 상세 페이지로 이동한다.
   *
   * Backend API:
   * GET /api/projects/{id}/detail
   *
   * Frontend Route:
   * /project/{id}/detail
   */
  const handleProjectClick = (
    project: Project,
  ) => {

    navigate(
      `/project/${project.id}/detail`,
    );

  };


  /**
   * 검색
   */
  const handleSearch = (
    params: ProjectSearchParams,
  ) => {

    const newParams:
      ProjectSearchParams = {
        ...params,

        page: 0,

        size: DEFAULT_PAGE_SIZE,
      };


    setSearchParams(
      newParams,
    );


    fetchProjects(
      newParams,
    );

  };


  /**
   * 검색 조건 초기화
   */
  const handleReset = () => {

    setSearchParams(
      INITIAL_SEARCH_PARAMS,
    );


    fetchProjects(
      INITIAL_SEARCH_PARAMS,
    );

  };


  /**
   * 페이지 변경
   */
  const handlePageChange = (
    nextPage: number,
  ) => {

    if (
      nextPage < 0 ||
      nextPage >= totalPages
    ) {
      return;
    }


    const newParams:
      ProjectSearchParams = {
        ...searchParams,

        page: nextPage,

        size: DEFAULT_PAGE_SIZE,
      };


    setSearchParams(
      newParams,
    );


    fetchProjects(
      newParams,
    );

  };


  return (
    <div className="project-page">


      {/* ================================
          Page Header
          ================================ */}

      <section
        className="project-page__header"
      >

        <div>

          <div
            className="project-page__eyebrow"
          >
            PROJECT MANAGEMENT
          </div>


          <h1
            className="project-page__title"
          >
            프로젝트 관리
          </h1>


          <p
            className="project-page__description"
          >
            프로젝트 정보를 조회하고 관리합니다.
          </p>

        </div>


        {/* Header Actions */}

        <div
          className="project-page__header-actions"
        >

          {/* Summary */}

          <div
            className="project-page__summary"
          >

            <span
              className="project-page__summary-label"
            >
              전체 프로젝트
            </span>


            <strong
              className="project-page__summary-value"
            >
              {totalElements}
            </strong>


            <span
              className="project-page__summary-unit"
            >
              건
            </span>

          </div>


          {/* Create */}

          <button
            type="button"
            className="project-page__create-button"
            onClick={handleCreate}
          >
            + 프로젝트 등록
          </button>

        </div>

      </section>


      {/* ================================
          Search
          ================================ */}

      <section
        className="project-page__search"
      >

        <ProjectSearch
          onSearch={handleSearch}
          onReset={handleReset}
        />

      </section>


      {/* ================================
          Error
          ================================ */}

      {error && (

        <div
          className="project-page__error"
          role="alert"
        >

          <span
            className="project-page__error-icon"
          >
            !
          </span>


          <span>
            {error}
          </span>

        </div>

      )}


      {/* ================================
          Project List
          ================================ */}

      <section
        className="project-page__content"
      >

        <div
          className="project-page__content-header"
        >

          <div>

            <h2
              className="project-page__content-title"
            >
              프로젝트 목록
            </h2>


            <p
              className="project-page__content-description"
            >
              등록된 프로젝트를 조회할 수 있습니다.
            </p>

          </div>


          <div
            className="project-page__content-count"
          >
            총{' '}

            <strong>
              {totalElements}
            </strong>

            건
          </div>

        </div>


        <ProjectList
          projects={projects}
          loading={loading}
          onProjectClick={
            handleProjectClick
          }
        />

      </section>


      {/* ================================
          Pagination
          ================================ */}

      {!loading &&
        !error &&
        totalPages > 0 && (

          <div
            className="project-page__pagination"
          >

            {/* Previous */}

            <button
              type="button"
              className="project-page__pagination-button"
              onClick={() =>
                handlePageChange(
                  page - 1,
                )
              }
              disabled={
                page === 0
              }
            >
              이전
            </button>


            {/* Page Info */}

            <div
              className="project-page__pagination-info"
            >

              <strong>
                {page + 1}
              </strong>

              <span>
                /
              </span>

              <span>
                {totalPages}
              </span>

            </div>


            {/* Next */}

            <button
              type="button"
              className="project-page__pagination-button"
              onClick={() =>
                handlePageChange(
                  page + 1,
                )
              }
              disabled={
                page ===
                totalPages - 1
              }
            >
              다음
            </button>

          </div>

        )}

    </div>
  );
}


export default ProjectPage;