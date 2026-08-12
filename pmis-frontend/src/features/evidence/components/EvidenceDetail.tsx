function EvidenceDetail() {
  return (
    <div
      style={{
        backgroundColor: "#ffffff",
        border: "1px solid #e0e0e0",
        borderRadius: "8px",
        padding: "20px",
      }}
    >
      {/* Header */}
      <div
        style={{
          marginBottom: "20px",
        }}
      >
        <h2
          style={{
            margin: 0,
            fontSize: "18px",
          }}
        >
          Evidence Detail
        </h2>

        <p
          style={{
            marginTop: "6px",
            marginBottom: 0,
            color: "#666666",
            fontSize: "14px",
          }}
        >
          Selected evidence information
        </p>
      </div>

      {/* Evidence Information */}
      <div
        style={{
          marginBottom: "20px",
        }}
      >
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "120px 1fr",
            rowGap: "12px",
            fontSize: "14px",
          }}
        >
          <strong>Evidence ID</strong>
          <span>EV-2026-001</span>

          <strong>Evidence Type</strong>
          <span>Installation Result</span>

          <strong>Management Area</strong>
          <span>Infrastructure Construction</span>

          <strong>Target</strong>
          <span>Server-001</span>

          <strong>Status</strong>
          <span
            style={{
              display: "inline-block",
              width: "fit-content",
              padding: "4px 8px",
              borderRadius: "4px",
              backgroundColor: "#e8f5e9",
              color: "#2e7d32",
              fontSize: "12px",
              fontWeight: 600,
            }}
          >
            VERIFIED
          </span>

          <strong>Submitted By</strong>
          <span>홍길동</span>

          <strong>Submitted Date</strong>
          <span>2026-08-11</span>

          <strong>Verified Date</strong>
          <span>2026-08-12</span>
        </div>
      </div>

      {/* File Information */}
      <div
        style={{
          borderTop: "1px solid #eeeeee",
          paddingTop: "20px",
        }}
      >
        <h3
          style={{
            marginTop: 0,
            marginBottom: "12px",
            fontSize: "15px",
          }}
        >
          Attached Evidence
        </h3>

        <div
          style={{
            padding: "12px",
            border: "1px solid #e0e0e0",
            borderRadius: "6px",
            backgroundColor: "#fafafa",
          }}
        >
          <div
            style={{
              fontWeight: 600,
              marginBottom: "6px",
            }}
          >
            server-001-installation-result.pdf
          </div>

          <div
            style={{
              color: "#666666",
              fontSize: "13px",
            }}
          >
            PDF · 2.4 MB
          </div>
        </div>
      </div>

      {/* Verification */}
      <div
        style={{
          marginTop: "20px",
          borderTop: "1px solid #eeeeee",
          paddingTop: "20px",
        }}
      >
        <h3
          style={{
            marginTop: 0,
            marginBottom: "12px",
            fontSize: "15px",
          }}
        >
          Verification
        </h3>

        <div
          style={{
            display: "grid",
            gridTemplateColumns: "120px 1fr",
            rowGap: "12px",
            fontSize: "14px",
          }}
        >
          <strong>Verifier</strong>
          <span>김PM</span>

          <strong>Result</strong>
          <span
            style={{
              fontWeight: 600,
              color: "#2e7d32",
            }}
          >
            Verified
          </span>

          <strong>Comment</strong>
          <span>
            설치 결과 및 제출 파일 확인 완료
          </span>
        </div>
      </div>
    </div>
  );
}

export default EvidenceDetail;