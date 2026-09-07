import type {
  ScheduleResponse,
} from '../types/schedule';

import ScheduleRow from './ScheduleRow';


interface ScheduleListProps {

  schedules: ScheduleResponse[];

  onSelect: (
    schedule: ScheduleResponse,
  ) => void;

  onEdit: (
    schedule: ScheduleResponse,
  ) => void;

  onDelete: (
    scheduleId: number,
  ) => void;

}


/**
 * =====================================================
 * Schedule 목록
 * =====================================================
 *
 * Schedule 데이터를 Table 형태로 표시하고
 * 개별 Row의 사용자 Action을 상위 컴포넌트로 전달한다.
 */
const ScheduleList = ({

  schedules,

  onSelect,
  onEdit,
  onDelete,

}: ScheduleListProps) => {


  /**
   * =====================================================
   * Empty
   * =====================================================
   */
  if (schedules.length === 0) {

    return (

      <div
        className="card schedule-list"
      >

        <div
          className="schedule-list__empty"
        >

          <p>
            등록된 일정이 없습니다.
          </p>

          <span>
            검색 조건을 변경하거나 새로운 일정을 등록해주세요.
          </span>

        </div>

      </div>

    );

  }


  /**
   * =====================================================
   * Render
   * =====================================================
   */
  return (

    <section
      className="card schedule-list"
    >


      {/* ================================================
          Card Header
          ================================================ */}

      <div
        className="card__header schedule-list__header"
      >

        <div
          className="card__header-content"
        >

          <h2
            className="card__title"
          >
            일정 목록
          </h2>


          <p
            className="card__description"
          >
            프로젝트 일정 정보를 확인하고 관리합니다.
          </p>

        </div>


        <div
          className="card__meta"
        >

          총{' '}

          <strong>
            {schedules.length}
          </strong>

          건

        </div>

      </div>


      {/* ================================================
          Table
          ================================================ */}

      <div
        className="schedule-list__table-wrapper"
      >

        <table
          className="schedule-list__table"
        >

          <thead>

            <tr>

              <th>
                WBS
              </th>

              <th>
                일정명
              </th>

              <th>
                시작일
              </th>

              <th>
                종료일
              </th>

              <th>
                기간
              </th>

              <th>
                상태
              </th>

              <th>
                순서
              </th>

              <th>
                관리
              </th>

            </tr>

          </thead>


          <tbody>

            {schedules.map(
              (schedule) => (

                <ScheduleRow
                  key={schedule.id}
                  schedule={schedule}
                  onSelect={onSelect}
                  onEdit={onEdit}
                  onDelete={() =>
                    onDelete(schedule.id)
                  }
                />

              ),
            )}

          </tbody>

        </table>

      </div>

    </section>

  );

};


export default ScheduleList;