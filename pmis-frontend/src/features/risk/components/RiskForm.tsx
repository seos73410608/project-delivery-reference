import {
  useEffect,
  useState,
} from 'react';

import type {
  FormEvent,
} from 'react';

import type {
  RiskCreateRequest,
  RiskImpact,
  RiskPriority,
  RiskProbability,
  RiskResponse,
  RiskStatus,
  RiskUpdateRequest,
} from '../types/risk';


interface RiskFormProps {

  projectId: number;


  risk?:
    RiskResponse
    | null;


  onSubmit: (
    request:
      | RiskCreateRequest
      | RiskUpdateRequest,
  ) => void | Promise<void>;


  onCancel: () => void;

}


/**
 * =====================================================
 * Risk 생성 / 수정 Form
 * =====================================================
 */
const RiskForm = ({

  projectId,

  risk = null,

  onSubmit,
  onCancel,

}: RiskFormProps) => {


  /**
   * ===================================================
   * Edit Mode
   * ===================================================
   */
  const isEdit =
    Boolean(risk);


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
  ] = useState<RiskStatus>(
    'OPEN',
  );


  const [
    priority,
    setPriority,
  ] = useState<RiskPriority>(
    'MEDIUM',
  );


  const [
    probability,
    setProbability,
  ] = useState<RiskProbability>(
    'MEDIUM',
  );


  const [
    impact,
    setImpact,
  ] = useState<RiskImpact>(
    'MEDIUM',
  );


  const [
    assigneeId,
    setAssigneeId,
  ] = useState('');


  const [
    identifiedDate,
    setIdentifiedDate,
  ] = useState('');


  const [
    dueDate,
    setDueDate,
  ] = useState('');


  const [
    responsePlan,
    setResponsePlan,
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
   * Risk Data Initialize
   * ===================================================
   */
  useEffect(
    () => {

      /**
       * -----------------------------------------------
       * Edit Mode
       * -----------------------------------------------
       */
      if (risk) {

        setTitle(
          risk.title,
        );


        setDescription(
          risk.description ?? '',
        );


        setStatus(
          risk.status,
        );


        setPriority(
          risk.priority,
        );


        setProbability(
          risk.probability,
        );


        setImpact(
          risk.impact,
        );


        setAssigneeId(

          risk.assigneeId !== null &&
          risk.assigneeId !== undefined

            ? String(
                risk.assigneeId,
              )

            : '',

        );


        setIdentifiedDate(
          risk.identifiedDate ?? '',
        );


        setDueDate(
          risk.dueDate ?? '',
        );


        setResponsePlan(
          risk.responsePlan ?? '',
        );


        setSortOrder(

          risk.sortOrder !== null &&
          risk.sortOrder !== undefined

            ? String(
                risk.sortOrder,
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

      setStatus(
        'OPEN',
      );

      setPriority(
        'MEDIUM',
      );

      setProbability(
        'MEDIUM',
      );

      setImpact(
        'MEDIUM',
      );

      setAssigneeId('');

      setIdentifiedDate('');

      setDueDate('');

      setResponsePlan('');

      setSortOrder('');

      setError('');

    },
    [
      risk,
    ],
  );


  /**
   * ===================================================
   * Submit
   * ===================================================
   */
  const handleSubmit = async (

    event:
      FormEvent<
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
        'Risk 제목을 입력해주세요.',
      );

      return;

    }


    /**
     * -----------------------------------------------
     * Identified Date Validation
     * -----------------------------------------------
     */
    if (
      !identifiedDate
    ) {

      setError(
        'Risk 식별일을 입력해주세요.',
      );

      return;

    }


    /**
     * -----------------------------------------------
     * Due Date Validation
     * -----------------------------------------------
     */
    if (

      dueDate &&

      identifiedDate &&

      dueDate < identifiedDate

    ) {

      setError(
        '대응 목표일은 식별일보다 빠를 수 없습니다.',
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
     *
     * Backend-managed fields are intentionally
     * excluded:
     *
     * - id
     * - projectId
     * - riskKey
     * - reporterId
     * - mitigatedDate
     * - createdAt
     * - updatedAt
     * -----------------------------------------------
     */
    const request:
      | RiskCreateRequest
      | RiskUpdateRequest = {

      title:
        title.trim(),


      description:

        description.trim()
          || undefined,


      status,


      priority,


      probability,


      impact,


      ...(parsedAssigneeId !== undefined

        ? {

            assigneeId:
              parsedAssigneeId,

          }

        : {}),


      identifiedDate,


      dueDate:

        dueDate
          || undefined,


      responsePlan:

        responsePlan.trim()
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
    try {

      await onSubmit(
        request,
      );

    } catch (err) {

      console.error(
        'Failed to submit risk form.',
        err,
      );

      setError(
        isEdit
          ? 'Risk 수정에 실패했습니다.'
          : 'Risk 생성에 실패했습니다.',
      );

    }

  };


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <section
      className="risk-form"
    >


      {/* ================================================
          Header
          ================================================ */}

      <header
        className="
          risk-form__header
        "
      >

        <h2
          className="
            risk-form__title
          "
        >

          {isEdit
            ? 'Risk 수정'
            : 'Risk 생성'}

        </h2>


        <p
          className="
            risk-form__description
          "
        >

          {isEdit

            ? '선택한 Risk 정보를 수정합니다.'

            : '새로운 프로젝트 Risk를 등록합니다.'

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
            risk-form__body
          "
        >

          <div
            className="
              risk-form__grid
            "
          >


            {/* ============================================
                Project ID
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-project"
              >
                Project ID
              </label>


              <input
                id="risk-project"
                type="number"
                value={projectId}
                disabled
              />

            </div>


            {/* ============================================
                Risk Key
                ============================================ */}

            {isEdit && risk && (

              <div
                className="
                  risk-form__group
                "
              >

                <label
                  htmlFor="risk-key"
                >
                  Risk Key
                </label>


                <input
                  id="risk-key"
                  type="text"
                  value={
                    risk.riskKey ?? ''
                  }
                  disabled
                />

              </div>

            )}


            {/* ============================================
                Title
                ============================================ */}

            <div
              className="
                risk-form__group
                risk-form__group--full
              "
            >

              <label
                htmlFor="risk-title"
              >
                Risk Title
              </label>


              <input
                id="risk-title"
                type="text"
                value={title}
                placeholder="Risk 제목을 입력하세요."
                onChange={(
                  event,
                ) =>
                  setTitle(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* ============================================
                Status
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-status"
              >
                Status
              </label>


              <select
                id="risk-status"
                value={status}
                onChange={(
                  event,
                ) =>
                  setStatus(
                    event.target.value as RiskStatus,
                  )
                }
              >

                <option value="OPEN">
                  신규
                </option>


                <option value="IN_PROGRESS">
                  진행 중
                </option>


                <option value="MITIGATED">
                  완화
                </option>


                <option value="CLOSED">
                  종료
                </option>


                <option value="ACCEPTED">
                  수용
                </option>


                <option value="CANCELLED">
                  취소
                </option>

              </select>

            </div>


            {/* ============================================
                Priority
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-priority"
              >
                Priority
              </label>


              <select
                id="risk-priority"
                value={priority}
                onChange={(
                  event,
                ) =>
                  setPriority(
                    event.target.value as RiskPriority,
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


            {/* ============================================
                Probability
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-probability"
              >
                Probability
              </label>


              <select
                id="risk-probability"
                value={probability}
                onChange={(
                  event,
                ) =>
                  setProbability(
                    event.target.value as RiskProbability,
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

              </select>

            </div>


            {/* ============================================
                Impact
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-impact"
              >
                Impact
              </label>


              <select
                id="risk-impact"
                value={impact}
                onChange={(
                  event,
                ) =>
                  setImpact(
                    event.target.value as RiskImpact,
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


            {/* ============================================
                Assignee
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-assignee-id"
              >
                Assignee ID
              </label>


              <input
                id="risk-assignee-id"
                type="number"
                min="1"
                value={assigneeId}
                placeholder="담당자 ID"
                onChange={(
                  event,
                ) =>
                  setAssigneeId(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* ============================================
                Identified Date
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-identified-date"
              >
                Identified Date
              </label>


              <input
                id="risk-identified-date"
                type="date"
                value={identifiedDate}
                onChange={(
                  event,
                ) =>
                  setIdentifiedDate(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* ============================================
                Due Date
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-due-date"
              >
                Due Date
              </label>


              <input
                id="risk-due-date"
                type="date"
                value={dueDate}
                onChange={(
                  event,
                ) =>
                  setDueDate(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* ============================================
                Sort Order
                ============================================ */}

            <div
              className="
                risk-form__group
              "
            >

              <label
                htmlFor="risk-sort-order"
              >
                Sort Order
              </label>


              <input
                id="risk-sort-order"
                type="number"
                min="0"
                value={sortOrder}
                placeholder="예: 1"
                onChange={(
                  event,
                ) =>
                  setSortOrder(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* ============================================
                Response Plan
                ============================================ */}

            <div
              className="
                risk-form__group
                risk-form__group--full
              "
            >

              <label
                htmlFor="risk-response-plan"
              >
                Response Plan
              </label>


              <textarea
                id="risk-response-plan"
                value={responsePlan}
                placeholder="Risk 대응 계획을 입력하세요."
                rows={5}
                onChange={(
                  event,
                ) =>
                  setResponsePlan(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* ============================================
                Description
                ============================================ */}

            <div
              className="
                risk-form__group
                risk-form__group--full
              "
            >

              <label
                htmlFor="risk-description"
              >
                Description
              </label>


              <textarea
                id="risk-description"
                value={description}
                placeholder="Risk 상세 내용을 입력하세요."
                rows={5}
                onChange={(
                  event,
                ) =>
                  setDescription(
                    event.target.value,
                  )
                }
              />

            </div>


            {/* ============================================
                Error
                ============================================ */}

            {error && (

              <div
                className="
                  risk-form__error
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
              risk-form__actions
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


export default RiskForm;