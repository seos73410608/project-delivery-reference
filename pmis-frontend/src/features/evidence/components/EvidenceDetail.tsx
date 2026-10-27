import {
  useEffect,
  useRef,
  useState,
  type ChangeEvent,
} from "react";
import type { Evidence } from "../types/evidence";
import {
  createEvidenceOcrJob,
  refreshEvidenceOcrJob,
  uploadOcrFile,
  type EvidenceOcrJob,
} from "../api/evidenceApi";

interface EvidenceDetailProps {
  evidence: Evidence | null;
}

type OcrStatus = "IDLE" | "PENDING" | "SUCCEEDED" | "FAILED";

const OCR_POLL_INTERVAL_MS = 2000;
const OCR_MAX_ATTEMPTS = 15;

function getVerificationStatusLabel(
  status: Evidence["verificationStatus"],
) {
  switch (status) {
    case "APPROVED":
      return "승인";
    case "REJECTED":
      return "반려";
    case "PENDING":
      return "검증 대기";
    default:
      return status;
  }
}

function getVerificationStatusStyle(
  status: Evidence["verificationStatus"],
) {
  switch (status) {
    case "APPROVED":
      return {
        backgroundColor: "#e8f5e9",
        color: "#2e7d32",
      };
    case "REJECTED":
      return {
        backgroundColor: "#ffebee",
        color: "#c62828",
      };
    case "PENDING":
    default:
      return {
        backgroundColor: "#fff3e0",
        color: "#e65100",
      };
  }
}

