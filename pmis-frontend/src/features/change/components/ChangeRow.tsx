import type {
  ChangeImpactLevel,
  ChangePriority,
  ChangeResponse,
  ChangeStatus,
  ChangeType,
} from "@/features/change/types/change";

interface ChangeRowProps {
  change: ChangeResponse;
  onClick?: (change: ChangeResponse) => void;
  onStatusChange?: (
    change: ChangeResponse,
    status: ChangeStatus,
  ) => void;
  onPriorityChange?: (
    change: ChangeResponse,
    priority: ChangePriority,
  ) => void;
}

const changeTypeLabels: Record<ChangeType, string> = {
  SCOPE: "범위",
  SCHEDULE: "일정",
  COST: "비용",
  QUALITY: "품질",
  TECHNICAL: "기술",
  INFRASTRUCTURE: "인프라",
  SECURITY: "보안",
  DATA: "데이터",
  INTERFACE: "인터페이스",
  OPERATION: "운영",
  REQUIREMENT: "요구사항",
  OTHER: "기타",
};

const impactLevelLabels: Record<ChangeImpactLevel, string> = {
  LOW: "낮음",
  MEDIUM: "보통",
  HIGH: "높음",
  CRITICAL: "치명적",
};

const priorityLabels: Record<ChangePriority, string> = {
  LOW: "낮음",
  MEDIUM: "보통",
  HIGH: "높음",
  CRITICAL: "긴급",
};

const statusLabels: Record<ChangeStatus, string> = {
  REQUESTED: "요청",
  ANALYZING: "분석 중",
  PENDING_APPROVAL: "승인 대기",
  APPROVED: "승인",
  REJECTED: "반려",
  IMPLEMENTING: "구현 중",
  VERIFIED: "검증 완료",
  CLOSED: "종료",
  CANCELLED: "취소",
};

const priorityClassNames: Record<ChangePriority, string> = {
  LOW: "change-row__badge--priority-low",
  MEDIUM: "change-row__badge--priority-medium",
  HIGH: "change-row__badge--priority-high",
  CRITICAL: "change-row__badge--priority-critical",
};

const statusClassNames: Record<ChangeStatus, string> = {
  REQUESTED: "change-row__badge--status-requested",
  ANALYZING: "change-row__badge--status-analyzing",
  PENDING_APPROVAL: "change-row__badge--status-pending-approval",
  APPROVED: "change-row__badge--status-approved",
  REJECTED: "change-row__badge--status-rejected",
  IMPLEMENTING: "change-row__badge--status-implementing",
  VERIFIED: "change-row__badge--status-verified",
  CLOSED: "change-row__badge--status-closed",
  CANCELLED: "change-row__badge--status-cancelled",
};

function formatDate(value?: string | null): string {
  if (!value) {
    return "-";
  }

  return value;
}

export default function ChangeRow({
  change,
  onClick,
}: ChangeRowProps) {
  const priorityClassName =
    priorityClassNames[change.priority];

  const statusClassName =
    statusClassNames[change.status];

  return (
    <div
      className="change-row"
      role="row"
      style={{
        gridTemplateColumns:
          "minmax(220px, 2.5fr) 1fr 0.8fr 0.9fr 1fr 1fr 1fr",
      }}
    >
      {/* 1. 변경 요청 */}
      <div
        className="change-row__request"
        role="gridcell"
      >
        <button
          type="button"
          className="change-row__title"
          onClick={() => onClick?.(change)}
        >
          <span className="change-row__key">
            {change.changeKey}
          </span>

          <span className="change-row__title-text">
            {change.title}
          </span>
        </button>
      </div>

      {/* 2. 유형 */}
      <div
        className="change-row__type"
        role="gridcell"
      >
        {changeTypeLabels[change.changeType]}
      </div>

      {/* 3. 영향도 */}
      <div
        className="change-row__impact"
        role="gridcell"
      >
        <span
          className={`change-row__impact-value change-row__impact-value--${change.impactLevel.toLowerCase()}`}
        >
          {impactLevelLabels[change.impactLevel]}
        </span>
      </div>

      {/* 4. 우선순위 */}
      <div
        className="change-row__priority"
        role="gridcell"
      >
        <span
          className={`change-row__badge ${priorityClassName}`}
        >
          {priorityLabels[change.priority]}
        </span>
      </div>

      {/* 5. 상태 */}
      <div
        className="change-row__status"
        role="gridcell"
      >
        <span
          className={`change-row__badge ${statusClassName}`}
        >
          {statusLabels[change.status]}
        </span>
      </div>

      {/* 6. 요청일 */}
      <div
        className="change-row__requested-date"
        role="gridcell"
      >
        {formatDate(change.requestedDate)}
      </div>

      {/* 7. 완료 예정일 */}
      <div
        className={`change-row__due-date${
          change.overdue
            ? " change-row__due-date--overdue"
            : ""
        }`}
        role="gridcell"
      >
        {formatDate(change.dueDate)}
      </div>
    </div>
  );
}