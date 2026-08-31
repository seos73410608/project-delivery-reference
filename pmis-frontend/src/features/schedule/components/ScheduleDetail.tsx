import type { ScheduleResponse } from '../types/schedule';

interface ScheduleDetailProps {
  schedule: ScheduleResponse | null;
  onEdit: (schedule: ScheduleResponse) => void;
  onDelete: (schedule: ScheduleResponse) => void;
};

/**
 * Schedule 상세 정보
 *
 * 선택된 Schedule의 상세 정보를 표시하고
 * 수정/삭제 Action을 상위 컴포넌트로 전달한다.
 */
const ScheduleDetail = ({
  schedule,
  onEdit,
  onDelete,
}: ScheduleDetailProps) => {
  if (!schedule) {
    return (
      <div>
        <p>Select a schedule to view details.</p>
      </div>
    );
  }

  const start = new Date(schedule.startDate);
  const end = new Date(schedule.endDate);

  const duration =
    Math.floor(
      (end.getTime() - start.getTime()) /
        (1000 * 60 * 60 * 24),
    ) + 1;

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

  return (
    <section>
      <h2>Schedule Detail</h2>

      <dl>
        <dt>Schedule ID</dt>
        <dd>{schedule.id}</dd>

        <dt>Project ID</dt>
        <dd>{schedule.projectId}</dd>

        <dt>WBS ID</dt>
        <dd>{schedule.wbsId}</dd>

        <dt>Schedule Name</dt>
        <dd>{schedule.scheduleName}</dd>

        <dt>Start Date</dt>
        <dd>{schedule.startDate}</dd>

        <dt>End Date</dt>
        <dd>{schedule.endDate}</dd>

        <dt>Duration</dt>
        <dd>{duration} days</dd>

        <dt>Status</dt>
        <dd>{getStatusLabel(schedule.status)}</dd>

        <dt>Description</dt>
        <dd>{schedule.description || '-'}</dd>

        <dt>Sort Order</dt>
        <dd>{schedule.sortOrder}</dd>

        <dt>Created At</dt>
        <dd>{schedule.createdAt}</dd>

        <dt>Updated At</dt>
        <dd>{schedule.updatedAt}</dd>
      </dl>

      <div>
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
      </div>
    </section>
  );
};

export default ScheduleDetail;