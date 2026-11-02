import {
  useCallback,
  useEffect,
  useRef,
  useState,
  type ChangeEvent,
  type CSSProperties,
} from "react";

import type {
  Evidence,
  EvidenceVersion,
  EvidenceVerificationHistory,
} from "../types/evidence";

import {
  createEvidenceOcrJob,
  refreshEvidenceOcrJob,
  uploadOcrFile,
  getEvidenceVersions,
  getEvidenceVersion,
  getEvidenceVersionVerificationHistory,
  type EvidenceOcrJob,
} from "../api/evidenceApi";

interface EvidenceDetailProps {
  evidence: Evidence | null;
}

type OcrStatus = "IDLE" | "PENDING" | "SUCCEEDED" | "FAILED";

type VersionLoadingStatus =
  | "IDLE"
  | "LOADING"
  | "SUCCEEDED"
  | "FAILED";

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
): CSSProperties {
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

  return "처리 중 알 수 없는 오류가 발생했습니다.";
}

function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function EvidenceDetail({ evidence }: EvidenceDetailProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  // OCR 요청 식별
  const requestSequenceRef = useRef(0);

  // 버전 목록 요청 식별
  const versionListRequestSequenceRef = useRef(0);

  // 버전 상세 및 검증 이력 요청 식별
  const versionDetailRequestSequenceRef = useRef(0);

  const mountedRef = useRef(true);

  // OCR 상태
  const [ocrStatus, setOcrStatus] = useState<OcrStatus>("IDLE");
  const [ocrJob, setOcrJob] = useState<EvidenceOcrJob | null>(null);
  const [ocrError, setOcrError] = useState("");

  // Evidence 버전 목록
  const [versions, setVersions] = useState<EvidenceVersion[]>([]);
  const [versionsLoading, setVersionsLoading] =
    useState<VersionLoadingStatus>("IDLE");
  const [versionsError, setVersionsError] = useState("");

  // 선택된 버전 번호
  const [selectedVersionNumber, setSelectedVersionNumber] =
    useState<number | null>(null);

  // 선택된 버전 상세 정보
  const [selectedVersion, setSelectedVersion] =
    useState<EvidenceVersion | null>(null);

  // 선택된 버전의 검증 이력
  const [verificationHistory, setVerificationHistory] =
    useState<EvidenceVerificationHistory[]>([]);

  // 버전 상세 조회 상태
  const [versionDetailLoading, setVersionDetailLoading] =
    useState(false);
  const [versionDetailError, setVersionDetailError] = useState("");

  // 검증 이력 조회 상태
  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyError, setHistoryError] = useState("");

  // 컴포넌트 마운트 및 언마운트 처리
  useEffect(() => {
    mountedRef.current = true;

    return () => {
      mountedRef.current = false;

      requestSequenceRef.current += 1;
      versionListRequestSequenceRef.current += 1;
      versionDetailRequestSequenceRef.current += 1;
    };
  }, []);

  // 현재 OCR 요청인지 확인
  const isCurrentRequest = useCallback(
    (requestSequence: number): boolean =>
      mountedRef.current &&
      requestSequenceRef.current === requestSequence,
    [],
  );

  // 현재 버전 목록 요청인지 확인
  const isCurrentVersionListRequest = useCallback(
    (requestSequence: number): boolean =>
      mountedRef.current &&
      versionListRequestSequenceRef.current === requestSequence,
    [],
  );

  // 현재 버전 상세 및 검증 이력 요청인지 확인
  const isCurrentVersionDetailRequest = useCallback(
    (requestSequence: number): boolean =>
      mountedRef.current &&
      versionDetailRequestSequenceRef.current === requestSequence,
    [],
  );

  // Evidence 변경 시 OCR 및 버전 화면 초기화
  useEffect(() => {
    // 진행 중인 요청 무효화
    requestSequenceRef.current += 1;
    versionListRequestSequenceRef.current += 1;
    versionDetailRequestSequenceRef.current += 1;

    // OCR 상태 초기화
    setOcrStatus("IDLE");
    setOcrJob(null);
    setOcrError("");

    // 버전 목록 초기화
    setVersions([]);
    setVersionsLoading("IDLE");
    setVersionsError("");

    // 선택된 버전 초기화
    setSelectedVersionNumber(null);
    setSelectedVersion(null);
    setVerificationHistory([]);

    // 버전 상세 상태 초기화
    setVersionDetailLoading(false);
    setVersionDetailError("");

    // 검증 이력 상태 초기화
    setHistoryLoading(false);
    setHistoryError("");

    // 파일 입력 초기화
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  }, [evidence?.id]);

  // Evidence 선택 시 버전 목록 조회
  useEffect(() => {
    if (!evidence) {
      return;
    }

    const evidenceId = evidence.id;
    const requestSequence =
      ++versionListRequestSequenceRef.current;

    async function loadVersions() {
      setVersionsLoading("LOADING");
      setVersionsError("");

      try {
        const result = await getEvidenceVersions(evidenceId);

        if (!isCurrentVersionListRequest(requestSequence)) {
          return;
        }

        setVersions(result);
        setVersionsLoading("SUCCEEDED");

        if (result.length > 0) {
          // Backend에서 최신 버전부터 반환하므로 첫 번째 항목 사용
          setSelectedVersionNumber(result[0].versionNumber);
        } else {
          setSelectedVersionNumber(null);
          setSelectedVersion(null);
          setVerificationHistory([]);
        }
      } catch (error: unknown) {
        if (!isCurrentVersionListRequest(requestSequence)) {
          return;
        }

        setVersionsLoading("FAILED");
        setVersionsError(getErrorMessage(error));
      }
    }

    void loadVersions();

    return () => {
      versionListRequestSequenceRef.current += 1;
    };
  }, [evidence?.id, isCurrentVersionListRequest]);

  // 버전 선택 시 상세 정보 및 검증 이력 조회
  useEffect(() => {
    if (!evidence || selectedVersionNumber === null) {
      versionDetailRequestSequenceRef.current += 1;
      return;
    }

    const evidenceId = evidence.id;
    const versionNumber = selectedVersionNumber;

    const requestSequence =
      ++versionDetailRequestSequenceRef.current;

    async function loadVersionDetails() {
      setSelectedVersion(null);
      setVerificationHistory([]);

      setVersionDetailLoading(true);
      setVersionDetailError("");

      setHistoryLoading(true);
      setHistoryError("");

      // 버전 상세와 검증 이력을 병렬 조회
      const [versionResult, historyResult] =
        await Promise.allSettled([
          getEvidenceVersion(evidenceId, versionNumber),
          getEvidenceVersionVerificationHistory(
            evidenceId,
            versionNumber,
          ),
        ]);

      if (!isCurrentVersionDetailRequest(requestSequence)) {
        return;
      }

      // 버전 상세 조회 결과 처리
      if (versionResult.status === "fulfilled") {
        setSelectedVersion(versionResult.value);
        setVersionDetailLoading(false);
      } else {
        setVersionDetailLoading(false);
        setVersionDetailError(
          getErrorMessage(versionResult.reason),
        );
      }

      // 검증 이력 조회 결과 처리
      if (historyResult.status === "fulfilled") {
        setVerificationHistory(historyResult.value);
        setHistoryLoading(false);
      } else {
        setHistoryLoading(false);
        setHistoryError(
          getErrorMessage(historyResult.reason),
        );
      }
    }

    void loadVersionDetails();

    return () => {
      versionDetailRequestSequenceRef.current += 1;
    };
  }, [
    evidence?.id,
    selectedVersionNumber,
    isCurrentVersionDetailRequest,
  ]);

  // 현재 Evidence의 검증 상태 스타일
  const statusStyle = evidence
    ? getVerificationStatusStyle(evidence.verificationStatus)
    : undefined;

  // 선택한 버전의 파일 정보를 우선 표시
  const displayedFileName =
    selectedVersion?.fileName || evidence?.fileName || null;

  const displayedFilePath =
    selectedVersion?.filePath || evidence?.filePath || null;

  // OCR 실행 버튼 클릭
  function handleOcrButtonClick() {
    if (!evidence || ocrStatus === "PENDING") {
      return;
    }

    setOcrError("");
    fileInputRef.current?.click();
  }

  // 버전 선택 변경
  function handleVersionChange(
    event: ChangeEvent<HTMLSelectElement>,
  ) {
    const versionNumber = Number(event.target.value);

    if (!Number.isInteger(versionNumber)) {
      return;
    }

    setSelectedVersionNumber(versionNumber);
  }

  // OCR 작업 완료까지 상태 재조회
  async function waitForOcrResult(
    jobId: number,
    requestSequence: number,
  ): Promise<EvidenceOcrJob> {
    for (
      let attempt = 0;
      attempt < OCR_MAX_ATTEMPTS;
      attempt += 1
    ) {
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

      if (attempt < OCR_MAX_ATTEMPTS - 1) {
        await sleep(OCR_POLL_INTERVAL_MS);
      }
    }

    throw new Error(
      "OCR 처리 결과를 제한 시간 내에 확인하지 못했습니다. " +
        "잠시 후 다시 시도해 주세요.",
    );
  }

  // OCR 파일 선택 및 작업 실행
  async function handleFileChange(
    event: ChangeEvent<HTMLInputElement>,
  ) {
    const file = event.target.files?.[0];
    const selectedEvidence = evidence;

    event.target.value = "";

    if (
      !file ||
      !selectedEvidence ||
      ocrStatus === "PENDING"
    ) {
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

      // 3. OCR 완료까지 상태 재조회
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
          {/* 증적 기본 정보 */}
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

          {/* Evidence 버전 목록 */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>
              Evidence Versions
            </h3>

            {versionsLoading === "LOADING" && (
              <p style={styles.mutedText}>
                버전 목록을 불러오는 중입니다.
              </p>
            )}

            {versionsLoading === "FAILED" && (
              <div role="alert" style={styles.errorBox}>
                버전 목록 조회 실패: {versionsError}
              </div>
            )}

            {versionsLoading === "SUCCEEDED" &&
              versions.length === 0 && (
                <p style={styles.mutedText}>
                  등록된 버전이 없습니다.
                </p>
              )}

            {versions.length > 0 && (
              <>
                <div style={styles.versionSelector}>
                  <label
                    htmlFor="evidence-version"
                    style={styles.versionLabel}
                  >
                    버전 선택
                  </label>

                  <select
                    id="evidence-version"
                    value={selectedVersionNumber ?? ""}
                    onChange={handleVersionChange}
                    style={styles.versionSelect}
                  >
                    {versions.map((version, index) => (
                      <option
                        key={version.id}
                        value={version.versionNumber}
                      >
                        Version {version.versionNumber}
                        {index === 0 ? " (최신)" : ""}
                      </option>
                    ))}
                  </select>
                </div>

                <div style={styles.versionList}>
                  {versions.map((version, index) => (
                    <button
                      key={version.id}
                      type="button"
                      onClick={() =>
                        setSelectedVersionNumber(
                          version.versionNumber,
                        )
                      }
                      style={{
                        ...styles.versionItem,
                        ...(selectedVersionNumber ===
                        version.versionNumber
                          ? styles.versionItemSelected
                          : {}),
                      }}
                    >
                      <span style={styles.versionItemTitle}>
                        Version {version.versionNumber}
                        {index === 0 ? " · 최신" : ""}
                      </span>

                      <span style={styles.versionItemMeta}>
                        {version.fileName || "파일명 없음"}
                      </span>

                      <span style={styles.versionItemMeta}>
                        제출: {formatDate(version.submittedAt)}
                      </span>

                      <span
                        style={{
                          ...styles.statusBadge,
                          ...getVerificationStatusStyle(
                            version.verificationStatus,
                          ),
                        }}
                      >
                        {getVerificationStatusLabel(
                          version.verificationStatus,
                        )}
                      </span>
                    </button>
                  ))}
                </div>
              </>
            )}
          </section>

          {/* 선택된 버전 상세 및 검증 이력 */}
          {selectedVersionNumber !== null && (
            <>
              <section style={styles.section}>
                <h3 style={styles.sectionHeading}>
                  Version {selectedVersionNumber} Detail
                </h3>

                {versionDetailLoading && (
                  <p style={styles.mutedText}>
                    선택한 버전의 상세 정보를 불러오는 중입니다.
                  </p>
                )}

                {versionDetailError && (
                  <div role="alert" style={styles.errorBox}>
                    버전 상세 조회 실패: {versionDetailError}
                  </div>
                )}

                {selectedVersion && (
                  <div style={styles.infoGrid}>
                    <strong>Version ID</strong>
                    <span>{selectedVersion.id}</span>

                    <strong>Version Number</strong>
                    <span>{selectedVersion.versionNumber}</span>

                    <strong>Title</strong>
                    <span>{selectedVersion.title || "-"}</span>

                    <strong>Evidence Type</strong>
                    <span>{selectedVersion.evidenceType || "-"}</span>

                    <strong>Description</strong>
                    <span style={styles.longText}>
                      {selectedVersion.description || "-"}
                    </span>

                    <strong>File Name</strong>
                    <span>{selectedVersion.fileName || "-"}</span>

                    <strong>File Path</strong>
                    <span style={styles.longText}>
                      {selectedVersion.filePath || "-"}
                    </span>

                    <strong>Source File ID</strong>
                    <span style={styles.longText}>
                      {selectedVersion.sourceFileId || "-"}
                    </span>

                    <strong>Execution UUID</strong>
                    <span style={styles.longText}>
                      {selectedVersion.executionUuid || "-"}
                    </span>

                    <strong>Source Job ID</strong>
                    <span>{selectedVersion.sourceJobId || "-"}</span>

                    <strong>Submitted By ID</strong>
                    <span>
                      {selectedVersion.submittedById ?? "-"}
                    </span>

                    <strong>Submitted At</strong>
                    <span>
                      {formatDate(selectedVersion.submittedAt)}
                    </span>

                    <strong>Verification Status</strong>
                    <span>
                      <span
                        style={{
                          ...styles.statusBadge,
                          ...getVerificationStatusStyle(
                            selectedVersion.verificationStatus,
                          ),
                        }}
                      >
                        {getVerificationStatusLabel(
                          selectedVersion.verificationStatus,
                        )}
                      </span>
                    </span>

                    <strong>Verified By ID</strong>
                    <span>
                      {selectedVersion.verifiedById ?? "-"}
                    </span>

                    <strong>Verified At</strong>
                    <span>
                      {formatDate(selectedVersion.verifiedAt)}
                    </span>

                    <strong>Verification Remark</strong>
                    <span style={styles.longText}>
                      {selectedVersion.verificationRemark || "-"}
                    </span>
                  </div>
                )}
              </section>

              {/* 버전별 검증 이력 */}
              <section style={styles.section}>
                <h3 style={styles.sectionHeading}>
                  Version {selectedVersionNumber} Verification History
                </h3>

                {historyLoading && (
                  <p style={styles.mutedText}>
                    검증 이력을 불러오는 중입니다.
                  </p>
                )}

                {historyError && (
                  <div role="alert" style={styles.errorBox}>
                    검증 이력 조회 실패: {historyError}
                  </div>
                )}

                {!historyLoading &&
                  !historyError &&
                  verificationHistory.length === 0 && (
                    <p style={styles.mutedText}>
                      해당 버전에 등록된 검증 이력이 없습니다.
                    </p>
                  )}

                {verificationHistory.length > 0 && (
                  <div style={styles.historyList}>
                    {verificationHistory.map((history) => (
                      <div
                        key={history.id}
                        style={styles.historyItem}
                      >
                        <div style={styles.historyHeader}>
                          <strong>
                            {getVerificationStatusLabel(
                              history.verificationStatus,
                            )}
                          </strong>

                          <span style={styles.historyDate}>
                            {formatDate(history.verifiedAt)}
                          </span>
                        </div>

                        <div style={styles.historyMeta}>
                          검증자 ID: {history.verifiedById ?? "-"}
                        </div>

                        <div style={styles.historyRemark}>
                          {history.remark || "검증 의견 없음"}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </section>
            </>
          )}

          {/* 첨부 파일 */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>
              Attached Evidence
            </h3>

            <div style={styles.fileCard}>
              <div style={styles.fileName}>
                {displayedFileName || "첨부 파일 없음"}
              </div>

              <div style={styles.fileDescription}>
                {displayedFilePath
                  ? `파일 경로: ${displayedFilePath}`
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

          {/* OCR 결과 */}
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

                {ocrJob.warnings &&
                  ocrJob.warnings.length > 0 && (
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

          {/* 현재 증적의 검증 정보 */}
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

const styles: Record<string, CSSProperties> = {
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

  versionSelector: {
    display: "flex",
    alignItems: "center",
    gap: 12,
    marginBottom: 12,
  },

  versionLabel: {
    fontSize: 14,
    fontWeight: 600,
  },

  versionSelect: {
    minWidth: 180,
    padding: "8px 10px",
    border: "1px solid #cccccc",
    borderRadius: 6,
    backgroundColor: "#ffffff",
    fontSize: 14,
  },

  versionList: {
    display: "flex",
    flexDirection: "column",
    gap: 8,
  },

  versionItem: {
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-start",
    gap: 6,
    width: "100%",
    padding: 12,
    border: "1px solid #e0e0e0",
    borderRadius: 6,
    backgroundColor: "#ffffff",
    cursor: "pointer",
    textAlign: "left",
  },

  versionItemSelected: {
    border: "1px solid #1976d2",
    backgroundColor: "#f3f8ff",
  },

  versionItemTitle: {
    fontSize: 14,
    fontWeight: 700,
  },

  versionItemMeta: {
    color: "#666666",
    fontSize: 12,
  },

  historyList: {
    display: "flex",
    flexDirection: "column",
    gap: 10,
  },

  historyItem: {
    padding: 12,
    border: "1px solid #e0e0e0",
    borderRadius: 6,
    backgroundColor: "#fafafa",
  },

  historyHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    flexWrap: "wrap",
    gap: 8,
    fontSize: 14,
  },

  historyDate: {
    color: "#666666",
    fontSize: 12,
  },

  historyMeta: {
    marginTop: 8,
    color: "#666666",
    fontSize: 12,
  },

  historyRemark: {
    marginTop: 8,
    fontSize: 13,
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