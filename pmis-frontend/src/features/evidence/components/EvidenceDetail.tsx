import {
  useCallback,
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
): string {
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

function formatDate(
  value: string | null | undefined,
): string {
  if (!value) {
    return "-";
  }

  const parsedDate = new Date(value);

  if (Number.isNaN(parsedDate.getTime())) {
    return value;
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

  // 증적 변경 및 비동기 요청의 최신 실행 번호를 관리한다.
  const requestSequenceRef = useRef(0);

  // 컴포넌트가 언마운트되었는지 확인한다.
  const mountedRef = useRef(true);

  const [ocrStatus, setOcrStatus] = useState<OcrStatus>("IDLE");
  const [ocrJob, setOcrJob] = useState<EvidenceOcrJob | null>(null);
  const [ocrError, setOcrError] = useState("");

  useEffect(() => {
    mountedRef.current = true;

    return () => {
      mountedRef.current = false;
      requestSequenceRef.current += 1;
    };
  }, []);

  /**
   * 증적이 바뀌면 기존 OCR 결과를 초기화한다.
   *
   * 이전 요청이 완료되더라도 새로 선택한 증적의 화면을
   * 덮어쓰지 못하도록 실행 번호를 갱신한다.
   */
  useEffect(() => {
    requestSequenceRef.current += 1;

    setOcrStatus("IDLE");
    setOcrJob(null);
    setOcrError("");

    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  }, [evidence?.id]);

  const isCurrentRequest = useCallback(
    (requestSequence: number): boolean => {
      return (
        mountedRef.current &&
        requestSequenceRef.current === requestSequence
      );
    },
    [],
  );

  const statusStyle = evidence
    ? getVerificationStatusStyle(evidence.verificationStatus)
    : undefined;

  /**
   * OCR 버튼을 누르면 파일 선택창을 연다.
   */
  function handleOcrButtonClick() {
    if (!evidence || ocrStatus === "PENDING") {
      return;
    }

    setOcrError("");

    fileInputRef.current?.click();
  }

  /**
   * OCR 작업 상태를 재조회한다.
   *
   * PENDING 또는 RUNNING 상태에서는 제한 횟수만큼 반복 조회한다.
   */
  async function waitForOcrResult(
    jobId: number,
    requestSequence: number,
  ): Promise<EvidenceOcrJob> {
    for (let attempt = 0; attempt < OCR_MAX_ATTEMPTS; attempt += 1) {
      if (!isCurrentRequest(requestSequence)) {
        throw new Error(
          "증적 선택이 변경되어 OCR 화면 갱신을 중단했습니다.",
        );
      }

      const refreshedJob = await refreshEvidenceOcrJob(jobId);

      if (!isCurrentRequest(requestSequence)) {
        throw new Error(
          "증적 선택이 변경되어 OCR 화면 갱신을 중단했습니다.",
        );
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

      // PENDING 또는 RUNNING 상태는 계속 조회한다.
      if (attempt < OCR_MAX_ATTEMPTS - 1) {
        await sleep(OCR_POLL_INTERVAL_MS);
      }
    }

    throw new Error(
      "OCR 처리 결과를 제한 시간 내에 확인하지 못했습니다. " +
        "잠시 후 다시 시도해 주세요.",
    );
  }

  /**
   * 파일 선택 → Runtime 업로드 → OCR 작업 생성 → 결과 확인
   */
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

      if (!isCurrentRequest(requestSequence)) {
        return;
      }

      if (!uploadedFile.file_id) {
        throw new Error(
          "파일 업로드 응답에 file_id가 없습니다.",
        );
      }

      // 2. 현재 선택된 증적의 OCR 작업 생성
      const createdJob = await createEvidenceOcrJob(
        selectedEvidence.id,
        {
          inputFileId: uploadedFile.file_id,
        },
      );

      if (!isCurrentRequest(requestSequence)) {
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

      // 3. 완료될 때까지 OCR 작업 상태를 재조회한다.
      const completedJob = await waitForOcrResult(
        createdJob.id,
        requestSequence,
      );

      if (!isCurrentRequest(requestSequence)) {
        return;
      }

      setOcrJob(completedJob);
      setOcrStatus("SUCCEEDED");
    } catch (error: unknown) {
      if (!isCurrentRequest(requestSequence)) {
        return;
      }

      setOcrStatus("FAILED");
      setOcrError(getErrorMessage(error));
    }
  }

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <h2 style={styles.heading}>Evidence Detail</h2>

        <p style={styles.subheading}>
          Selected evidence information
        </p>
      </div>

      {!evidence ? (
        <div style={styles.emptyState}>
          증적 목록에서 항목을 선택하면 상세 정보가 표시됩니다.
        </div>
      ) : (
        <>
          {/* Evidence Information */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>증적 정보</h3>

            <div style={styles.infoGrid}>
              <strong>Evidence ID</strong>
              <span>
                {evidence.evidenceKey || `#${evidence.id}`}
              </span>

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
              <span>
                <span
                  style={{
                    ...styles.statusBadge,
                    ...statusStyle,
                  }}
                >
                  {getVerificationStatusLabel(
                    evidence.verificationStatus,
                  )}
                </span>
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
              <span style={styles.longText}>
                {evidence.description || "-"}
              </span>
            </div>
          </section>

          {/* File Information and OCR Input */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>Attached Evidence</h3>

            <div style={styles.fileCard}>
              <div style={styles.fileName}>
                {evidence.fileName || "첨부 파일 없음"}
              </div>

              <div style={styles.fileDescription}>
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
                style={styles.hiddenInput}
              />

              <button
                type="button"
                onClick={handleOcrButtonClick}
                disabled={ocrStatus === "PENDING"}
                style={{
                  ...styles.ocrButton,
                  ...(ocrStatus === "PENDING"
                    ? styles.ocrButtonDisabled
                    : {}),
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
          </section>

          {/* OCR Result */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>OCR Result</h3>

            {ocrStatus === "IDLE" && (
              <p style={styles.mutedText}>
                OCR 실행 버튼을 눌러 파일을 선택하면 인식 작업이
                시작됩니다.
              </p>
            )}

            {ocrStatus === "PENDING" && (
              <p role="status" style={styles.pendingText}>
                OCR 작업을 처리하고 있습니다. 완료될 때까지 자동으로
                확인합니다.
              </p>
            )}

            {ocrStatus === "FAILED" && (
              <div role="alert" style={styles.errorBox}>
                {ocrError}
              </div>
            )}

            {ocrStatus === "SUCCEEDED" && ocrJob && (
              <div>
                <div style={styles.infoGrid}>
                  <strong>Job ID</strong>
                  <span>{ocrJob.id}</span>

                  <strong>OCR Status</strong>
                  <span style={styles.successText}>
                    {ocrJob.status}
                  </span>

                  <strong>Engine</strong>
                  <span>{ocrJob.engineName || "-"}</span>

                  <strong>Engine Version</strong>
                  <span>{ocrJob.engineVersion || "-"}</span>

                  <strong>Finished At</strong>
                  <span>{formatDate(ocrJob.finishedAt)}</span>
                </div>

                {ocrJob.warnings && ocrJob.warnings.length > 0 && (
                  <div style={styles.warningBox}>
                    <strong>Warnings</strong>
                    <ul style={styles.warningList}>
                      {ocrJob.warnings.map((warning, index) => (
                        <li key={`${index}-${warning}`}>
                          {warning}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}

                <strong style={styles.recognizedTextHeading}>
                  Recognized Text
                </strong>

                <pre style={styles.recognizedText}>
                  {ocrJob.recognizedText ||
                    "인식된 텍스트가 없습니다."}
                </pre>
              </div>
            )}
          </section>

          {/* Verification */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>Verification</h3>

            <div style={styles.infoGrid}>
              <strong>Verifier</strong>
              <span>{evidence.verifiedByName || "-"}</span>

              <strong>Result</strong>
              <span
                style={{
                  ...styles.verificationResult,
                  color: statusStyle?.color,
                }}
              >
                {getVerificationStatusLabel(
                  evidence.verificationStatus,
                )}
              </span>

              <strong>Comment</strong>
              <span style={styles.longText}>
                {evidence.verificationRemark || "-"}
              </span>
            </div>
          </section>
        </>
      )}
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  container: {
    display: "flex",
    flexDirection: "column",
    gap: 20,
    width: "100%",
    boxSizing: "border-box",
    padding: 20,
    border: "1px solid #e0e0e0",
    borderRadius: 8,
    backgroundColor: "#ffffff",
  },

  header: {
    marginBottom: 0,
  },

  heading: {
    margin: 0,
    fontSize: 18,
  },

  subheading: {
    marginTop: 6,
    marginBottom: 0,
    color: "#666666",
    fontSize: 14,
  },

  emptyState: {
    padding: "40px 12px",
    textAlign: "center",
    color: "#777777",
    fontSize: 14,
    backgroundColor: "#fafafa",
    borderRadius: 6,
  },

  section: {
    borderTop: "1px solid #eeeeee",
    paddingTop: 20,
    minWidth: 0,
  },

  sectionHeading: {
    marginTop: 0,
    marginBottom: 12,
    fontSize: 15,
  },

  infoGrid: {
    display: "grid",
    gridTemplateColumns: "120px minmax(0, 1fr)",
    rowGap: 12,
    columnGap: 12,
    fontSize: 14,
    overflowWrap: "anywhere",
  },

  statusBadge: {
    display: "inline-block",
    width: "fit-content",
    padding: "4px 8px",
    borderRadius: 4,
    fontSize: 12,
    fontWeight: 600,
  },

  longText: {
    whiteSpace: "pre-wrap",
    overflowWrap: "anywhere",
  },

  fileCard: {
    padding: 12,
    border: "1px solid #e0e0e0",
    borderRadius: 6,
    backgroundColor: "#fafafa",
    overflowWrap: "anywhere",
  },

  fileName: {
    marginBottom: 6,
    fontWeight: 600,
  },

  fileDescription: {
    color: "#666666",
    fontSize: 13,
  },

  hiddenInput: {
    display: "none",
  },

  ocrButton: {
    marginTop: 12,
    padding: "8px 14px",
    border: "1px solid #1976d2",
    borderRadius: 6,
    backgroundColor: "#1976d2",
    color: "#ffffff",
    cursor: "pointer",
  },

  ocrButtonDisabled: {
    backgroundColor: "#eeeeee",
    color: "#777777",
    borderColor: "#eeeeee",
    cursor: "not-allowed",
  },

  mutedText: {
    color: "#666666",
    fontSize: 14,
  },

  pendingText: {
    color: "#e65100",
    fontSize: 14,
  },

  errorBox: {
    padding: 12,
    border: "1px solid #ffcdd2",
    borderRadius: 6,
    backgroundColor: "#ffebee",
    color: "#c62828",
    fontSize: 14,
    whiteSpace: "pre-wrap",
    overflowWrap: "anywhere",
  },

  successText: {
    color: "#2e7d32",
    fontWeight: 600,
  },

  warningBox: {
    marginTop: 16,
    padding: 12,
    border: "1px solid #ffe0b2",
    borderRadius: 6,
    backgroundColor: "#fff8e1",
    color: "#795548",
    fontSize: 13,
  },

  warningList: {
    marginBottom: 0,
    paddingLeft: 20,
  },

  recognizedTextHeading: {
    display: "block",
    marginTop: 16,
    marginBottom: 8,
  },

  recognizedText: {
    boxSizing: "border-box",
    width: "100%",
    margin: 0,
    padding: 12,
    backgroundColor: "#f5f5f5",
    borderRadius: 6,
    whiteSpace: "pre-wrap",
    overflowWrap: "anywhere",
    fontSize: 13,
    fontFamily: "inherit",
  },

  verificationResult: {
    fontWeight: 600,
  },
};

export default EvidenceDetail;