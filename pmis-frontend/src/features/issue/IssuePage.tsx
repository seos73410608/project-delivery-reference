import {
  useCallback,
  useEffect,
  useState,
} from 'react';

import {
  createIssue,
  deleteIssue,
  getIssue,
  getProjectIssues,
  searchIssues,
  updateIssue,
} from './api/issueApi';

import IssueDetail from './components/IssueDetail';
import IssueForm from './components/IssueForm';
import IssueList from './components/IssueList';
import IssueToolbar from './components/IssueToolbar';

import type {
  IssueCreateRequest,
  IssuePriority,
  IssueResponse,
  IssueSearchParams,
  IssueStatus,
  IssueUpdateRequest,
} from './types/issue';

import './styles/Issue.css';


/**
 * =====================================================
 * Current Project ID
 * =====================================================
 *
 * 현재 선택된 프로젝트 ID
 *
 * TODO:
 *
 * 향후 Project Context 또는
 * Route Parameter 기반으로 변경한다.
 */
const PROJECT_ID = 2;


/**
 * =====================================================
 * Issue Page
 * =====================================================
 *
 * Issue Domain의 전체 화면을 담당한다.
 *
 * 주요 책임:
 *
 * - 프로젝트 Issue 목록 조회
 * - Issue 검색
 * - Issue 상세 조회
 * - Issue 생성
 * - Issue 수정
 * - Issue 삭제
 * - Issue 선택 상태 관리
 * - Issue Form 상태 관리
 */
