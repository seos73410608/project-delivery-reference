import type { ReactNode } from "react";

interface EmptyStateProps {
  title?: string;
  message?: string;
  action?: ReactNode;
}

function EmptyState({
  title = "데이터가 없습니다.",
  message = "표시할 데이터가 없습니다.",
  action,
}: EmptyStateProps) {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "180px",
        padding: "32px 20px",
        textAlign: "center",
        color: "#666666",
      }}
    >
      <div
        aria-hidden="true"
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          width: "48px",
          height: "48px",
          marginBottom: "16px",
          borderRadius: "50%",
          backgroundColor: "#f0f0f0",
          color: "#999999",
          fontSize: "24px",
        }}
      >
        −
      </div>

      <h2
        style={{
          margin: "0 0 8px",
          fontSize: "16px",
          fontWeight: "600",
          color: "#333333",
        }}
      >
        {title}
      </h2>

      <p
        style={{
          margin: "0 0 16px",
          fontSize: "14px",
          lineHeight: 1.5,
          color: "#666666",
        }}
      >
        {message}
      </p>

      {action && <div>{action}</div>}
    </div>
  );
}

export default EmptyState;