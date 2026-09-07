import type {
  ScheduleResponse,
} from '../types/schedule';


interface ScheduleDetailProps {

  schedule: ScheduleResponse | null;

  onEdit: (
    schedule: ScheduleResponse,
  ) => void;

  onDelete: (
    schedule: ScheduleResponse,
  ) => void;

}


/**
 * =====================================================
 * Schedule 기간 계산
 * =====================================================
 *
 * 시작일과 종료일을 포함한 일수로 계산한다.
 *
 * 예:
 *
 * 2026-08-01 ~ 2026-08-10
 *
 * → 10일
 */
const getDuration = (

  startDate: string,
  endDate: string,

): number => {

  const start =
    new Date(startDate);

  const end =
    new Date(endDate);


  const diffTime =
    end.getTime() -
    start.getTime();


  return (
    Math.floor(
      diffTime /
      (1000 * 60 * 60 * 24),
    ) + 1
  );

};


/**
 * =====================================================
 * Schedule 상태 표시명
 * =====================================================
 *
 * Backend ScheduleStatus Enum을
 * 화면 표시용 문자열로 변환한다.
 */
const getStatusLabel = (

  status: ScheduleResponse['status'],

): string => {

  switch (status) {

    case 'PLANNED':
      return '계획';


    case 'IN_PROGRESS':
      return '진행 중';


    case 'COMPLETED':
      return '완료';


    case 'ON_HOLD':
      return '보류';


    case 'CANCELLED':
      return '취소';


    default:
      return status;

  }

};


/**
 * =====================================================
 * Schedule Status Badge Class
 * =====================================================
 *
 * 공통 badge.css와 연결하기 위한
 * Status별 클래스 반환
 */
const getStatusClassName = (

  status: ScheduleResponse['status'],

): string => {

  switch (status) {

    case 'PLANNED':
      return 'badge badge--secondary';


    case 'IN_PROGRESS':
      return 'badge badge--primary';


    case 'COMPLETED':
      return 'badge badge--success';


    case 'ON_HOLD':
      return 'badge badge--warning';


    case 'CANCELLED':
      return 'badge badge--danger';


    default:
      return 'badge';

  }

};


/**
 * =====================================================
 * Schedule 상세 정보
 * =====================================================
 *
 * 선택된 Schedule의 상세 정보를 표시하고
 * 수정 / 삭제 Action을 상위 컴포넌트로 전달한다.
 */
const ScheduleDetail = ({

  schedule,

  onEdit,
  onDelete,

}: ScheduleDetailProps) => {


  /**
   * =====================================================
   * Empty State
   * =====================================================
   */

  if (!schedule) {

    return (

      <section
        className="schedule-detail"
      >

        <div
          className="schedule-detail__empty"
        >

          <p>
            목록에서 일정을 선택하면
            상세 정보를 확인할 수 있습니다.
          </p>

        </div>

      </section>

    );

  }


  /**
   * =====================================================
   * Duration
   * =====================================================
   */

  const duration =
    getDuration(
      schedule.startDate,
      schedule.endDate,
    );


  /**
   * =====================================================
   * Render
   * =====================================================
   */

  return (

    <section
      className="schedule-detail"
    >


      {/* ================================================
          Schedule Detail Header
          ================================================ */}

      <div
        className="schedule-detail__header"
      >


        <div
          className="schedule-detail__header-content"
        >

          <h2
            className="schedule-detail__title"
          >
            일정 상세
          </h2>


          <p
            className="schedule-detail__subtitle"
          >
            선택한 일정의 상세 정보를 확인합니다.
          </p>

        </div>


        {/* Status */}

        <span
          className={
            getStatusClassName(
              schedule.status,
            )
          }
        >

          {getStatusLabel(
            schedule.status,
          )}

        </span>


      </div>


      {/* ================================================
          Schedule Detail Body
          ================================================ */}

      <div
        className="schedule-detail__body"
      >


        {/* ================================================
            Detail Grid
            ================================================ */}

        <dl
          className="schedule-detail__grid"
        >


          {/* Schedule ID */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              Schedule ID
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.id}
            </dd>

          </div>


          {/* Project ID */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              Project ID
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.projectId}
            </dd>

          </div>


          {/* WBS ID */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              WBS ID
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.wbsId}
            </dd>

          </div>


          {/* Schedule Name */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              일정명
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.scheduleName}
            </dd>

          </div>


          {/* Start Date */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              시작일
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.startDate}
            </dd>

          </div>


          {/* End Date */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              종료일
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.endDate}
            </dd>

          </div>


          {/* Duration */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              기간
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {duration}일
            </dd>

          </div>


          {/* Sort Order */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              정렬 순서
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.sortOrder}
            </dd>

          </div>


          {/* Created At */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              생성일시
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.createdAt}
            </dd>

          </div>


          {/* Updated At */}

          <div
            className="schedule-detail__item"
          >

            <dt
              className="schedule-detail__label"
            >
              수정일시
            </dt>


            <dd
              className="schedule-detail__value"
            >
              {schedule.updatedAt}
            </dd>

          </div>


        </dl>


        {/* ================================================
            Description
            ================================================ */}

        <div
          className="schedule-detail__description"
        >

          <span
            className="schedule-detail__description-label"
          >
            설명
          </span>


          {schedule.description || '-'}


        </div>


      </div>


      {/* ================================================
          Actions
          ================================================ */}

      <div
        className="schedule-detail__actions"
      >


        {/* Edit */}

        <button
          type="button"
          className="button button--primary"
          onClick={() =>
            onEdit(schedule)
          }
        >
          수정
        </button>


        {/* Delete */}

        <button
          type="button"
          className="button button--danger"
          onClick={() =>
            onDelete(schedule)
          }
        >
          삭제
        </button>


      </div>


    </section>

  );

};


export default ScheduleDetail;