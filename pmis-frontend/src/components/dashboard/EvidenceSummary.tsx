function EvidenceSummary() {
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
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: "20px",
        }}
      >
        <div>
          <h3
            style={{
              margin: 0,
              fontSize: "18px",
            }}
          >
            Evidence Status
          </h3>

          <p
            style={{
              margin: "6px 0 0",
              color: "#666666",
              fontSize: "14px",
            }}
          >
            Project evidence completeness
          </p>
        </div>

        <button
          type="button"
          style={{
            padding: "8px 12px",
            border: "1px solid #cccccc",
            borderRadius: "4px",
            backgroundColor: "#ffffff",
            cursor: "pointer",
          }}
        >
          View Evidence
        </button>
      </div>

      {/* Summary */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(4, 1fr)",
          gap: "12px",
          marginBottom: "20px",
        }}
      >
        <div>
          <p
            style={{
              margin: 0,
              color: "#666666",
              fontSize: "13px",
            }}
          >
            Total
          </p>

          <strong
            style={{
              display: "block",
              marginTop: "4px",
              fontSize: "22px",
            }}
          >
            128
          </strong>
        </div>

        <div>
          <p
            style={{
              margin: 0,
              color: "#666666",
              fontSize: "13px",
            }}
          >
            Required
          </p>

          <strong
            style={{
              display: "block",
              marginTop: "4px",
              fontSize: "22px",
            }}
          >
            103
          </strong>
        </div>

        <div>
          <p
            style={{
              margin: 0,
              color: "#666666",
              fontSize: "13px",
            }}
          >
            Submitted
          </p>

          <strong
            style={{
              display: "block",
              marginTop: "4px",
              fontSize: "22px",
            }}
          >
            91
          </strong>
        </div>

        <div>
          <p
            style={{
              margin: 0,
              color: "#666666",
              fontSize: "13px",
            }}
          >
            Verified
          </p>

          <strong
            style={{
              display: "block",
              marginTop: "4px",
              fontSize: "22px",
            }}
          >
            82
          </strong>
        </div>
      </div>

      {/* Completeness */}
      <div
        style={{
          marginBottom: "20px",
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            marginBottom: "8px",
          }}
        >
          <span
            style={{
              fontSize: "14px",
              fontWeight: 500,
            }}
          >
            Evidence Completeness
          </span>

          <strong>80%</strong>
        </div>

        <div
          style={{
            width: "100%",
            height: "10px",
            backgroundColor: "#eeeeee",
            borderRadius: "5px",
            overflow: "hidden",
          }}
        >
          <div
            style={{
              width: "80%",
              height: "100%",
              backgroundColor: "#4caf50",
            }}
          />
        </div>
      </div>

      {/* Status */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(3, 1fr)",
          gap: "12px",
        }}
      >
        <div
          style={{
            padding: "12px",
            backgroundColor: "#fff8e1",
            borderRadius: "4px",
          }}
        >
          <p
            style={{
              margin: 0,
              color: "#666666",
              fontSize: "13px",
            }}
          >
            Missing
          </p>

          <strong
            style={{
              display: "block",
              marginTop: "4px",
              fontSize: "20px",
            }}
          >
            12
          </strong>
        </div>

        <div
          style={{
            padding: "12px",
            backgroundColor: "#fff8e1",
            borderRadius: "4px",
          }}
        >
          <p
            style={{
              margin: 0,
              color: "#666666",
              fontSize: "13px",
            }}
          >
            Verification Required
          </p>

          <strong
            style={{
              display: "block",
              marginTop: "4px",
              fontSize: "20px",
            }}
          >
            9
          </strong>
        </div>

        <div
          style={{
            padding: "12px",
            backgroundColor: "#ffebee",
            borderRadius: "4px",
          }}
        >
          <p
            style={{
              margin: 0,
              color: "#666666",
              fontSize: "13px",
            }}
          >
            Rejected
          </p>

          <strong
            style={{
              display: "block",
              marginTop: "4px",
              fontSize: "20px",
            }}
          >
            2
          </strong>
        </div>
      </div>
    </div>
  );
}

export default EvidenceSummary;