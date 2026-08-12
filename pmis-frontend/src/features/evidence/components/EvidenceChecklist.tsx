function EvidenceChecklist() {
  const evidences = [
    {
      name: "설치 작업 계획서",
      status: "검증완료",
    },
    {
      name: "HW 설치 결과서",
      status: "검증완료",
    },
    {
      name: "OS 설치 결과",
      status: "제출",
    },
    {
      name: "구성 정보",
      status: "검증 필요",
    },
    {
      name: "보안 설정 결과",
      status: "미제출",
    },
    {
      name: "검수 확인서",
      status: "미제출",
    },
  ];

  const submittedCount = evidences.filter(
    (evidence) =>
      evidence.status === "제출" ||
      evidence.status === "검증완료",
  ).length;

  return (
    <div
      style={{
        padding: "20px",
        backgroundColor: "#ffffff",
        border: "1px solid #dddddd",
        borderRadius: "6px",
      }}
    >
      {/* Header */}
      <div
        style={{
          marginBottom: "20px",
        }}
      >
        <h3
          style={{
            margin: 0,
            fontSize: "18px",
          }}
        >
          Evidence Checklist
        </h3>

        <p
          style={{
            marginTop: "8px",
            marginBottom: 0,
            color: "#666666",
          }}
        >
          대상: Server-001
        </p>
      </div>

      {/* Checklist */}
      <div>
        {evidences.map((evidence) => {
          const isVerified =
            evidence.status === "검증완료";

          const isSubmitted =
            evidence.status === "제출";

          const isPending =
            evidence.status === "검증 필요";

          return (
            <div
              key={evidence.name}
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "12px 0",
                borderBottom: "1px solid #eeeeee",
              }}
            >
              {/* Evidence Name */}
              <div
                style={{
                  display: "flex",
                  alignItems: "center",
                  gap: "8px",
                }}
              >
                <span
                  style={{
                    fontSize: "16px",
                  }}
                >
                  {isVerified && "☑"}
                  {isSubmitted && "☑"}
                  {isPending && "⚠"}
                  {evidence.status === "미제출" && "✕"}
                </span>

                <span>
                  {evidence.name}
                </span>
              </div>

              {/* Status */}
              <span
                style={{
                  fontSize: "14px",
                  color: isVerified
                    ? "#2e7d32"
                    : isSubmitted
                      ? "#1976d2"
                      : isPending
                        ? "#f57c00"
                        : "#d32f2f",
                  fontWeight: 500,
                }}
              >
                {evidence.status}
              </span>
            </div>
          );
        })}
      </div>

      {/* Summary */}
      <div
        style={{
          marginTop: "16px",
          paddingTop: "12px",
          color: "#666666",
          fontSize: "14px",
        }}
      >
        제출 {submittedCount} / 전체 {evidences.length}
      </div>
    </div>
  );
}

export default EvidenceChecklist;