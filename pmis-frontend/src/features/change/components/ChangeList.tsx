import type {
  ChangePriority,
  ChangeResponse,
  ChangeStatus,
} from "@/features/change/types/change";
import ChangeRow from "./ChangeRow";

interface ChangeListProps {
  changes: ChangeResponse[];
  loading?: boolean;
  onChangeClick?: (change: ChangeResponse) => void;
  onStatusChange?: (
    change: ChangeResponse,
    status: ChangeStatus,
  ) => void;
  onPriorityChange?: (
    change: ChangeResponse,
    priority: ChangePriority,
  ) => void;
}

const columns = [
  {
    key: "request",
    label: "변경 요청",
    width: "minmax(220px, 2.5fr)",
  },
  {
    key: "type",
    label: "유형",
    width: "1fr",
  },
  {
    key: "impact",
    label: "영향도",
    width: "0.8fr",
  },
  {
    key: "priority",
    label: "우선순위",
    width: "0.9fr",
  },
  {
    key: "status",
    label: "상태",
    width: "1fr",
  },
  {
    key: "requestedDate",
    label: "요청일",
    width: "1fr",
  },
  {
    key: "dueDate",
    label: "완료 예정일",
    width: "1fr",
  },
];

const gridTemplateColumns = columns
  .map((column) => column.width)
  .join(" ");

export default function ChangeList({
  changes,
  loading = false,
  onChangeClick,
  onStatusChange,
  onPriorityChange,
}: ChangeListProps) {
  if (loading) {
    return (
      <div
        className="change-list__state change-list__state--loading"
        role="status"
        aria-live="polite"
      >
        변경 요청을 불러오는 중입니다.
      </div>
    );
  }

  if (changes.length === 0) {
    return (
      <div
        className="change-list__state change-list__state--empty"
        role="status"
      >
        조회된 변경 요청이 없습니다.
      </div>
    );
  }

  return (
    <div className="change-list">
      <div
        className="change-list__header"
        style={{
          gridTemplateColumns,
        }}
        role="row"
      >
        {columns.map((column) => (
          <div
            key={column.key}
            className="change-list__header-cell"
            role="columnheader"
          >
            {column.label}
          </div>
        ))}
      </div>

      <div className="change-list__body" role="rowgroup">
        {changes.map((change) => (
          <ChangeRow
            key={change.id}
            change={change}
            onClick={onChangeClick}
            onStatusChange={onStatusChange}
            onPriorityChange={onPriorityChange}
          />
        ))}
      </div>
    </div>
  );
}