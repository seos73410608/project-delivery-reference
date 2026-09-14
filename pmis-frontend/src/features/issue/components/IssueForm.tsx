import {
  useEffect,
  useState,
} from 'react';

import type {
  IssueCreateRequest,
  IssuePriority,
  IssueResponse,
  IssueStatus,
  IssueUpdateRequest,
} from '../types/issue';


interface IssueFormProps {

  projectId: number;


  issue?:
    IssueResponse
    | null;


  onSubmit: (
    request:
      | IssueCreateRequest
      | IssueUpdateRequest,
  ) => void | Promise<void>;


  onCancel: () => void;

}


/**
 * =====================================================
 * Issue Status 변환
 * =====================================================
 */
const toIssueStatus = (

  value: string,

): IssueStatus => {

  switch (value) {

    case 'OPEN':
      return 'OPEN';

    case 'IN_PROGRESS':
      return 'IN_PROGRESS';

    case 'RESOLVED':
      return 'RESOLVED';

    case 'CLOSED':
      return 'CLOSED';

    case 'ON_HOLD':
      return 'ON_HOLD';

    case 'CANCELLED':
      return 'CANCELLED';

    default:
      return 'OPEN';

  }

};


/**
 * =====================================================
 * Issue Priority 변환
 * =====================================================
 */
const toIssuePriority = (

  value: string,

): IssuePriority => {

  switch (value) {

    case 'LOW':
      return 'LOW';

    case 'MEDIUM':
      return 'MEDIUM';

    case 'HIGH':
      return 'HIGH';

    case 'CRITICAL':
      return 'CRITICAL';

    default:
      return 'MEDIUM';

  }

};


/**
 * =====================================================
 * Issue 생성 / 수정 Form
 * =====================================================
 */
