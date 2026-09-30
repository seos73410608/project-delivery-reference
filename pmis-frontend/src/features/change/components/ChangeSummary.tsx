import type { ChangeResponse } from "../types/change";

interface ChangeSummaryProps {
  changes: ChangeResponse[];
}

interface SummaryItem {
  label: string;
  count: number;
  description: string;
  color: string;
  backgroundColor: string;
}

export default function ChangeSummary({ changes }: ChangeSummaryProps) {
  const totalCount = changes.length;

  const requestedCount = changes.filter(
    (change) => change.status === "REQUESTED",
  ).length;

  const analyzingCount = changes.filter(
    (change) => change.status === "ANALYZING",
  ).length;

  const pendingApprovalCount = changes.filter(
    (change) => change.status === "PENDING_APPROVAL",
  ).length;

  const implementingCount = changes.filter(
    (change) => change.status === "IMPLEMENTING",
  ).length;

  const completedCount = changes.filter(
    (change) =>
      change.status === "VERIFIED" || change.status === "CLOSED",
  ).length;

  const summaryItems: SummaryItem[] = [
    {
      label: "전체 변경 요청",
      count: totalCount,
      description: "현재 목록 기준",
      color: "#1f2937",
      backgroundColor: "#f3f4f6",
    },
    {
      label: "요청",
      count: requestedCount,
      description: "분석 전 요청",
      color: "#2563eb",
      backgroundColor: "#eff6ff",
    },
    {
      label: "분석 중",
      count: analyzingCount,
      description: "영향도 및 계획 검토",
      color: "#7c3aed",
      backgroundColor: "#f5f3ff",
    },
    {
      label: "승인 대기",
      count: pendingApprovalCount,
      description: "승인 판단 필요",
      color: "#b45309",
      backgroundColor: "#fffbeb",
    },
    {
      label: "이행 중",
      count: implementingCount,
      description: "승인 후 작업 진행",
      color: "#0891b2",
      backgroundColor: "#ecfeff",
    },
    {
      label: "검증 완료·종료",
      count: completedCount,
      description: "검증 완료 또는 종료",
      color: "#15803d",
      backgroundColor: "#f0fdf4",
    },
  ];

  return (
    <section
      aria-label="변경 요청 요약"
      className="change-summary"
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(150px, 1fr))",
        gap: "12px",
        width: "100%",
      }}
    >
      {summaryItems.map((item) => (
        <article
          key={item.label}
          className="change-summary__card"
          style={{
            display: "flex",
            flexDirection: "column",
            gap: "10px",
            minWidth: 0,
            padding: "16px",
            border: "1px solid #e5e7eb",
            borderRadius: "8px",
            backgroundColor: "#fff",
          }}
        >
          <div
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              gap: "8px",
            }}
          >
            <span
              style={{
                color: "#4b5563",
                fontSize: "13px",
                fontWeight: 500,
              }}
            >
              {item.label}
            </span>

            <span
              aria-hidden="true"
              style={{
                width: "8px",
                height: "8px",
                flex: "0 0 8px",
                borderRadius: "50%",
                backgroundColor: item.color,
              }}
            />
          </div>

          <strong
            style={{
              color: item.color,
              fontSize: "28px",
              fontWeight: 700,
              lineHeight: 1.2,
              fontVariantNumeric: "tabular-nums",
            }}
          >
            {item.count.toLocaleString("ko-KR")}
          </strong>

          <span
            style={{
              color: "#6b7280",
              fontSize: "12px",
              lineHeight: 1.4,
            }}
          >
            {item.description}
          </span>

          <div
            aria-hidden="true"
            style={{
              height: "3px",
              width: "100%",
              borderRadius: "999px",
              backgroundColor: item.backgroundColor,
              overflow: "hidden",
            }}
          >
            <div
              style={{
                width:
                  totalCount > 0
                    ? `${Math.min((item.count / totalCount) * 100, 100)}%`
                    : "0%",
                height: "100%",
                borderRadius: "999px",
                backgroundColor: item.color,
              }}
            />
          </div>
        </article>
      ))}
    </section>
  );
}