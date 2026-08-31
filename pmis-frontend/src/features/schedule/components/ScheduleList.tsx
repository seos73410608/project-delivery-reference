import type { ScheduleResponse } from '../types/schedule';
import ScheduleRow from './ScheduleRow';

interface ScheduleListProps {
  schedules: ScheduleResponse[];
  onSelect: (schedule: ScheduleResponse) => void;
  onEdit: (schedule: ScheduleResponse) => void;
  onDelete: (scheduleId: number) => void;
}

/**
 * Schedule 목록
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
  if (schedules.length === 0) {
    return (
      <div>
        <p>No schedules found.</p>
      </div>
    );
  }

  return (
    <div>
      <table>
        <thead>
          <tr>
            <th>WBS</th>
            <th>Schedule</th>
            <th>Start</th>
            <th>End</th>
            <th>Duration</th>
            <th>Status</th>
            <th>Order</th>
            <th>Action</th>
          </tr>
        </thead>

        <tbody>
          {schedules.map((schedule) => (
            <ScheduleRow
              key={schedule.id}
              schedule={schedule}
              onSelect={onSelect}
              onEdit={onEdit}
              onDelete={() => onDelete(schedule.id)}
            />
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ScheduleList;