function formatDate(date: string | null | undefined): string {
  if (!date) {
    return "-";
  }

  const parsedDate = new Date(date);

  if (Number.isNaN(parsedDate.getTime())) {
    return date;
  }

  return parsedDate.toLocaleString("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  });
}

function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  return "OCR 처리 중 알 수 없는 오류가 발생했습니다.";
}

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function EvidenceDetail({ evidence }: EvidenceDetailProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const requestSequenceRef = useRef(0);

  const [ocrStatus, setOcrStatus] = useState<OcrStatus>("IDLE");
  const [ocrJob, setOcrJob] = useState<EvidenceOcrJob | null>(null);
  const [ocrError, setOcrError] = useState("");

  useEffect(() => {
    // 증적이 바뀌면 이전 OCR 요청의 화면 반영을 무효화한다.
    requestSequenceRef.current += 1;

    setOcrStatus("IDLE");
    setOcrJob(null);
    setOcrError("");

    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }

    return () => {
      // 진행 중인 요청이 이후 화면 상태를 덮어쓰지 않도록 한다.
      requestSequenceRef.current += 1;
    };
  }, [evidence?.id]);

  const statusStyle = evidence
    ? getVerificationStatusStyle(evidence.verificationStatus)
    : undefined;

  function handleOcrButtonClick() {
    if (!evidence || ocrStatus === "PENDING") {
      return;
    }

    setOcrError("");

    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  }

  async function waitForOcrResult(
    jobId: number,
    requestSequence: number,
  ): Promise<EvidenceOcrJob> {
    for (let attempt = 0; attempt < OCR_MAX_ATTEMPTS; attempt += 1) {
      if (requestSequenceRef.current !== requestSequence) {
        throw new Error("증적 선택이 변경되어 OCR 화면 갱신을 중단했습니다.");
      }

      // 최초 갱신 및 후속 상태 재조회
      const refreshedJob = await refreshEvidenceOcrJob(jobId);

      if (requestSequenceRef.current !== requestSequence) {
        throw new Error("증적 선택이 변경되어 OCR 화면 갱신을 중단했습니다.");
      }

      setOcrJob(refreshedJob);

      if (refreshedJob.status === "SUCCEEDED") {
        return refreshedJob;
      }

      if (refreshedJob.status === "FAILED") {
        throw new Error(
          refreshedJob.errorDetail || "OCR 작업에 실패했습니다.",
        );
      }

      // PENDING 또는 RUNNING 상태면 잠시 기다렸다가 재조회한다.
      if (attempt < OCR_MAX_ATTEMPTS - 1) {
        await sleep(OCR_POLL_INTERVAL_MS);
      }
    }

    throw new Error(
      "OCR 처리 결과를 제한 시간 내에 확인하지 못했습니다. 잠시 후 다시 시도해 주세요.",
    );
  }

  async function handleFileChange(
    event: ChangeEvent<HTMLInputElement>,
  ) {
    const file = event.target.files?.[0];
    const selectedEvidence = evidence;

    // 같은 파일을 다시 선택할 수 있도록 입력값을 초기화한다.
    event.target.value = "";

    if (!file || !selectedEvidence || ocrStatus === "PENDING") {
      return;
    }

    const requestSequence = ++requestSequenceRef.current;

    setOcrStatus("PENDING");
    setOcrJob(null);
    setOcrError("");

    try {
      // 1. OCR 입력 파일 업로드
      const uploadedFile = await uploadOcrFile(file);

      if (requestSequenceRef.current !== requestSequence) {
        return;
      }

      if (!uploadedFile.file_id) {
        throw new Error("파일 업로드 응답에 file_id가 없습니다.");
      }

      // 2. 선택한 증적의 OCR 작업 생성
      const createdJob = await createEvidenceOcrJob(
        selectedEvidence.id,
        {
          inputFileId: uploadedFile.file_id,
        },
      );

      if (requestSequenceRef.current !== requestSequence) {
        return;
      }

      setOcrJob(createdJob);

      if (createdJob.status === "SUCCEEDED") {
        setOcrStatus("SUCCEEDED");
        return;
      }

      if (createdJob.status === "FAILED") {
        setOcrStatus("FAILED");
        setOcrError(
          createdJob.errorDetail || "OCR 작업에 실패했습니다.",
        );
        return;
      }

      // 3. 작업이 완료될 때까지 상태를 재조회한다.
      const completedJob = await waitForOcrResult(
        createdJob.id,
        requestSequence,
      );

      if (requestSequenceRef.current !== requestSequence) {
        return;
      }

      setOcrJob(completedJob);
      setOcrStatus("SUCCEEDED");
    } catch (error: unknown) {
      if (requestSequenceRef.current !== requestSequence) {
        return;
      }

      setOcrStatus("FAILED");
      setOcrError(getErrorMessage(error));
    }
  }

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
      <div style={{ marginBottom: "20px" }}>
        <h2 style={{ margin: 0, fontSize: "18px" }}>
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

      {!evidence ? (
        <div
          style={{
            padding: "40px 12px",
            textAlign: "center",
            color: "#777777",
            fontSize: "14px",
            backgroundColor: "#fafafa",
            borderRadius: "6px",
          }}
        >
          증적 목록에서 항목을 선택하면 상세 정보가 표시됩니다.
        </div>
      ) : (
        <>
          {/* Evidence Information */}
          <div style={{ marginBottom: "20px" }}>
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "120px 1fr",
                rowGap: "12px",
                fontSize: "14px",
                overflowWrap: "anywhere",
              }}
            >
              <strong>Evidence ID</strong>
              <span>{evidence.evidenceKey || `#${evidence.id}`}</span>

              <strong>Title</strong>
              <span>{evidence.title || "-"}</span>

              <strong>Evidence Type</strong>
              <span>{evidence.evidenceType || "-"}</span>

              <strong>Project ID</strong>
              <span>{evidence.projectId}</span>

              <strong>Requirement ID</strong>
              <span>{evidence.requirementId}</span>

              <strong>WBS ID</strong>
              <span>{evidence.wbsId ?? "-"}</span>

              <strong>Status</strong>
              <span
                style={{
                  display: "inline-block",
                  width: "fit-content",
                  padding: "4px 8px",
                  borderRadius: "4px",
                  fontSize: "12px",
                  fontWeight: 600,
                  ...statusStyle,
                }}
              >
                {getVerificationStatusLabel(evidence.verificationStatus)}
              </span>

              <strong>Submitted By</strong>
              <span>{evidence.submittedByName || "-"}</span>

              <strong>Submitted Date</strong>
              <span>{formatDate(evidence.submittedAt)}</span>

              <strong>Verified By</strong>
              <span>{evidence.verifiedByName || "-"}</span>

              <strong>Verified Date</strong>
              <span>{formatDate(evidence.verifiedAt)}</span>

              <strong>Description</strong>
              <span>{evidence.description || "-"}</span>
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
                overflowWrap: "anywhere",
              }}
            >
              <div style={{ fontWeight: 600, marginBottom: "6px" }}>
                {evidence.fileName || "첨부 파일 없음"}
              </div>

              <div
                style={{
                  color: "#666666",
                  fontSize: "13px",
                }}
              >
                {evidence.filePath
                  ? "파일 경로 정보가 등록되어 있습니다."
                  : "파일 경로 정보 없음"}
              </div>

              <input
                ref={fileInputRef}
                type="file"
                accept="image/*,.pdf"
                onChange={handleFileChange}
                disabled={ocrStatus === "PENDING"}
                style={{ display: "none" }}
              />

              <button
                type="button"
                onClick={handleOcrButtonClick}
                disabled={ocrStatus === "PENDING"}
                style={{
                  marginTop: "12px",
                  padding: "8px 14px",
                  border: "1px solid #1976d2",
                  borderRadius: "6px",
                  backgroundColor:
                    ocrStatus === "PENDING" ? "#eeeeee" : "#1976d2",
                  color:
                    ocrStatus === "PENDING" ? "#777777" : "#ffffff",
                  cursor:
                    ocrStatus === "PENDING" ? "not-allowed" : "pointer",
                }}
              >
                {ocrStatus === "PENDING"
                  ? "OCR 처리 중..."
                  : ocrStatus === "SUCCEEDED"
                    ? "OCR 다시 실행"
                    : ocrStatus === "FAILED"
                      ? "OCR 재시도"
                      : "OCR 실행"}
              </button>
            </div>
          </div>

          {/* OCR Result */}
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
              OCR Result
            </h3>

            {ocrStatus === "IDLE" && (
              <p style={{ color: "#666666", fontSize: "14px" }}>
                OCR 실행 버튼을 눌러 파일을 선택하면 인식 작업이 시작됩니다.
              </p>
            )}

            {ocrStatus === "PENDING" && (
              <p
                role="status"
                style={{ color: "#e65100", fontSize: "14px" }}
              >
                OCR 작업을 처리하고 있습니다. 완료될 때까지 자동으로 확인합니다.
              </p>
            )}

            {ocrStatus === "FAILED" && (
              <p
                role="alert"
                style={{
                  color: "#c62828",
                  fontSize: "14px",
                  whiteSpace: "pre-wrap",
                }}
              >
                {ocrError}
              </p>
            )}

            {ocrStatus === "SUCCEEDED" && ocrJob && (
              <div style={{ fontSize: "14px" }}>
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "120px 1fr",
                    rowGap: "10px",
                    marginBottom: "12px",
                  }}
                >
                  <strong>Job ID</strong>
                  <span>{ocrJob.id}</span>

                  <strong>OCR Status</strong>
                  <span style={{ color: "#2e7d32", fontWeight: 600 }}>
                    {ocrJob.status}
                  </span>

                  <strong>Engine</strong>
                  <span>{ocrJob.engineName || "-"}</span>

                  <strong>Engine Version</strong>
                  <span>{ocrJob.engineVersion || "-"}</span>

                  <strong>Finished At</strong>
                  <span>{formatDate(ocrJob.finishedAt)}</span>
                </div>

                <strong style={{ display: "block", marginBottom: "8px" }}>
                  Recognized Text
                </strong>

                <pre
                  style={{
                    margin: 0,
                    padding: "12px",
                    backgroundColor: "#f5f5f5",
                    borderRadius: "6px",
                    whiteSpace: "pre-wrap",
                    overflowWrap: "anywhere",
                    fontSize: "13px",
                  }}
                >
                  {ocrJob.recognizedText || "인식된 텍스트가 없습니다."}
                </pre>
              </div>
            )}
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
                overflowWrap: "anywhere",
              }}
            >
              <strong>Verifier</strong>
              <span>{evidence.verifiedByName || "-"}</span>

              <strong>Result</strong>
              <span
                style={{
                  fontWeight: 600,
                  color: statusStyle?.color,
                }}
              >
                {getVerificationStatusLabel(evidence.verificationStatus)}
              </span>

              <strong>Comment</strong>
              <span>{evidence.verificationRemark || "-"}</span>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

export default EvidenceDetail;