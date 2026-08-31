import type { ScheduleResponse } from '../types/schedule';

interface ScheduleRowProps {
  schedule: ScheduleResponse;
  onSelect: (schedule: ScheduleResponse) => void;
  onEdit: (schedule: ScheduleResponse) => void;
  onDelete: (schedule: ScheduleResponse) => void;
};

/**
 * Schedule 기간 계산
 *
 * 시작일과 종료일을 포함한 일수로 계산한다.
 *
 * 예:
 * 2026-08-01 ~ 2026-08-10
 * → 10 days
 */
const getDuration = (
  startDate: string,
  endDate: string,
): number => {
  const start = new Date(startDate);
  const end = new Date(endDate);

  const diffTime = end.getTime() - start.getTime();

  return Math.floor(
    diffTime / (1000 * 60 * 60 * 24),
  ) + 1;
};

/**
 * Schedule 상태 표시명
 *
 * Backend ScheduleStatus Enum을
 * 화면 표시용 문자열로 변환한다.
 */
const getStatusLabel = (
  status: ScheduleResponse['status'],
): string => {
  switch (status) {
    case 'PLANNED':
      return 'Planned';

    case 'IN_PROGRESS':
      return 'In Progress';

    case 'COMPLETED':
      return 'Completed';

    case 'ON_HOLD':
      return 'On Hold';

    case 'CANCELLED':
      return 'Cancelled';

    default:
      return status;
  }
};

/**
 * Schedule 목록의 개별 Row
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
  const duration = getDuration(
    schedule.startDate,
    schedule.endDate,
  );

  return (
    <tr>
      {/* WBS */}
      <td>{schedule.wbsId}</td>

      {/* Schedule Name */}
      <td>{schedule.scheduleName}</td>

      {/* Start Date */}
      <td>{schedule.startDate}</td>

      {/* End Date */}
      <td>{schedule.endDate}</td>

      {/* Duration */}
      <td>{duration} days</td>

      {/* Status */}
      <td>{getStatusLabel(schedule.status)}</td>

      {/* Sort Order */}
      <td>{schedule.sortOrder}</td>

      {/* Actions */}
      <td>
        <button
          type="button"
          onClick={() => onSelect(schedule)}
        >
          View
        </button>

        <button
          type="button"
          onClick={() => onEdit(schedule)}
        >
          Edit
        </button>

        <button
          type="button"
          onClick={() => onDelete(schedule)}
        >
          Delete
        </button>
      </td>
    </tr>
  );
};

export default ScheduleRow;