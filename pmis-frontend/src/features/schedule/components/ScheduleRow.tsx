import type {
  ScheduleResponse,
} from '../types/schedule';


interface ScheduleRowProps {

  schedule: ScheduleResponse;

  onSelect: (
    schedule: ScheduleResponse,
  ) => void;

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
 * → 10 days
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
 * Schedule 목록의 개별 Row
 * =====================================================
 *
 * Schedule 데이터를 화면에 표시하고
 * View / Edit / Delete Action을 상위 컴포넌트로 전달한다.
 */
const ScheduleRow = ({

  schedule,

  onSelect,
  onEdit,
  onDelete,

}: ScheduleRowProps) => {


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

    <tr
      className="schedule-row"
    >


      {/* ================================================
          WBS
          ================================================ */}

      <td
        className="schedule-row__wbs"
      >
        {schedule.wbsId}
      </td>


      {/* ================================================
          Schedule Name
          ================================================ */}

      <td
        className="schedule-row__name"
      >

        <button
          type="button"
          className="schedule-row__name-button"
          onClick={() =>
            onSelect(schedule)
          }
        >

          {schedule.scheduleName}

        </button>

      </td>


      {/* ================================================
          Start Date
          ================================================ */}

      <td
        className="schedule-row__date"
      >
        {schedule.startDate}
      </td>


      {/* ================================================
          End Date
          ================================================ */}

      <td
        className="schedule-row__date"
      >
        {schedule.endDate}
      </td>


      {/* ================================================
          Duration
          ================================================ */}

      <td
        className="schedule-row__duration"
      >
        {duration}일
      </td>


      {/* ================================================
          Status
          ================================================ */}

      <td
        className="schedule-row__status"
      >

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

      </td>


      {/* ================================================
          Sort Order
          ================================================ */}

      <td
        className="schedule-row__order"
      >
        {schedule.sortOrder}
      </td>


      {/* ================================================
          Actions
          ================================================ */}

      <td
        className="schedule-row__actions"
      >

        <div
          className="schedule-row__action-group"
        >


          {/* View */}

          <button
            type="button"
            className="button button--secondary button--sm"
            onClick={() =>
              onSelect(schedule)
            }
          >
            상세
          </button>


          {/* Edit */}

          <button
            type="button"
            className="button button--primary button--sm"
            onClick={() =>
              onEdit(schedule)
            }
          >
            수정
          </button>


          {/* Delete */}

          <button
            type="button"
            className="button button--danger button--sm"
            onClick={() =>
              onDelete(schedule)
            }
          >
            삭제
          </button>


        </div>

      </td>


    </tr>

  );

};


export default ScheduleRow;