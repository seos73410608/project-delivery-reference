import { useEffect, useState } from 'react';

import type {
  ScheduleCreateRequest,
  ScheduleResponse,
  ScheduleStatus,
  ScheduleUpdateRequest,
} from '../types/schedule';


interface ScheduleFormProps {

  projectId: number;

  schedule?: ScheduleResponse | null;

  onSubmit: (
    request:
      | ScheduleCreateRequest
      | ScheduleUpdateRequest,
  ) => void;

  onCancel: () => void;

}


/**
 * =====================================================
 * Schedule Status 변환
 * =====================================================
 *
 * HTML Select의 value는 string이므로
 * ScheduleStatus 타입으로 안전하게 변환한다.
 */
const toScheduleStatus = (
  value: string,
): ScheduleStatus => {

  switch (value) {

    case 'PLANNED':
      return 'PLANNED';

    case 'IN_PROGRESS':
      return 'IN_PROGRESS';

    case 'COMPLETED':
      return 'COMPLETED';

    case 'ON_HOLD':
      return 'ON_HOLD';

    case 'CANCELLED':
      return 'CANCELLED';

    default:
      return 'PLANNED';

  }

};


/**
 * =====================================================
 * Schedule 생성 / 수정 Form
 * =====================================================
 *
 * Schedule 생성과 수정에 공통으로 사용하는 Form이다.
 *
 * API 호출은 상위 SchedulePage에서 처리한다.
 *
 * Form은 다음 기능을 담당한다.
 *
 * - 사용자 입력
 * - 생성 / 수정 데이터 초기화
 * - 기본 Validation
 * - 상위 컴포넌트 Submit 호출
 */
