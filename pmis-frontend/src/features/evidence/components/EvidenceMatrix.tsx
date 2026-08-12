function EvidenceMatrix() {
  const evidenceMatrix = [
    {
      area: "구축",
      target: "Server-001",
      required: 6,
      submitted: 6,
      verified: 5,
    },
    {
      area: "구축",
      target: "Server-002",
      required: 6,
      submitted: 4,
      verified: 4,
    },
    {
      area: "DB",
      target: "DB-001",
      required: 5,
      submitted: 5,
      verified: 5,
    },
    {
      area: "이슈",
      target: "ISSUE-001",
      required: 2,
      submitted: 2,
      verified: 2,
    },
    {
      area: "이슈",
      target: "ISSUE-002",
      required: 2,
      submitted: 1,
      verified: 1,
    },
    {
      area: "변경",
      target: "CR-001",
      required: 4,
      submitted: 4,
      verified: 4,
    },
  ];

  const getStatus = (
    required: number,
    submitted: number,
    verified: number,
  ) => {
    if (verified === required) {
      return {
        icon: "✓",
        label: "완료",
        color: "#2e7d32",
      };
    }

    if (submitted === required) {
      return {
        icon: "⚠",
        label: "검증 필요",
        color: "#f57c00",
      };
    }

    return {
      icon: "✕",
      label: "미제출",
      color: "#d32f2f",
    };
  };

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
          Evidence Matrix
        </h3>

        <p
          style={{
            marginTop: "8px",
            marginBottom: 0,
            color: "#666666",
          }}
        >
          프로젝트 전체 증적 현황
        </p>
      </div>

      {/* Matrix Table */}
      <div
        style={{
          overflowX: "auto",
        }}
      >
        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
            fontSize: "14px",
          }}
        >
          <thead>
            <tr
              style={{
                backgroundColor: "#f5f5f5",
              }}
            >
              <th
                style={{
                  padding: "12px",
                  borderBottom: "1px solid #dddddd",
                  textAlign: "left",
                  whiteSpace: "nowrap",
                }}
              >
                관리영역
              </th>

              <th
                style={{
                  padding: "12px",
                  borderBottom: "1px solid #dddddd",
                  textAlign: "left",
                  whiteSpace: "nowrap",
                }}
              >
                대상
              </th>

              <th
                style={{
                  padding: "12px",
                  borderBottom: "1px solid #dddddd",
                  textAlign: "center",
                  whiteSpace: "nowrap",
                }}
              >
                필수
              </th>

              <th
                style={{
                  padding: "12px",
                  borderBottom: "1px solid #dddddd",
                  textAlign: "center",
                  whiteSpace: "nowrap",
                }}
              >
                제출
              </th>

              <th
                style={{
                  padding: "12px",
                  borderBottom: "1px solid #dddddd",
                  textAlign: "center",
                  whiteSpace: "nowrap",
                }}
              >
                검증
              </th>

              <th
                style={{
                  padding: "12px",
                  borderBottom: "1px solid #dddddd",
                  textAlign: "left",
                  whiteSpace: "nowrap",
                }}
              >
                상태
              </th>
            </tr>
          </thead>

          <tbody>
            {evidenceMatrix.map((item) => {
              const status = getStatus(
                item.required,
                item.submitted,
                item.verified,
              );

              return (
                <tr key={`${item.area}-${item.target}`}>
                  <td
                    style={{
                      padding: "12px",
                      borderBottom: "1px solid #eeeeee",
                    }}
                  >
                    {item.area}
                  </td>

                  <td
                    style={{
                      padding: "12px",
                      borderBottom: "1px solid #eeeeee",
                      fontWeight: 500,
                    }}
                  >
                    {item.target}
                  </td>

                  <td
                    style={{
                      padding: "12px",
                      borderBottom: "1px solid #eeeeee",
                      textAlign: "center",
                    }}
                  >
                    {item.required}
                  </td>

                  <td
                    style={{
                      padding: "12px",
                      borderBottom: "1px solid #eeeeee",
                      textAlign: "center",
                    }}
                  >
                    {item.submitted}
                  </td>

                  <td
                    style={{
                      padding: "12px",
                      borderBottom: "1px solid #eeeeee",
                      textAlign: "center",
                    }}
                  >
                    {item.verified}
                  </td>

                  <td
                    style={{
                      padding: "12px",
                      borderBottom: "1px solid #eeeeee",
                      color: status.color,
                      fontWeight: 500,
                      whiteSpace: "nowrap",
                    }}
                  >
                    {status.icon} {status.label}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Footer Summary */}
      <div
        style={{
          display: "flex",
          gap: "24px",
          marginTop: "16px",
          paddingTop: "16px",
          borderTop: "1px solid #eeeeee",
          color: "#666666",
          fontSize: "14px",
        }}
      >
        <span>
          전체 대상: {evidenceMatrix.length}
        </span>

        <span>
          완료:{" "}
          {
            evidenceMatrix.filter(
              (item) =>
                item.verified === item.required,
            ).length
          }
        </span>

        <span>
          미완료:{" "}
          {
            evidenceMatrix.filter(
              (item) =>
                item.verified !== item.required,
            ).length
          }
        </span>
      </div>
    </div>
  );
}

export default EvidenceMatrix;