const IssueForm = ({

  projectId,

  issue = null,

  onSubmit,
  onCancel,

}: IssueFormProps) => {


  /**
   * ===================================================
   * State
   * ===================================================
   */

  const [
    title,
    setTitle,
  ] = useState('');


  const [
    description,
    setDescription,
  ] = useState('');


  const [
    status,
    setStatus,
  ] = useState<IssueStatus>(
    'OPEN',
  );


  const [
    priority,
    setPriority,
  ] = useState<IssuePriority>(
    'MEDIUM',
  );


  const [
    assigneeId,
    setAssigneeId,
  ] = useState('');


  const [
    occurredDate,
    setOccurredDate,
  ] = useState('');


  const [
    dueDate,
    setDueDate,
  ] = useState('');


  const [
    sortOrder,
    setSortOrder,
  ] = useState('');


  const [
    error,
    setError,
  ] = useState('');


  /**
   * ===================================================
   * Edit Mode
   * ===================================================
   */
  const isEdit =
    Boolean(issue);


  /**
   * ===================================================
   * Issue Data Initialize
   * ===================================================
   */
  useEffect(
    () => {

      /**
       * -----------------------------------------------
       * Edit Mode
       * -----------------------------------------------
       */
      if (issue) {

        setTitle(
          issue.title,
        );


        setDescription(
          issue.description ?? '',
        );


        setStatus(
          issue.status,
        );


        setPriority(
          issue.priority,
        );


        setAssigneeId(

          issue.assigneeId !== null &&
          issue.assigneeId !== undefined

            ? String(
                issue.assigneeId,
              )

            : '',

        );


        setOccurredDate(
          issue.occurredDate ?? '',
        );


        setDueDate(
          issue.dueDate ?? '',
        );


        setSortOrder(

          issue.sortOrder !== null &&
          issue.sortOrder !== undefined

            ? String(
                issue.sortOrder,
              )

            : '',

        );


        setError('');

        return;

      }


      /**
       * -----------------------------------------------
       * Create Mode
       * -----------------------------------------------
       */

      setTitle('');

      setDescription('');

      setStatus('OPEN');

      setPriority('MEDIUM');

      setAssigneeId('');

      setOccurredDate('');

      setDueDate('');

      setSortOrder('');

      setError('');


    },
    [
      issue,
    ],
  );


  /**
   * ===================================================
   * Submit
   * ===================================================
   */
  const handleSubmit = (

    event:
      React.FormEvent<
        HTMLFormElement
      >,

  ) => {

    event.preventDefault();


    setError('');


    /**
     * -----------------------------------------------
     * Title Validation
     * -----------------------------------------------
     */
    if (
      !title.trim()
    ) {

      setError(
        'Issue 제목을 입력해주세요.',
      );

      return;

    }


    /**
     * -----------------------------------------------
     * Occurred Date Validation
     * -----------------------------------------------
     */
    if (
      !occurredDate
    ) {

      setError(
        'Issue 발생일을 입력해주세요.',
      );

      return;

    }


    /**
     * -----------------------------------------------
     * Assignee ID Parsing
     * -----------------------------------------------
     */
    let parsedAssigneeId:
      | number
      | undefined;


    if (
      assigneeId !== ''
    ) {

      parsedAssigneeId =
        Number(
          assigneeId,
        );


      if (

        !Number.isInteger(
          parsedAssigneeId,
        )

        ||

        parsedAssigneeId <= 0

      ) {

        setError(
          '담당자 ID는 1 이상의 정수여야 합니다.',
        );

        return;

      }

    }


    /**
     * -----------------------------------------------
     * Sort Order Parsing
     * -----------------------------------------------
     */
    let parsedSortOrder:
      | number
      | undefined;


    if (
      sortOrder !== ''
    ) {

      parsedSortOrder =
        Number(
          sortOrder,
        );


      if (

        !Number.isInteger(
          parsedSortOrder,
        )

        ||

        parsedSortOrder < 0

      ) {

        setError(
          'Sort Order는 0 이상의 정수여야 합니다.',
        );

        return;

      }

    }


    /**
     * -----------------------------------------------
     * Request
     * -----------------------------------------------
     */
    const request:
      | IssueCreateRequest
      | IssueUpdateRequest = {

      title:
        title.trim(),


      description:

        description.trim()
          || undefined,


      status,


      priority,


      ...(parsedAssigneeId !== undefined

        ? {

            assigneeId:
              parsedAssigneeId,

          }

        : {}),


      occurredDate,


      dueDate:

        dueDate
          || undefined,


      ...(parsedSortOrder !== undefined

        ? {

            sortOrder:
              parsedSortOrder,

          }

        : {}),

    };


    /**
     * -----------------------------------------------
     * Submit
     * -----------------------------------------------
     */
    void onSubmit(
      request,
    );

  };


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <section
      className="issue-form"
    >


      {/* ================================================
          Header
          ================================================ */}

      <header
        className="
          issue-form__header
        "
      >

        <h2
          className="
            issue-form__title
          "
        >

          {isEdit
            ? 'Issue 수정'
            : 'Issue 생성'}

        </h2>


        <p
          className="
            issue-form__description
          "
        >

          {isEdit
            ? '선택한 Issue 정보를 수정합니다.'
            : '새로운 프로젝트 Issue를 등록합니다.'
          }

        </p>

      </header>


      {/* ================================================
          Form
          ================================================ */}

      <form
        onSubmit={handleSubmit}
      >

        <div
          className="
            issue-form__body
          "
        >

          <div
            className="
              issue-form__grid
            "
          >


            {/* Project ID */}

            <div
              className="
                issue-form__group
              "
            >

              <label
                htmlFor="issue-project"
              >
                Project ID
              </label>


              <input
                id="issue-project"
                type="number"
                value={projectId}
                disabled
              />

            </div>


            {/* Issue Key */}

            {isEdit && issue && (

              <div
                className="
                  issue-form__group
                "
              >

                <label
                  htmlFor="issue-key"
                >
                  Issue Key
                </label>


                <input
                  id="issue-key"
                  type="text"
                  value={issue.issueKey}
                  disabled
                />

              </div>

            )}


            {/* Title */}

            <div
              className="
                issue-form__group
                issue-form__group--full
              "
            >

              <label
                htmlFor="issue-title"
              >
                Issue Title
              </label>


              <input
                id="issue-title"
                type="text"
                value={title}
                placeholder="Issue 제목을 입력하세요."
                onChange={(event) =>
                  setTitle(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* Status */}

            <div
              className="
                issue-form__group
              "
            >

              <label
                htmlFor="issue-status"
              >
                Status
              </label>


              <select
                id="issue-status"
                value={status}
                onChange={(event) =>
                  setStatus(
                    toIssueStatus(
                      event.target.value,
                    ),
                  )
                }
              >

                <option value="OPEN">
                  신규
                </option>


                <option value="IN_PROGRESS">
                  진행 중
                </option>


                <option value="RESOLVED">
                  해결
                </option>


                <option value="CLOSED">
                  종료
                </option>


                <option value="ON_HOLD">
                  보류
                </option>


                <option value="CANCELLED">
                  취소
                </option>

              </select>

            </div>


            {/* Priority */}

            <div
              className="
                issue-form__group
              "
            >

              <label
                htmlFor="issue-priority"
              >
                Priority
              </label>


              <select
                id="issue-priority"
                value={priority}
                onChange={(event) =>
                  setPriority(
                    toIssuePriority(
                      event.target.value,
                    ),
                  )
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


            {/* Assignee */}

            <div
              className="
                issue-form__group
              "
            >

              <label
                htmlFor="issue-assignee-id"
              >
                Assignee ID
              </label>


              <input
                id="issue-assignee-id"
                type="number"
                min="1"
                value={assigneeId}
                placeholder="담당자 ID"
                onChange={(event) =>
                  setAssigneeId(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* Occurred Date */}

            <div
              className="
                issue-form__group
              "
            >

              <label
                htmlFor="issue-occurred-date"
              >
                Occurred Date
              </label>


              <input
                id="issue-occurred-date"
                type="date"
                value={occurredDate}
                onChange={(event) =>
                  setOccurredDate(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* Due Date */}

            <div
              className="
                issue-form__group
              "
            >

              <label
                htmlFor="issue-due-date"
              >
                Due Date
              </label>


              <input
                id="issue-due-date"
                type="date"
                value={dueDate}
                onChange={(event) =>
                  setDueDate(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* Sort Order */}

            <div
              className="
                issue-form__group
              "
            >

              <label
                htmlFor="issue-sort-order"
              >
                Sort Order
              </label>


              <input
                id="issue-sort-order"
                type="number"
                min="0"
                value={sortOrder}
                placeholder="예: 1"
                onChange={(event) =>
                  setSortOrder(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* Description */}

            <div
              className="
                issue-form__group
                issue-form__group--full
              "
            >

              <label
                htmlFor="issue-description"
              >
                Description
              </label>


              <textarea
                id="issue-description"
                value={description}
                placeholder="Issue 상세 내용을 입력하세요."
                rows={5}
                onChange={(event) =>
                  setDescription(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* Error */}

            {error && (

              <div
                className="
                  issue-form__error
                "
                role="alert"
              >
                {error}
              </div>

            )}


          </div>


          {/* ==============================================
              Actions
              ============================================== */}

          <div
            className="
              issue-form__actions
            "
          >

            <button
              type="button"
              className="
                button
                button--secondary
                button--md
              "
              onClick={onCancel}
            >
              취소
            </button>


            <button
              type="submit"
              className="
                button
                button--primary
                button--md
              "
            >

              {isEdit
                ? '수정'
                : '생성'}

            </button>

          </div>


        </div>

      </form>

    </section>

  );

};


export default IssueForm;