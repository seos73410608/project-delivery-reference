import {
  useCallback,
  useEffect,
  useRef,
  useState,
  type ChangeEvent,
  type CSSProperties,
  type FormEvent,
} from "react";

import type {
  Evidence,
  EvidenceType,
  EvidenceVersion,
  EvidenceVerificationHistory,
} from "../types/evidence";

import {
  createEvidenceVersion,
  createEvidenceOcrJob,
  refreshEvidenceOcrJob,
  uploadOcrFile,
  getEvidenceVersions,
  getEvidenceVersion,
  getEvidenceVersionVerificationHistory,
  type EvidenceOcrJob,
  type EvidenceVersionCreateRequest,
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

interface EvidenceVersionFormState {
  evidenceType: EvidenceType;
  title: string;
  description: string;
  fileName: string;
  filePath: string;
  sourceFileId: string;
  executionUuid: string;
  sourceJobId: string;
}

const OCR_POLL_INTERVAL_MS = 2000;
const OCR_MAX_ATTEMPTS = 15;

const EVIDENCE_TYPES: EvidenceType[] = [
  "DOCUMENT",
  "IMAGE",
  "SCREENSHOT",
  "LOG",
  "CONFIGURATION",
  "REPORT",
  "TEST_RESULT",
  "OTHER",
];

const EVIDENCE_TYPE_LABELS: Record<EvidenceType, string> = {
  DOCUMENT: "문서",
  IMAGE: "이미지",
  SCREENSHOT: "스크린샷",
  LOG: "로그",
  CONFIGURATION: "설정",
  REPORT: "보고서",
  TEST_RESULT: "테스트 결과",
  OTHER: "기타",
};

function createEmptyVersionForm(): EvidenceVersionFormState {
  return {
    evidenceType: "DOCUMENT",
    title: "",
    description: "",
    fileName: "",
    filePath: "",
    sourceFileId: "",
    executionUuid: "",
    sourceJobId: "",
  };
}

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

  // 버전 생성 요청 식별
  const versionCreateRequestSequenceRef = useRef(0);

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

  // 신규 버전 제출 폼
  const [isVersionFormOpen, setIsVersionFormOpen] =
    useState(false);

  const [versionForm, setVersionForm] =
    useState<EvidenceVersionFormState>(createEmptyVersionForm);

  const [versionSubmitting, setVersionSubmitting] =
    useState(false);

  const [versionSubmitError, setVersionSubmitError] =
    useState("");

  const [versionSubmitSuccess, setVersionSubmitSuccess] =
    useState("");

  // 컴포넌트 마운트 및 언마운트 처리
  useEffect(() => {
    mountedRef.current = true;

    return () => {
      mountedRef.current = false;

      requestSequenceRef.current += 1;
      versionListRequestSequenceRef.current += 1;
      versionDetailRequestSequenceRef.current += 1;
      versionCreateRequestSequenceRef.current += 1;
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
    versionCreateRequestSequenceRef.current += 1;

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

    // 신규 버전 폼 초기화
    setIsVersionFormOpen(false);
    setVersionForm(createEmptyVersionForm());
    setVersionSubmitting(false);
    setVersionSubmitError("");
    setVersionSubmitSuccess("");

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

  // 신규 버전 폼 열기
  function handleOpenVersionForm() {
    if (!evidence || versionSubmitting) {
      return;
    }

    const initialTitle =
      selectedVersion?.title || evidence.title || "";

    const initialDescription =
      selectedVersion?.description || evidence.description || "";

    setVersionForm({
      evidenceType:
        selectedVersion?.evidenceType ||
        evidence.evidenceType ||
        "DOCUMENT",
      title: initialTitle,
      description: initialDescription,
      fileName: "",
      filePath: "",
      sourceFileId: "",
      executionUuid: "",
      sourceJobId: "",
    });

    setVersionSubmitError("");
    setVersionSubmitSuccess("");
    setIsVersionFormOpen(true);
  }

  // 신규 버전 폼 닫기
  function handleCloseVersionForm() {
    if (versionSubmitting) {
      return;
    }

    setIsVersionFormOpen(false);
    setVersionSubmitError("");
  }

  // 신규 버전 폼 입력 변경
  function handleVersionFormChange(
    event: ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >,
  ) {
    const { name, value } = event.target;

    setVersionForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    setVersionSubmitError("");
    setVersionSubmitSuccess("");
  }

  // 신규 버전 생성 및 목록 갱신
  async function handleCreateVersion(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    if (!evidence || versionSubmitting) {
      return;
    }

    const evidenceId = evidence.id;
    const requestSequence =
      ++versionCreateRequestSequenceRef.current;

    const title = versionForm.title.trim();

    if (!title) {
      setVersionSubmitError("증적 제목을 입력해 주세요.");
      return;
    }

    if (title.length > 200) {
      setVersionSubmitError(
        "증적 제목은 200자 이하로 입력해 주세요.",
      );
      return;
    }

    const request: EvidenceVersionCreateRequest = {
      evidenceType: versionForm.evidenceType,
      title,
      description: versionForm.description.trim() || null,
      fileName: versionForm.fileName.trim() || null,
      filePath: versionForm.filePath.trim() || null,
      sourceFileId: versionForm.sourceFileId.trim() || null,
      executionUuid: versionForm.executionUuid.trim() || null,
      sourceJobId: versionForm.sourceJobId.trim() || null,
    };

    setVersionSubmitting(true);
    setVersionSubmitError("");
    setVersionSubmitSuccess("");

    try {
      // 1. 신규 버전 생성
      const createdVersion = await createEvidenceVersion(
        evidenceId,
        request,
      );

      if (
        !mountedRef.current ||
        versionCreateRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      // 2. 기존 버전 목록 요청 무효화
      versionListRequestSequenceRef.current += 1;

      // 3. 최신 버전 목록 재조회
      setVersionsLoading("LOADING");
      setVersionsError("");

      const refreshedVersions =
        await getEvidenceVersions(evidenceId);

      if (
        !mountedRef.current ||
        versionCreateRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      setVersions(refreshedVersions);
      setVersionsLoading("SUCCEEDED");

      // 4. 생성된 버전 선택
      setSelectedVersionNumber(createdVersion.versionNumber);

      // 5. 제출 완료 표시
      setVersionSubmitSuccess(
        `Version ${createdVersion.versionNumber} 생성이 완료되었습니다.`,
      );

      setIsVersionFormOpen(false);
      setVersionForm(createEmptyVersionForm());
    } catch (error: unknown) {
      if (
        !mountedRef.current ||
        versionCreateRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      setVersionSubmitError(getErrorMessage(error));
    } finally {
      if (
        mountedRef.current &&
        versionCreateRequestSequenceRef.current === requestSequence
      ) {
        setVersionSubmitting(false);
      }
    }
  }

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

          {/* Evidence 버전 목록 및 버전 생성 */}
          <section style={styles.section}>
            <div style={styles.sectionHeader}>
              <h3 style={styles.sectionHeading}>
                Evidence Versions
              </h3>

              <button
                type="button"
                onClick={handleOpenVersionForm}
                disabled={versionSubmitting}
                style={styles.createVersionButton}
              >
                새 버전 제출
              </button>
            </div>

            {versionSubmitSuccess && (
              <div role="status" style={styles.successBox}>
                {versionSubmitSuccess}
              </div>
            )}

            {versionSubmitError && !isVersionFormOpen && (
              <div role="alert" style={styles.errorBox}>
                {versionSubmitError}
              </div>
            )}

            {/* 신규 버전 제출 폼 */}
            {isVersionFormOpen && (
              <form
                onSubmit={handleCreateVersion}
                style={styles.versionForm}
              >
                <h4 style={styles.formHeading}>
                  신규 증적 버전 등록
                </h4>

                <div style={styles.formField}>
                  <label htmlFor="version-evidence-type">
                    증적 유형 <span style={styles.requiredMark}>*</span>
                  </label>

                  <select
                    id="version-evidence-type"
                    name="evidenceType"
                    value={versionForm.evidenceType}
                    onChange={handleVersionFormChange}
                    required
                    disabled={versionSubmitting}
                    style={styles.formInput}
                  >
                    {EVIDENCE_TYPES.map((type) => (
                      <option key={type} value={type}>
                        {EVIDENCE_TYPE_LABELS[type]}
                      </option>
                    ))}
                  </select>
                </div>

                <div style={styles.formField}>
                  <label htmlFor="version-title">
                    제목 <span style={styles.requiredMark}>*</span>
                  </label>

                  <input
                    id="version-title"
                    name="title"
                    type="text"
                    value={versionForm.title}
                    onChange={handleVersionFormChange}
                    maxLength={200}
                    required
                    disabled={versionSubmitting}
                    placeholder="증적 제목을 입력하세요."
                    style={styles.formInput}
                  />
                </div>

                <div style={styles.formField}>
                  <label htmlFor="version-description">
                    설명
                  </label>

                  <textarea
                    id="version-description"
                    name="description"
                    value={versionForm.description}
                    onChange={handleVersionFormChange}
                    rows={3}
                    disabled={versionSubmitting}
                    placeholder="증적에 대한 설명을 입력하세요."
                    style={styles.formTextarea}
                  />
                </div>

                <div style={styles.formField}>
                  <label htmlFor="version-file-name">
                    파일명
                  </label>

                  <input
                    id="version-file-name"
                    name="fileName"
                    type="text"
                    value={versionForm.fileName}
                    onChange={handleVersionFormChange}
                    maxLength={255}
                    disabled={versionSubmitting}
                    placeholder="예: test-result.pdf"
                    style={styles.formInput}
                  />
                </div>

                <div style={styles.formField}>
                  <label htmlFor="version-file-path">
                    파일 경로
                  </label>

                  <input
                    id="version-file-path"
                    name="filePath"
                    type="text"
                    value={versionForm.filePath}
                    onChange={handleVersionFormChange}
                    maxLength={1000}
                    disabled={versionSubmitting}
                    placeholder="파일 저장 경로"
                    style={styles.formInput}
                  />
                </div>

                <div style={styles.runtimeFields}>
                  <h5 style={styles.runtimeHeading}>
                    Automation Runtime 연계 정보
                  </h5>

                  <p style={styles.runtimeDescription}>
                    Runtime 파일 메타데이터와 연결할 경우 입력합니다.
                    sourceFileId를 입력하면 백엔드에서 Runtime 파일
                    메타데이터를 조회합니다.
                  </p>

                  <div style={styles.formField}>
                    <label htmlFor="version-source-file-id">
                      Source File ID
                    </label>

                    <input
                      id="version-source-file-id"
                      name="sourceFileId"
                      type="text"
                      value={versionForm.sourceFileId}
                      onChange={handleVersionFormChange}
                      maxLength={64}
                      disabled={versionSubmitting}
                      placeholder="Runtime file_id"
                      style={styles.formInput}
                    />
                  </div>

                  <div style={styles.formField}>
                    <label htmlFor="version-execution-uuid">
                      Execution UUID
                    </label>

                    <input
                      id="version-execution-uuid"
                      name="executionUuid"
                      type="text"
                      value={versionForm.executionUuid}
                      onChange={handleVersionFormChange}
                      maxLength={64}
                      disabled={versionSubmitting}
                      placeholder="Runtime execution UUID"
                      style={styles.formInput}
                    />
                  </div>

                  <div style={styles.formField}>
                    <label htmlFor="version-source-job-id">
                      Source Job ID
                    </label>

                    <input
                      id="version-source-job-id"
                      name="sourceJobId"
                      type="text"
                      value={versionForm.sourceJobId}
                      onChange={handleVersionFormChange}
                      maxLength={64}
                      disabled={versionSubmitting}
                      placeholder="Runtime source job ID"
                      style={styles.formInput}
                    />
                  </div>
                </div>

                {versionSubmitError && (
                  <div role="alert" style={styles.errorBox}>
                    버전 생성 실패: {versionSubmitError}
                  </div>
                )}

                <div style={styles.formActions}>
                  <button
                    type="button"
                    onClick={handleCloseVersionForm}
                    disabled={versionSubmitting}
                    style={styles.cancelButton}
                  >
                    취소
                  </button>

                  <button
                    type="submit"
                    disabled={versionSubmitting}
                    style={{
                      ...styles.submitButton,
                      ...(versionSubmitting
                        ? styles.disabledButton
                        : {}),
                    }}
                  >
                    {versionSubmitting
                      ? "제출 중..."
                      : "버전 생성"}
                  </button>
                </div>
              </form>
            )}

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

  sectionHeader: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    flexWrap: "wrap",
    gap: 12,
    marginBottom: 12,
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

  createVersionButton: {
    padding: "8px 14px",
    border: "1px solid #1976d2",
    borderRadius: 6,
    backgroundColor: "#ffffff",
    color: "#1976d2",
    cursor: "pointer",
    fontSize: 13,
    fontWeight: 600,
  },

  versionForm: {
    display: "flex",
    flexDirection: "column",
    gap: 14,
    marginBottom: 20,
    padding: 16,
    border: "1px solid #d6e4f0",
    borderRadius: 8,
    backgroundColor: "#f8fbff",
  },

  formHeading: {
    margin: 0,
    fontSize: 15,
  },

  formField: {
    display: "flex",
    flexDirection: "column",
    gap: 6,
    fontSize: 13,
    fontWeight: 600,
  },

  requiredMark: {
    color: "#c62828",
  },

  formInput: {
    boxSizing: "border-box",
    width: "100%",
    minHeight: 38,
    padding: "8px 10px",
    border: "1px solid #cccccc",
    borderRadius: 5,
    backgroundColor: "#ffffff",
    color: "#333333",
    fontSize: 14,
    fontWeight: 400,
  },

  formTextarea: {
    boxSizing: "border-box",
    width: "100%",
    padding: "10px",
    border: "1px solid #cccccc",
    borderRadius: 5,
    backgroundColor: "#ffffff",
    color: "#333333",
    fontSize: 14,
    fontWeight: 400,
    resize: "vertical",
    fontFamily: "inherit",
  },

  runtimeFields: {
    display: "flex",
    flexDirection: "column",
    gap: 12,
    padding: 14,
    border: "1px solid #e0e0e0",
    borderRadius: 6,
    backgroundColor: "#ffffff",
  },

  runtimeHeading: {
    margin: 0,
    fontSize: 14,
  },

  runtimeDescription: {
    margin: 0,
    color: "#666666",
    fontSize: 12,
    lineHeight: 1.5,
    fontWeight: 400,
  },

  formActions: {
    display: "flex",
    justifyContent: "flex-end",
    gap: 8,
    marginTop: 4,
  },

  cancelButton: {
    padding: "9px 16px",
    border: "1px solid #cccccc",
    borderRadius: 6,
    backgroundColor: "#ffffff",
    color: "#555555",
    cursor: "pointer",
  },

  submitButton: {
    padding: "9px 16px",
    border: "1px solid #1976d2",
    borderRadius: 6,
    backgroundColor: "#1976d2",
    color: "#ffffff",
    cursor: "pointer",
    fontWeight: 600,
  },

  disabledButton: {
    borderColor: "#bdbdbd",
    backgroundColor: "#bdbdbd",
    cursor: "not-allowed",
  },

  successBox: {
    marginBottom: 12,
    padding: 12,
    border: "1px solid #c8e6c9",
    borderRadius: 6,
    backgroundColor: "#e8f5e9",
    color: "#2e7d32",
    fontSize: 14,
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