const ScheduleForm = ({

  projectId,

  schedule = null,

  onSubmit,
  onCancel,

}: ScheduleFormProps) => {


  /* ===================================================
     State
     =================================================== */

  const [wbsId, setWbsId] =
    useState('');


  const [scheduleName, setScheduleName] =
    useState('');


  const [startDate, setStartDate] =
    useState('');


  const [endDate, setEndDate] =
    useState('');


  const [status, setStatus] =
    useState<ScheduleStatus>(
      'PLANNED',
    );


  const [description, setDescription] =
    useState('');


  const [sortOrder, setSortOrder] =
    useState('');


  const [error, setError] =
    useState('');


  /**
   * ===================================================
   * Edit Mode
   * ===================================================
   */
  const isEdit =
    Boolean(schedule);


  /* ===================================================
     Schedule Data Initialize
     =================================================== */

  /**
   * 수정 모드
   *
   * 선택된 Schedule 데이터를
   * Form State에 반영한다.
   *
   * 생성 모드
   *
   * Form State를 초기화한다.
   */
  useEffect(() => {


    /* ===============================================
       Edit Mode
       =============================================== */

    if (schedule) {

      setWbsId(
        String(schedule.wbsId),
      );


      setScheduleName(
        schedule.scheduleName,
      );


      setStartDate(
        schedule.startDate,
      );


      setEndDate(
        schedule.endDate,
      );


      setStatus(
        schedule.status,
      );


      setDescription(
        schedule.description ?? '',
      );


      setSortOrder(
        String(schedule.sortOrder),
      );


      setError('');

      return;

    }


    /* ===============================================
       Create Mode
       =============================================== */

    setWbsId('');

    setScheduleName('');

    setStartDate('');

    setEndDate('');

    setStatus('PLANNED');

    setDescription('');

    setSortOrder('');

    setError('');


  }, [schedule]);


  /* ===================================================
     Submit
     =================================================== */

  /**
   * Schedule 생성 / 수정
   *
   * 기본적인 사용자 입력 Validation 후
   * 상위 컴포넌트의 onSubmit을 호출한다.
   */
  const handleSubmit = (

    event:
      React.FormEvent<HTMLFormElement>,

  ) => {

    event.preventDefault();


    /* ===============================================
       Error Reset
       =============================================== */

    setError('');


    /* ===============================================
       WBS ID Validation
       =============================================== */

    if (!wbsId) {

      setError(
        'WBS ID를 입력해주세요.',
      );

      return;

    }


    /* ===============================================
       Schedule Name Validation
       =============================================== */

    if (!scheduleName.trim()) {

      setError(
        '일정명을 입력해주세요.',
      );

      return;

    }


    /* ===============================================
       Start Date Validation
       =============================================== */

    if (!startDate) {

      setError(
        '시작일을 입력해주세요.',
      );

      return;

    }


    /* ===============================================
       End Date Validation
       =============================================== */

    if (!endDate) {

      setError(
        '종료일을 입력해주세요.',
      );

      return;

    }


    /* ===============================================
       Date Range Validation
       =============================================== */

    if (startDate > endDate) {

      setError(
        '시작일은 종료일보다 늦을 수 없습니다.',
      );

      return;

    }


    /* ===============================================
       WBS ID Parsing
       =============================================== */

    const parsedWbsId =
      Number(wbsId);


    if (

      !Number.isInteger(parsedWbsId) ||

      parsedWbsId <= 0

    ) {

      setError(
        'WBS ID는 1 이상의 정수여야 합니다.',
      );

      return;

    }


    /* ===============================================
       Sort Order Parsing
       =============================================== */

    let parsedSortOrder:
      | number
      | undefined;


    if (sortOrder !== '') {

      parsedSortOrder =
        Number(sortOrder);


      if (

        !Number.isInteger(
          parsedSortOrder,
        ) ||

        parsedSortOrder < 0

      ) {

        setError(
          'Sort Order는 0 이상의 정수여야 합니다.',
        );

        return;

      }

    }


    /* ===============================================
       Request
       =============================================== */

    const request:
      | ScheduleCreateRequest
      | ScheduleUpdateRequest = {

      wbsId:
        parsedWbsId,


      scheduleName:
        scheduleName.trim(),


      startDate,


      endDate,


      status,


      description:

        description.trim() ||

        undefined,


      ...(parsedSortOrder !== undefined

        ? {

            sortOrder:
              parsedSortOrder,

          }

        : {}),

    };


    /* ===============================================
       Submit
       =============================================== */

    onSubmit(request);

  };


  /* ===================================================
     Render
     =================================================== */

  return (

    <section
      className="schedule-form"
    >


      {/* ===============================================
          Form Header
          =============================================== */}

      <header
        className="schedule-form__header"
      >

        <h2
          className="schedule-form__title"
        >

          {isEdit
            ? '일정 수정'
            : '일정 생성'}

        </h2>


        <p
          className="schedule-form__description"
        >

          {isEdit

            ? '선택한 프로젝트 일정을 수정합니다.'

            : '새로운 프로젝트 일정을 등록합니다.'

          }

        </p>

      </header>


      {/* ===============================================
          Form
          =============================================== */}

      <form
        onSubmit={handleSubmit}
      >


        {/* =============================================
            Form Body
            ============================================= */}

        <div
          className="schedule-form__body"
        >


          {/* ===========================================
              Form Grid
              =========================================== */}

          <div
            className="schedule-form__grid"
          >


            {/* =========================================
                Project ID
                ========================================= */}

            <div
              className="schedule-form__group"
            >

              <label
                htmlFor="schedule-project"
              >
                Project ID
              </label>


              <input
                id="schedule-project"
                type="number"
                value={projectId}
                disabled
              />

            </div>


            {/* =========================================
                WBS ID
                ========================================= */}

            <div
              className="schedule-form__group"
            >

              <label
                htmlFor="schedule-wbs"
              >
                WBS ID
              </label>


              <input
                id="schedule-wbs"
                type="number"
                min="1"
                value={wbsId}
                onChange={(event) => {

                  setWbsId(
                    event.target.value,
                  );

                }}
              />

            </div>


            {/* =========================================
                Schedule Name
                ========================================= */}

            <div
              className="
                schedule-form__group
                schedule-form__group--full
              "
            >

              <label
                htmlFor="schedule-name"
              >
                Schedule Name
              </label>


              <input
                id="schedule-name"
                type="text"
                value={scheduleName}
                placeholder="일정명을 입력하세요."
                onChange={(event) => {

                  setScheduleName(
                    event.target.value,
                  );

                }}
              />

            </div>


            {/* =========================================
                Start Date
                ========================================= */}

            <div
              className="schedule-form__group"
            >

              <label
                htmlFor="schedule-start-date"
              >
                Start Date
              </label>


              <input
                id="schedule-start-date"
                type="date"
                value={startDate}
                onChange={(event) => {

                  setStartDate(
                    event.target.value,
                  );

                }}
              />

            </div>


            {/* =========================================
                End Date
                ========================================= */}

            <div
              className="schedule-form__group"
            >

              <label
                htmlFor="schedule-end-date"
              >
                End Date
              </label>


              <input
                id="schedule-end-date"
                type="date"
                value={endDate}
                onChange={(event) => {

                  setEndDate(
                    event.target.value,
                  );

                }}
              />

            </div>


            {/* =========================================
                Status
                ========================================= */}

            <div
              className="schedule-form__group"
            >

              <label
                htmlFor="schedule-status"
              >
                Status
              </label>


              <select
                id="schedule-status"
                value={status}
                onChange={(event) => {

                  setStatus(
                    toScheduleStatus(
                      event.target.value,
                    ),
                  );

                }}
              >

                <option
                  value="PLANNED"
                >
                  계획
                </option>


                <option
                  value="IN_PROGRESS"
                >
                  진행 중
                </option>


                <option
                  value="COMPLETED"
                >
                  완료
                </option>


                <option
                  value="ON_HOLD"
                >
                  보류
                </option>


                <option
                  value="CANCELLED"
                >
                  취소
                </option>

              </select>

            </div>


            {/* =========================================
                Sort Order
                ========================================= */}

            <div
              className="schedule-form__group"
            >

              <label
                htmlFor="schedule-sort-order"
              >
                Sort Order
              </label>


              <input
                id="schedule-sort-order"
                type="number"
                min="0"
                value={sortOrder}
                placeholder="예: 1"
                onChange={(event) => {

                  setSortOrder(
                    event.target.value,
                  );

                }}
              />

            </div>


            {/* =========================================
                Description
                ========================================= */}

            <div
              className="
                schedule-form__group
                schedule-form__group--full
              "
            >

              <label
                htmlFor="schedule-description"
              >
                Description
              </label>


              <textarea
                id="schedule-description"
                value={description}
                placeholder="일정 설명을 입력하세요."
                rows={5}
                onChange={(event) => {

                  setDescription(
                    event.target.value,
                  );

                }}
              />

            </div>


            {/* =========================================
                Error
                ========================================= */}

            {error && (

              <div
                className="schedule-form__error"
                role="alert"
              >

                {error}

              </div>

            )}


          </div>


          {/* =============================================
              Form Actions
              ============================================= */}

          <div
            className="schedule-form__actions"
          >


            {/* Cancel */}

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


            {/* Submit */}

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


export default ScheduleForm;