const IssuePage = () => {


  /**
   * =====================================================
   * State
   * =====================================================
   */


  /**
   * -----------------------------------------------------
   * Issue 목록
   * -----------------------------------------------------
   */
  const [
    issues,
    setIssues,
  ] = useState<
    IssueResponse[]
  >([]);


  /**
   * -----------------------------------------------------
   * 현재 선택된 Issue
   * -----------------------------------------------------
   */
  const [
    selectedIssue,
    setSelectedIssue,
  ] = useState<
    IssueResponse | null
  >(null);


  /**
   * -----------------------------------------------------
   * 수정 중인 Issue
   * -----------------------------------------------------
   */
  const [
    editingIssue,
    setEditingIssue,
  ] = useState<
    IssueResponse | null
  >(null);


  /**
   * -----------------------------------------------------
   * Issue 생성 화면 표시 여부
   * -----------------------------------------------------
   */
  const [
    isCreating,
    setIsCreating,
  ] = useState(false);


  /**
   * -----------------------------------------------------
   * 검색 키워드
   * -----------------------------------------------------
   */
  const [
    keyword,
    setKeyword,
  ] = useState('');


  /**
   * -----------------------------------------------------
   * Issue 상태 검색 조건
   * -----------------------------------------------------
   */
  const [
    status,
    setStatus,
  ] = useState<
    IssueStatus | ''
  >('');


  /**
   * -----------------------------------------------------
   * Issue 중요도 검색 조건
   * -----------------------------------------------------
   */
  const [
    priority,
    setPriority,
  ] = useState<
    IssuePriority | ''
  >('');


  /**
   * -----------------------------------------------------
   * Loading 상태
   * -----------------------------------------------------
   */
  const [
    loading,
    setLoading,
  ] = useState(false);


  /**
   * -----------------------------------------------------
   * Error Message
   * -----------------------------------------------------
   */
  const [
    error,
    setError,
  ] = useState('');


  /**
   * =====================================================
   * 프로젝트 Issue 목록 조회
   * =====================================================
   *
   * GET
   *
   * /api/projects/{projectId}/issues
   */
  const loadIssues =
    useCallback(
      async () => {

        setLoading(true);

        setError('');


        try {

          const data =
            await getProjectIssues(
              PROJECT_ID,
            );


          setIssues(
            data,
          );


          setSelectedIssue(
            null,
          );


        } catch (err) {

          console.error(
            'Failed to load issues.',
            err,
          );


          setError(
            'Issue 목록을 불러오지 못했습니다.',
          );


        } finally {

          setLoading(false);

        }

      },
      [],
    );


  /**
   * =====================================================
   * 최초 Issue 목록 조회
   * =====================================================
   */
  useEffect(
    () => {

      void loadIssues();

    },
    [
      loadIssues,
    ],
  );


  /**
   * =====================================================
   * Issue 검색
   * =====================================================
   *
   * GET
   *
   * /api/issues
   *
   * Query Parameters:
   *
   * - projectId
   * - keyword
   * - status
   * - priority
   * - page
   * - size
   */
  const handleSearch =
    async () => {

      setLoading(true);

      setError('');


      try {

        const params:
          IssueSearchParams = {

          projectId:
            PROJECT_ID,


          page:
            0,


          size:
            20,

        };


        /**
         * -----------------------------------------------
         * Keyword
         * -----------------------------------------------
         */
        if (
          keyword.trim()
        ) {

          params.keyword =
            keyword.trim();

        }


        /**
         * -----------------------------------------------
         * Status
         * -----------------------------------------------
         */
        if (
          status
        ) {

          params.status =
            status;

        }


        /**
         * -----------------------------------------------
         * Priority
         * -----------------------------------------------
         */
        if (
          priority
        ) {

          params.priority =
            priority;

        }


        /**
         * -----------------------------------------------
         * Search API
         * -----------------------------------------------
         */
        const response =
          await searchIssues(
            params,
          );


        /**
         * -----------------------------------------------
         * Issue 목록 변경
         * -----------------------------------------------
         */
        setIssues(
          response.content,
        );


        /**
         * -----------------------------------------------
         * 선택 상태 초기화
         * -----------------------------------------------
         */
        setSelectedIssue(
          null,
        );


        /**
         * -----------------------------------------------
         * Form 상태 초기화
         * -----------------------------------------------
         */
        setEditingIssue(
          null,
        );


        setIsCreating(
          false,
        );


      } catch (err) {

        console.error(
          'Failed to search issues.',
          err,
        );


        setError(
          'Issue 검색에 실패했습니다.',
        );


      } finally {

        setLoading(false);

      }

    };


  /**
   * =====================================================
   * Issue 상세 조회
   * =====================================================
   *
   * GET
   *
   * /api/issues/{id}
   */
  const handleSelect =
    async (
      issue: IssueResponse,
    ) => {

      setError('');


      try {

        const data =
          await getIssue(
            issue.id,
          );


        setSelectedIssue(
          data,
        );


        /**
         * 상세 조회 시
         * 생성 / 수정 Form 닫기
         */
        setEditingIssue(
          null,
        );


        setIsCreating(
          false,
        );


      } catch (err) {

        console.error(
          'Failed to load issue detail.',
          err,
        );


        setError(
          'Issue 상세 정보를 불러오지 못했습니다.',
        );

      }

    };


  /**
   * =====================================================
   * Issue 생성 화면 열기
   * =====================================================
   */
  const handleCreateOpen =
    () => {

      setSelectedIssue(
        null,
      );


      setEditingIssue(
        null,
      );


      setIsCreating(
        true,
      );


      setError('');

    };


  /**
   * =====================================================
   * Issue 생성
   * =====================================================
   *
   * POST
   *
   * /api/projects/{projectId}/issues
   */
  const handleCreate =
    async (
      request:
        IssueCreateRequest,
    ) => {

      setLoading(true);

      setError('');


      try {

        /**
         * -----------------------------------------------
         * Create API
         * -----------------------------------------------
         */
        const created =
          await createIssue(
            PROJECT_ID,
            request,
          );


        /**
         * -----------------------------------------------
         * 목록에 생성 Issue 추가
         * -----------------------------------------------
         */
        setIssues(
          (current) => [

            ...current,

            created,

          ],
        );


        /**
         * -----------------------------------------------
         * 생성된 Issue 선택
         * -----------------------------------------------
         */
        setSelectedIssue(
          created,
        );


        /**
         * -----------------------------------------------
         * Form 상태 초기화
         * -----------------------------------------------
         */
        setEditingIssue(
          null,
        );


        setIsCreating(
          false,
        );


      } catch (err) {

        console.error(
          'Failed to create issue.',
          err,
        );


        setError(
          'Issue 생성에 실패했습니다.',
        );


      } finally {

        setLoading(false);

      }

    };


  /**
   * =====================================================
   * Issue 수정 화면 열기
   * =====================================================
   */
  const handleEdit =
    (
      issue:
        IssueResponse,
    ) => {

      setSelectedIssue(
        issue,
      );


      setEditingIssue(
        issue,
      );


      setIsCreating(
        false,
      );


      setError('');

    };


  /**
   * =====================================================
   * Issue 수정
   * =====================================================
   *
   * PUT
   *
   * /api/issues/{id}
   */
  const handleUpdate =
    async (

      issueId:
        number,

      request:
        IssueUpdateRequest,

    ) => {

      setLoading(true);

      setError('');


      try {

        /**
         * -----------------------------------------------
         * Update API
         * -----------------------------------------------
         */
        const updated =
          await updateIssue(
            issueId,
            request,
          );


        /**
         * -----------------------------------------------
         * Issue 목록 갱신
         * -----------------------------------------------
         */
        setIssues(
          (current) =>

            current.map(
              (issue) =>

                issue.id === updated.id

                  ? updated

                  : issue,

            ),

        );


        /**
         * -----------------------------------------------
         * 선택 Issue 갱신
         * -----------------------------------------------
         */
        setSelectedIssue(
          updated,
        );


        /**
         * -----------------------------------------------
         * 수정 상태 종료
         * -----------------------------------------------
         */
        setEditingIssue(
          null,
        );


        setIsCreating(
          false,
        );


      } catch (err) {

        console.error(
          'Failed to update issue.',
          err,
        );


        setError(
          'Issue 수정에 실패했습니다.',
        );


      } finally {

        setLoading(false);

      }

    };


  /**
   * =====================================================
   * Issue 삭제
   * =====================================================
   *
   * DELETE
   *
   * /api/issues/{id}
   */
  const handleDelete =
    async (
      issueId:
        number,
    ) => {


      /**
       * -----------------------------------------------
       * 삭제 대상 Issue 조회
       * -----------------------------------------------
       */
      const issue =
        issues.find(
          (item) =>

            item.id === issueId,

        );


      /**
       * -----------------------------------------------
       * Issue 존재 여부 확인
       * -----------------------------------------------
       */
      if (!issue) {

        setError(
          '삭제할 Issue를 찾을 수 없습니다.',
        );

        return;

      }


      /**
       * -----------------------------------------------
       * 삭제 확인
       * -----------------------------------------------
       */
      const confirmed =
        window.confirm(
          `"${issue.title}" Issue를 삭제하시겠습니까?`,
        );


      if (!confirmed) {

        return;

      }


      setLoading(true);

      setError('');


      try {

        /**
         * -----------------------------------------------
         * Delete API
         * -----------------------------------------------
         */
        await deleteIssue(
          issueId,
        );


        /**
         * -----------------------------------------------
         * 목록에서 삭제
         * -----------------------------------------------
         */
        setIssues(
          (current) =>

            current.filter(
              (item) =>

                item.id !== issueId,

            ),

        );


        /**
         * -----------------------------------------------
         * 상태 초기화
         * -----------------------------------------------
         */
        setSelectedIssue(
          null,
        );


        setEditingIssue(
          null,
        );


        setIsCreating(
          false,
        );


      } catch (err) {

        console.error(
          'Failed to delete issue.',
          err,
        );


        setError(
          'Issue 삭제에 실패했습니다.',
        );


      } finally {

        setLoading(false);

      }

    };


  /**
   * =====================================================
   * Form 닫기
   * =====================================================
   */
  const handleFormClose =
    () => {

      setIsCreating(
        false,
      );


      setEditingIssue(
        null,
      );


      setError('');

    };


  /**
   * =====================================================
   * Render
   * =====================================================
   */
  return (

    <div
      className="page issue-page"
    >


      {/* ================================================
          Page Header
          ================================================ */}

      <div
        className="
          page__header
          issue-page__header
        "
      >

        <div
          className="
            page__header-content
          "
        >

          <div
            className="
              page__eyebrow
            "
          >
            ISSUE MANAGEMENT
          </div>


          <h1
            className="
              page__title
            "
          >
            Issue
          </h1>


          <p
            className="
              page__description
            "
          >
            프로젝트 수행 중 발생한 이슈를
            등록하고 관리합니다.
          </p>

        </div>


        {/* -----------------------------------------------
            Summary
            ----------------------------------------------- */}

        <div
          className="
            issue-page__summary
          "
        >

          <span
            className="
              issue-page__summary-label
            "
          >
            Total
          </span>


          <strong
            className="
              issue-page__summary-value
            "
          >
            {issues.length}
          </strong>


          <span
            className="
              issue-page__summary-unit
            "
          >
            issues
          </span>

        </div>

      </div>


      {/* ================================================
          Search / Filter
          ================================================ */}

      <div
        className="
          issue-page__toolbar
        "
      >

        <IssueToolbar

          keyword={keyword}

          status={status}

          priority={priority}


          onKeywordChange={
            setKeyword
          }


          onStatusChange={
            setStatus
          }


          onPriorityChange={
            setPriority
          }


          onSearch={
            handleSearch
          }


          onCreate={
            handleCreateOpen
          }

        />

      </div>


      {/* ================================================
          Error
          ================================================ */}

      {error && (

        <div
          className="
            issue-page__error
          "
          role="alert"
        >

          <span
            className="
              issue-page__error-icon
            "
          >
            !
          </span>


          <span>
            {error}
          </span>

        </div>

      )}


      {/* ================================================
          Loading
          ================================================ */}

      {loading && (

        <div
          className="
            issue-page__loading
          "
        >

          <div
            className="spinner"
          />


          <span>
            Issue 정보를 처리하는 중입니다...
          </span>

        </div>

      )}


      {/* ================================================
          Issue List
          ================================================ */}

      {!loading && (

        <div
          className="
            issue-page__content
          "
        >

          <div
            className="
              issue-page__content-header
            "
          >

            <div>

              <h2
                className="
                  issue-page__content-title
                "
              >
                Issue List
              </h2>


              <p
                className="
                  issue-page__content-description
                "
              >
                프로젝트에 등록된 Issue 목록입니다.
              </p>

            </div>


            <span
              className="
                issue-page__content-count
              "
            >

              Total&nbsp;

              <strong>
                {issues.length}
              </strong>

            </span>

          </div>


          <IssueList

            issues={issues}

            onSelect={
              handleSelect
            }

            onEdit={
              handleEdit
            }

            onDelete={
              handleDelete
            }

          />

        </div>

      )}


      {/* ================================================
          Issue Detail
          ================================================ */}

      <div
        className="
          issue-page__detail
        "
      >

        <IssueDetail

          issue={
            selectedIssue
          }


          onEdit={
            handleEdit
          }


          onDelete={
            (issue) =>

              handleDelete(
                issue.id,
              )
          }

        />

      </div>


      {/* ================================================
          Issue Create / Update
          ================================================ */}

      {(

        isCreating ||

        editingIssue

      ) && (

        <div
          className="
            issue-page__form
          "
        >

          <IssueForm

            projectId={
              PROJECT_ID
            }


            issue={
              editingIssue
            }


            onSubmit={
              async (
                request,
              ) => {

                /**
                 * ---------------------------------------
                 * Edit Mode
                 * ---------------------------------------
                 */
                if (
                  editingIssue
                ) {

                  await handleUpdate(

                    editingIssue.id,

                    request as
                      IssueUpdateRequest,

                  );

                  return;

                }


                /**
                 * ---------------------------------------
                 * Create Mode
                 * ---------------------------------------
                 */
                await handleCreate(

                  request as
                    IssueCreateRequest,

                );

              }
            }


            onCancel={
              handleFormClose
            }

          />

        </div>

      )}


    </div>

  );

};


export default IssuePage;