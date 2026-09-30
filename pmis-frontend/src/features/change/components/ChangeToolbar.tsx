import type { ChangePriority, ChangeStatus } from "../types/change";

type FilterValue<T extends string> = T | "ALL";

interface ChangeToolbarProps {
  searchText: string;
  status: FilterValue<ChangeStatus>;
  priority: FilterValue<ChangePriority>;

  onSearchTextChange: (value: string) => void;
  onStatusChange: (value: FilterValue<ChangeStatus>) => void;
  onPriorityChange: (value: FilterValue<ChangePriority>) => void;
  onCreateClick: () => void;
}

const statusOptions: { value: ChangeStatus; label: string }[] = [
  { value: "REQUESTED", label: "요청" },
  { value: "ANALYZING", label: "분석 중" },
  { value: "PENDING_APPROVAL", label: "승인 대기" },
  { value: "APPROVED", label: "승인" },
  { value: "REJECTED", label: "반려" },
  { value: "IMPLEMENTING", label: "이행 중" },
  { value: "VERIFIED", label: "검증 완료" },
  { value: "CLOSED", label: "종료" },
  { value: "CANCELLED", label: "취소" },
];

const priorityOptions: { value: ChangePriority; label: string }[] = [
  { value: "LOW", label: "낮음" },
  { value: "MEDIUM", label: "보통" },
  { value: "HIGH", label: "높음" },
  { value: "CRITICAL", label: "긴급" },
];

export default function ChangeToolbar({
  searchText,
  status,
  priority,
  onSearchTextChange,
  onStatusChange,
  onPriorityChange,
  onCreateClick,
}: ChangeToolbarProps) {
  const handleReset = () => {
    onSearchTextChange("");
    onStatusChange("ALL");
    onPriorityChange("ALL");
  };

  return (
    <section
      aria-label="변경 요청 검색 및 필터"
      className="change-toolbar"
      style={{
        display: "flex",
        flexWrap: "wrap",
        alignItems: "center",
        justifyContent: "space-between",
        gap: "12px",
        width: "100%",
      }}
    >
      <div
        className="change-toolbar__filters"
        style={{
          display: "flex",
          flex: "1 1 600px",
          flexWrap: "wrap",
          alignItems: "center",
          gap: "8px",
        }}
      >
        <label
          htmlFor="change-search"
          style={{
            position: "absolute",
            width: "1px",
            height: "1px",
            padding: 0,
            margin: "-1px",
            overflow: "hidden",
            clip: "rect(0, 0, 0, 0)",
            whiteSpace: "nowrap",
            border: 0,
          }}
        >
          변경 요청 검색
        </label>

        <input
          id="change-search"
          type="search"
          value={searchText}
          onChange={(event) => onSearchTextChange(event.target.value)}
          placeholder="변경 요청 제목 또는 내용 검색"
          className="change-toolbar__search"
          style={{
            flex: "1 1 240px",
            minWidth: "200px",
            maxWidth: "360px",
            height: "40px",
            padding: "0 12px",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
            fontSize: "14px",
          }}
        />

        <label
          htmlFor="change-status-filter"
          style={{ fontSize: "13px", color: "#4b5563" }}
        >
          상태
        </label>
        <select
          id="change-status-filter"
          value={status}
          onChange={(event) =>
            onStatusChange(event.target.value as FilterValue<ChangeStatus>)
          }
          className="change-toolbar__select"
          style={{
            height: "40px",
            minWidth: "132px",
            padding: "0 10px",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
            backgroundColor: "#fff",
            fontSize: "14px",
          }}
        >
          <option value="ALL">전체 상태</option>
          {statusOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>

        <label
          htmlFor="change-priority-filter"
          style={{ fontSize: "13px", color: "#4b5563" }}
        >
          우선순위
        </label>
        <select
          id="change-priority-filter"
          value={priority}
          onChange={(event) =>
            onPriorityChange(event.target.value as FilterValue<ChangePriority>)
          }
          className="change-toolbar__select"
          style={{
            height: "40px",
            minWidth: "120px",
            padding: "0 10px",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
            backgroundColor: "#fff",
            fontSize: "14px",
          }}
        >
          <option value="ALL">전체 우선순위</option>
          {priorityOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>

        <button
          type="button"
          onClick={handleReset}
          className="change-toolbar__reset"
          style={{
            height: "40px",
            padding: "0 12px",
            border: "1px solid #d1d5db",
            borderRadius: "6px",
            backgroundColor: "#fff",
            color: "#374151",
            fontSize: "14px",
            cursor: "pointer",
          }}
        >
          초기화
        </button>
      </div>

      <button
        type="button"
        onClick={onCreateClick}
        className="change-toolbar__create"
        style={{
          height: "40px",
          padding: "0 16px",
          border: "1px solid #2563eb",
          borderRadius: "6px",
          backgroundColor: "#2563eb",
          color: "#fff",
          fontSize: "14px",
          fontWeight: 600,
          cursor: "pointer",
          whiteSpace: "nowrap",
        }}
      >
        + 변경 요청 등록
      </button>
    </section>
  );
}