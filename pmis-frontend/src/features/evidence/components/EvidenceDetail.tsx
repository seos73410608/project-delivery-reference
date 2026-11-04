
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
  verifyEvidenceVersion,
  uploadEvidenceVersionFile,
  getEvidenceVersionFileMetadata,
  downloadEvidenceVersionFile,
  MAX_EVIDENCE_FILE_SIZE,
  type EvidenceOcrJob,
  type EvidenceVersionCreateRequest,
  type EvidenceVersionVerificationRequest,
  type EvidenceVersionFileMetadata,
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

function formatFileSize(size: number): string {
  if (!Number.isFinite(size) || size < 0) {
    return "-";
  }

  if (size < 1024) {
    return `${size} B`;
  }

  if (size < 1024 * 1024) {
    return `${(size / 1024).toFixed(1)} KB`;
  }

  return `${(size / (1024 * 1024)).toFixed(2)} MB`;
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
  // OCR 전용 파일 입력
  const ocrFileInputRef = useRef<HTMLInputElement>(null);

  // Evidence 파일 업로드 전용 파일 입력
  const evidenceFileInputRef = useRef<HTMLInputElement>(null);

  const requestSequenceRef = useRef(0);
  const versionListRequestSequenceRef = useRef(0);
  const versionDetailRequestSequenceRef = useRef(0);
  const versionCreateRequestSequenceRef = useRef(0);
  const verificationRequestSequenceRef = useRef(0);
  const fileMetadataRequestSequenceRef = useRef(0);

  const mountedRef = useRef(true);

  const [currentEvidence, setCurrentEvidence] =
    useState<Evidence | null>(evidence);

  // OCR 상태
  const [ocrStatus, setOcrStatus] = useState<OcrStatus>("IDLE");
  const [ocrJob, setOcrJob] = useState<EvidenceOcrJob | null>(null);
  const [ocrError, setOcrError] = useState("");

  // Evidence 버전 목록
  const [versions, setVersions] = useState<EvidenceVersion[]>([]);
  const [versionsLoading, setVersionsLoading] =
    useState<VersionLoadingStatus>("IDLE");
  const [versionsError, setVersionsError] = useState("");

  const [selectedVersionNumber, setSelectedVersionNumber] =
    useState<number | null>(null);

  const [selectedVersion, setSelectedVersion] =
    useState<EvidenceVersion | null>(null);

  const [verificationHistory, setVerificationHistory] =
    useState<EvidenceVerificationHistory[]>([]);

  const [versionDetailLoading, setVersionDetailLoading] =
    useState(false);
  const [versionDetailError, setVersionDetailError] = useState("");

  const [historyLoading, setHistoryLoading] = useState(false);
  const [historyError, setHistoryError] = useState("");

  // 신규 버전 폼
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

  // 신규 버전 바이너리 파일
  const [versionFile, setVersionFile] = useState<File | null>(null);

  // 버전 파일 메타데이터
  const [fileMetadata, setFileMetadata] =
    useState<EvidenceVersionFileMetadata | null>(null);

  const [fileMetadataLoading, setFileMetadataLoading] =
    useState(false);

  const [fileMetadataError, setFileMetadataError] = useState("");

  const [fileDownloadLoading, setFileDownloadLoading] =
    useState(false);

  const [fileDownloadError, setFileDownloadError] = useState("");

  // 버전별 검증
  const [verificationSubmitting, setVerificationSubmitting] =
    useState(false);

  const [verificationStatusInput, setVerificationStatusInput] =
    useState<"APPROVED" | "REJECTED">("APPROVED");

  const [verificationRemarkInput, setVerificationRemarkInput] =
    useState("");

  const [verificationSubmitError, setVerificationSubmitError] =
    useState("");

  const [verificationSubmitSuccess, setVerificationSubmitSuccess] =
    useState("");

  useEffect(() => {
    mountedRef.current = true;

    return () => {
      mountedRef.current = false;

      requestSequenceRef.current += 1;
      versionListRequestSequenceRef.current += 1;
      versionDetailRequestSequenceRef.current += 1;
      versionCreateRequestSequenceRef.current += 1;
      verificationRequestSequenceRef.current += 1;
      fileMetadataRequestSequenceRef.current += 1;
    };
  }, []);

  useEffect(() => {
    setCurrentEvidence(evidence);
  }, [evidence]);

  const isCurrentRequest = useCallback(
    (requestSequence: number): boolean =>
      mountedRef.current &&
      requestSequenceRef.current === requestSequence,
    [],
  );

  const isCurrentVersionListRequest = useCallback(
    (requestSequence: number): boolean =>
      mountedRef.current &&
      versionListRequestSequenceRef.current === requestSequence,
    [],
  );

  const isCurrentVersionDetailRequest = useCallback(
    (requestSequence: number): boolean =>
      mountedRef.current &&
      versionDetailRequestSequenceRef.current === requestSequence,
    [],
  );

  // Evidence 변경 시 화면 상태 초기화
  useEffect(() => {
    requestSequenceRef.current += 1;
    versionListRequestSequenceRef.current += 1;
    versionDetailRequestSequenceRef.current += 1;
    versionCreateRequestSequenceRef.current += 1;
    verificationRequestSequenceRef.current += 1;
    fileMetadataRequestSequenceRef.current += 1;

    setOcrStatus("IDLE");
    setOcrJob(null);
    setOcrError("");

    setVersions([]);
    setVersionsLoading("IDLE");
    setVersionsError("");

    setSelectedVersionNumber(null);
    setSelectedVersion(null);
    setVerificationHistory([]);

    setVersionDetailLoading(false);
    setVersionDetailError("");

    setHistoryLoading(false);
    setHistoryError("");

    setIsVersionFormOpen(false);
    setVersionForm(createEmptyVersionForm());
    setVersionFile(null);
    setVersionSubmitting(false);
    setVersionSubmitError("");
    setVersionSubmitSuccess("");

    setFileMetadata(null);
    setFileMetadataLoading(false);
    setFileMetadataError("");
    setFileDownloadLoading(false);
    setFileDownloadError("");

    setVerificationSubmitting(false);
    setVerificationStatusInput("APPROVED");
    setVerificationRemarkInput("");
    setVerificationSubmitError("");
    setVerificationSubmitSuccess("");

    if (ocrFileInputRef.current) {
      ocrFileInputRef.current.value = "";
    }

    if (evidenceFileInputRef.current) {
      evidenceFileInputRef.current.value = "";
    }
  }, [evidence?.id]);

  // 버전 목록 조회
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

  // 선택 버전 상세 및 검증 이력 조회
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

      if (versionResult.status === "fulfilled") {
        setSelectedVersion(versionResult.value);
        setVersionDetailLoading(false);
      } else {
        setVersionDetailLoading(false);
        setVersionDetailError(
          getErrorMessage(versionResult.reason),
        );
      }

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

  // 선택 버전 파일 메타데이터 조회
  useEffect(() => {
    if (!evidence || selectedVersionNumber === null) {
      fileMetadataRequestSequenceRef.current += 1;
      setFileMetadata(null);
      setFileMetadataLoading(false);
      setFileMetadataError("");
      return;
    }

    const evidenceId = evidence.id;
    const versionNumber = selectedVersionNumber;

    const requestSequence =
      ++fileMetadataRequestSequenceRef.current;

    async function loadFileMetadata() {
      setFileMetadata(null);
      setFileMetadataLoading(true);
      setFileMetadataError("");

      try {
        const metadata = await getEvidenceVersionFileMetadata(
          evidenceId,
          versionNumber,
        );

        if (
          !mountedRef.current ||
          fileMetadataRequestSequenceRef.current !== requestSequence
        ) {
          return;
        }

        setFileMetadata(metadata);
      } catch (error: unknown) {
        if (
          !mountedRef.current ||
          fileMetadataRequestSequenceRef.current !== requestSequence
        ) {
          return;
        }

        setFileMetadataError(getErrorMessage(error));
      } finally {
        if (
          mountedRef.current &&
          fileMetadataRequestSequenceRef.current === requestSequence
        ) {
          setFileMetadataLoading(false);
        }
      }
    }

    void loadFileMetadata();

    return () => {
      fileMetadataRequestSequenceRef.current += 1;
    };
  }, [evidence?.id, selectedVersionNumber]);

  // 신규 버전 폼 열기
  function handleOpenVersionForm() {
    if (!evidence || versionSubmitting) {
      return;
    }

    setVersionForm({
      evidenceType:
        selectedVersion?.evidenceType ||
        evidence.evidenceType ||
        "DOCUMENT",
      title:
        selectedVersion?.title ||
        evidence.title ||
        "",
      description:
        selectedVersion?.description ||
        evidence.description ||
        "",
      fileName: "",
      filePath: "",
      sourceFileId: "",
      executionUuid: "",
      sourceJobId: "",
    });

    setVersionFile(null);
    setVersionSubmitError("");
    setVersionSubmitSuccess("");
    setIsVersionFormOpen(true);
  }

  function handleCloseVersionForm() {
    if (versionSubmitting) {
      return;
    }

    setIsVersionFormOpen(false);
    setVersionSubmitError("");
    setVersionFile(null);

    if (evidenceFileInputRef.current) {
      evidenceFileInputRef.current.value = "";
    }
  }

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

  // 신규 버전 파일 선택
  function handleVersionFileChange(
    event: ChangeEvent<HTMLInputElement>,
  ) {
    const file = event.target.files?.[0] ?? null;

    setVersionSubmitError("");
    setVersionSubmitSuccess("");

    if (!file) {
      setVersionFile(null);
      return;
    }

    if (file.size <= 0) {
      setVersionSubmitError("빈 파일은 업로드할 수 없습니다.");
      setVersionFile(null);
      event.target.value = "";
      return;
    }

    if (file.size > MAX_EVIDENCE_FILE_SIZE) {
      setVersionSubmitError(
        "파일 크기는 50 MiB 이하여야 합니다.",
      );
      setVersionFile(null);
      event.target.value = "";
      return;
    }

    setVersionFile(file);

    // 선택 파일명을 기존 파일명 입력값에도 반영
    setVersionForm((previous) => ({
      ...previous,
      fileName: file.name,
    }));
  }

  // 버전 목록 새로고침
  async function refreshVersionList(
    evidenceId: number,
    createdVersionNumber?: number,
  ) {
    const refreshedVersions =
      await getEvidenceVersions(evidenceId);

    if (!mountedRef.current) {
      return;
    }

    setVersions(refreshedVersions);
    setVersionsLoading("SUCCEEDED");

    if (createdVersionNumber !== undefined) {
      setSelectedVersionNumber(createdVersionNumber);
    }
  }

  // 신규 버전 생성 또는 파일 업로드
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

    setVersionSubmitting(true);
    setVersionSubmitError("");
    setVersionSubmitSuccess("");

    try {
      let createdVersion: EvidenceVersion;

      if (versionFile) {
        // 파일이 선택된 경우:
        // 업로드 API가 파일 업로드와 버전 생성을 함께 처리한다.
        createdVersion = await uploadEvidenceVersionFile(
          evidenceId,
          {
            file: versionFile,
            evidenceType: versionForm.evidenceType,
            title,
            description:
              versionForm.description.trim() || null,
          },
        );
      } else {
        // 파일이 선택되지 않은 경우:
        // 기존 메타데이터 전용 버전 생성 기능을 유지한다.
        const request: EvidenceVersionCreateRequest = {
          evidenceType: versionForm.evidenceType,
          title,
          description:
            versionForm.description.trim() || null,
          fileName: versionForm.fileName.trim() || null,
          filePath: versionForm.filePath.trim() || null,
          sourceFileId:
            versionForm.sourceFileId.trim() || null,
          executionUuid:
            versionForm.executionUuid.trim() || null,
          sourceJobId:
            versionForm.sourceJobId.trim() || null,
        };

        createdVersion = await createEvidenceVersion(
          evidenceId,
          request,
        );
      }

      if (
        !mountedRef.current ||
        versionCreateRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      versionListRequestSequenceRef.current += 1;

      setVersionsLoading("LOADING");
      setVersionsError("");

      await refreshVersionList(
        evidenceId,
        createdVersion.versionNumber,
      );

      if (
        !mountedRef.current ||
        versionCreateRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      setVersionSubmitSuccess(
        versionFile
          ? `Version ${createdVersion.versionNumber} 파일 업로드 및 버전 생성이 완료되었습니다.`
          : `Version ${createdVersion.versionNumber} 생성이 완료되었습니다.`,
      );

      setIsVersionFormOpen(false);
      setVersionForm(createEmptyVersionForm());
      setVersionFile(null);

      if (evidenceFileInputRef.current) {
        evidenceFileInputRef.current.value = "";
      }
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

  const statusStyle = currentEvidence
    ? getVerificationStatusStyle(
        currentEvidence.verificationStatus,
      )
    : undefined;

  const displayedFileName =
    fileMetadata?.original_filename ||
    selectedVersion?.fileName ||
    currentEvidence?.fileName ||
    null;

  const displayedFilePath =
    selectedVersion?.filePath ||
    currentEvidence?.filePath ||
    null;

  // 선택 버전 파일 다운로드
  async function handleDownloadVersionFile() {
    if (!evidence || selectedVersionNumber === null) {
      return;
    }

    setFileDownloadLoading(true);
    setFileDownloadError("");

    try {
      const blob = await downloadEvidenceVersionFile(
        evidence.id,
        selectedVersionNumber,
      );

      const objectUrl = window.URL.createObjectURL(blob);
      const anchor = document.createElement("a");

      anchor.href = objectUrl;
      anchor.download =
        fileMetadata?.original_filename ||
        selectedVersion?.fileName ||
        `evidence-${evidence.id}-version-${selectedVersionNumber}`;

      document.body.appendChild(anchor);
      anchor.click();
      anchor.remove();

      window.URL.revokeObjectURL(objectUrl);
    } catch (error: unknown) {
      setFileDownloadError(getErrorMessage(error));
    } finally {
      setFileDownloadLoading(false);
    }
  }

  // OCR 실행 버튼
  function handleOcrButtonClick() {
    if (!evidence || ocrStatus === "PENDING") {
      return;
    }

    setOcrError("");
    ocrFileInputRef.current?.click();
  }

  // 버전 선택 변경
  function handleVersionChange(
    event: ChangeEvent<HTMLSelectElement>,
  ) {
    const versionNumber = Number(event.target.value);

    if (!Number.isInteger(versionNumber)) {
      return;
    }

    setVerificationSubmitError("");
    setVerificationSubmitSuccess("");
    setSelectedVersionNumber(versionNumber);
  }

  // 선택 버전 검증
  async function handleVerifySelectedVersion(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault();

    if (
      !evidence ||
      !selectedVersion ||
      selectedVersionNumber === null ||
      verificationSubmitting
    ) {
      return;
    }

    const evidenceId = evidence.id;
    const versionNumber = selectedVersionNumber;
    const remark = verificationRemarkInput.trim();

    if (
      verificationStatusInput === "REJECTED" &&
      !remark
    ) {
      setVerificationSubmitError(
        "반려 처리 시 검증 의견을 입력해야 합니다.",
      );
      return;
    }

    const request: EvidenceVersionVerificationRequest = {
      status: verificationStatusInput,
      remark: remark || null,
    };

    const requestSequence =
      ++verificationRequestSequenceRef.current;

    setVerificationSubmitting(true);
    setVerificationSubmitError("");
    setVerificationSubmitSuccess("");

    try {
      const updatedEvidence = await verifyEvidenceVersion(
        evidenceId,
        versionNumber,
        request,
      );

      if (
        !mountedRef.current ||
        verificationRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      setCurrentEvidence(updatedEvidence);

      const listRequestSequence =
        ++versionListRequestSequenceRef.current;

      const detailRequestSequence =
        ++versionDetailRequestSequenceRef.current;

      setVersionsLoading("LOADING");
      setVersionsError("");

      setVersionDetailLoading(true);
      setVersionDetailError("");

      setHistoryLoading(true);
      setHistoryError("");

      const [
        versionsResult,
        versionResult,
        historyResult,
      ] = await Promise.allSettled([
        getEvidenceVersions(evidenceId),
        getEvidenceVersion(evidenceId, versionNumber),
        getEvidenceVersionVerificationHistory(
          evidenceId,
          versionNumber,
        ),
      ]);

      if (
        !mountedRef.current ||
        verificationRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      if (
        versionListRequestSequenceRef.current ===
        listRequestSequence
      ) {
        if (versionsResult.status === "fulfilled") {
          setVersions(versionsResult.value);
          setVersionsLoading("SUCCEEDED");
        } else {
          setVersionsLoading("FAILED");
          setVersionsError(
            getErrorMessage(versionsResult.reason),
          );
        }
      }

      if (
        versionDetailRequestSequenceRef.current ===
        detailRequestSequence
      ) {
        if (versionResult.status === "fulfilled") {
          setSelectedVersion(versionResult.value);
          setVersionDetailLoading(false);
        } else {
          setVersionDetailLoading(false);
          setVersionDetailError(
            getErrorMessage(versionResult.reason),
          );
        }

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

      setVerificationSubmitSuccess(
        `Version ${versionNumber} ${
          verificationStatusInput === "APPROVED"
            ? "승인"
            : "반려"
        } 처리가 완료되었습니다.`,
      );

      setVerificationRemarkInput("");
    } catch (error: unknown) {
      if (
        !mountedRef.current ||
        verificationRequestSequenceRef.current !== requestSequence
      ) {
        return;
      }

      setVerificationSubmitError(
        getErrorMessage(error),
      );
    } finally {
      if (
        mountedRef.current &&
        verificationRequestSequenceRef.current === requestSequence
      ) {
        setVerificationSubmitting(false);
      }
    }
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
      const uploadedFile = await uploadOcrFile(file);

      if (!isCurrentRequest(requestSequence)) {
        return;
      }

      if (!uploadedFile.file_id) {
        throw new Error(
          "파일 업로드 응답에 file_id가 없습니다.",
        );
      }

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

      {!currentEvidence ? (
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
                {currentEvidence.evidenceKey ||
                  `#${currentEvidence.id}`}
              </span>

              <strong>Title</strong>
              <span>{currentEvidence.title || "-"}</span>

              <strong>Evidence Type</strong>
              <span>{currentEvidence.evidenceType || "-"}</span>

              <strong>Project ID</strong>
              <span>{currentEvidence.projectId}</span>

              <strong>Requirement ID</strong>
              <span>{currentEvidence.requirementId}</span>

              <strong>WBS ID</strong>
              <span>{currentEvidence.wbsId ?? "-"}</span>

              <strong>Status</strong>
              <span>
                <span
                  style={{
                    ...styles.statusBadge,
                    ...statusStyle,
                  }}
                >
                  {getVerificationStatusLabel(
                    currentEvidence.verificationStatus,
                  )}
                </span>
              </span>

              <strong>Submitted By</strong>
              <span>{currentEvidence.submittedByName || "-"}</span>

              <strong>Submitted Date</strong>
              <span>
                {formatDate(currentEvidence.submittedAt)}
              </span>

              <strong>Verified By</strong>
              <span>{currentEvidence.verifiedByName || "-"}</span>

              <strong>Verified Date</strong>
              <span>{formatDate(currentEvidence.verifiedAt)}</span>

              <strong>Description</strong>
              <span style={styles.longText}>
                {currentEvidence.description || "-"}
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
                    증적 유형
                    <span style={styles.requiredMark}> *</span>
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
                    제목
                    <span style={styles.requiredMark}> *</span>
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

                {/* 실제 바이너리 파일 업로드 */}
                <div style={styles.uploadBox}>
                  <h5 style={styles.runtimeHeading}>
                    증적 파일 업로드
                  </h5>

                  <p style={styles.runtimeDescription}>
                    파일을 선택하면 파일 업로드와 Evidence 버전 생성이
                    함께 처리됩니다. 최대 파일 크기는 50 MiB입니다.
                    파일을 선택하지 않으면 기존 메타데이터 전용 버전
                    생성 방식으로 처리됩니다.
                  </p>

                  <input
                    ref={evidenceFileInputRef}
                    id="version-binary-file"
                    type="file"
                    onChange={handleVersionFileChange}
                    disabled={versionSubmitting}
                    style={styles.formInput}
                  />

                  {versionFile && (
                    <div style={styles.selectedFileInfo}>
                      <strong>선택 파일:</strong>{" "}
                      {versionFile.name}
                      <br />
                      <strong>파일 크기:</strong>{" "}
                      {formatFileSize(versionFile.size)}
                    </div>
                  )}
                </div>

                {/* 기존 메타데이터 전용 등록 필드 */}
                {!versionFile && (
                  <>
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
                  </>
                )}

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
                      ? versionFile
                        ? "파일 업로드 및 버전 생성 중..."
                        : "제출 중..."
                      : versionFile
                        ? "파일 업로드 및 버전 생성"
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
                      onClick={() => {
                        setVerificationSubmitError("");
                        setVerificationSubmitSuccess("");
                        setSelectedVersionNumber(
                          version.versionNumber,
                        );
                      }}
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

          {/* 선택 버전 상세 및 검증 이력 */}
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

              {/* 선택 버전 파일 메타데이터 및 다운로드 */}
              <section style={styles.section}>
                <h3 style={styles.sectionHeading}>
                  Version {selectedVersionNumber} File
                </h3>

                {fileMetadataLoading && (
                  <p style={styles.mutedText}>
                    파일 메타데이터를 조회하고 있습니다.
                  </p>
                )}

                {fileMetadataError && (
                  <div style={styles.warningBox}>
                    파일 메타데이터를 조회하지 못했습니다.
                    <br />
                    {fileMetadataError}
                    <p style={styles.runtimeDescription}>
                      해당 버전에 실제 업로드 파일이 없는 경우
                      메타데이터 조회가 실패할 수 있습니다.
                    </p>
                  </div>
                )}

                {fileMetadata && (
                  <>
                    <div style={styles.infoGrid}>
                      <strong>Original Filename</strong>
                      <span style={styles.longText}>
                        {fileMetadata.original_filename}
                      </span>

                      <strong>Content Type</strong>
                      <span>{fileMetadata.content_type}</span>

                      <strong>File Size</strong>
                      <span>
                        {formatFileSize(fileMetadata.file_size)}
                      </span>

                      <strong>SHA-256</strong>
                      <span style={styles.longText}>
                        {fileMetadata.sha256}
                      </span>

                      <strong>File Created At</strong>
                      <span>
                        {formatDate(fileMetadata.created_at)}
                      </span>

                      <strong>Runtime File ID</strong>
                      <span style={styles.longText}>
                        {fileMetadata.file_id}
                      </span>
                    </div>

                    <button
                      type="button"
                      onClick={handleDownloadVersionFile}
                      disabled={fileDownloadLoading}
                      style={{
                        ...styles.downloadButton,
                        ...(fileDownloadLoading
                          ? styles.disabledButton
                          : {}),
                      }}
                    >
                      {fileDownloadLoading
                        ? "다운로드 준비 중..."
                        : "파일 다운로드"}
                    </button>
                  </>
                )}

                {!fileMetadataLoading &&
                  !fileMetadata &&
                  !fileMetadataError && (
                    <p style={styles.mutedText}>
                      파일 메타데이터가 없습니다.
                    </p>
                  )}

                {fileDownloadError && (
                  <div role="alert" style={styles.errorBox}>
                    파일 다운로드 실패: {fileDownloadError}
                  </div>
                )}
              </section>

              {/* 선택 버전 검증 처리 */}
              <section style={styles.section}>
                <h3 style={styles.sectionHeading}>
                  Version {selectedVersionNumber} Verification
                </h3>

                <p style={styles.verificationDescription}>
                  선택한 버전에 대해서만 검증을 수행합니다.
                  과거 버전을 검증해도 Evidence 대표 검증 상태는
                  변경되지 않습니다.
                </p>

                {verificationSubmitSuccess && (
                  <div role="status" style={styles.successBox}>
                    {verificationSubmitSuccess}
                  </div>
                )}

                {verificationSubmitError && (
                  <div role="alert" style={styles.errorBox}>
                    {verificationSubmitError}
                  </div>
                )}

                {selectedVersion && (
                  <form
                    onSubmit={handleVerifySelectedVersion}
                    style={styles.verificationForm}
                  >
                    <div style={styles.formField}>
                      <label htmlFor="version-verification-status">
                        검증 결과
                      </label>

                      <select
                        id="version-verification-status"
                        value={verificationStatusInput}
                        onChange={(event) => {
                          setVerificationStatusInput(
                            event.target.value as
                              | "APPROVED"
                              | "REJECTED",
                          );

                          setVerificationSubmitError("");
                          setVerificationSubmitSuccess("");
                        }}
                        disabled={verificationSubmitting}
                        style={styles.formInput}
                      >
                        <option value="APPROVED">승인</option>
                        <option value="REJECTED">반려</option>
                      </select>
                    </div>

                    <div style={styles.formField}>
                      <label htmlFor="version-verification-remark">
                        검증 의견
                        {verificationStatusInput === "REJECTED" && (
                          <span style={styles.requiredMark}> *</span>
                        )}
                      </label>

                      <textarea
                        id="version-verification-remark"
                        value={verificationRemarkInput}
                        onChange={(event) => {
                          setVerificationRemarkInput(
                            event.target.value,
                          );

                          setVerificationSubmitError("");
                          setVerificationSubmitSuccess("");
                        }}
                        rows={3}
                        maxLength={2000}
                        disabled={verificationSubmitting}
                        placeholder={
                          verificationStatusInput === "REJECTED"
                            ? "반려 사유를 입력해 주세요."
                            : "검증 의견을 입력할 수 있습니다."
                        }
                        style={styles.formTextarea}
                      />

                      {verificationStatusInput === "REJECTED" && (
                        <span style={styles.fieldHelp}>
                          반려 처리 시 검증 의견은 필수입니다.
                        </span>
                      )}
                    </div>

                    <div style={styles.formActions}>
                      <button
                        type="submit"
                        disabled={
                          verificationSubmitting ||
                          versionDetailLoading ||
                          !selectedVersion
                        }
                        style={{
                          ...styles.submitButton,
                          ...(verificationSubmitting
                            ? styles.disabledButton
                            : {}),
                        }}
                      >
                        {verificationSubmitting
                          ? "검증 처리 중..."
                          : verificationStatusInput === "APPROVED"
                            ? "선택 버전 승인"
                            : "선택 버전 반려"}
                      </button>
                    </div>
                  </form>
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

          {/* OCR 첨부 파일 */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>
              OCR Input File
            </h3>

            <div style={styles.fileCard}>
              <div style={styles.fileName}>
                {displayedFileName || "선택된 증적 파일 없음"}
              </div>

              <div style={styles.fileDescription}>
                {displayedFilePath
                  ? `파일 경로: ${displayedFilePath}`
                  : "OCR을 실행할 파일을 선택해 주세요."}
              </div>

              <input
                ref={ocrFileInputRef}
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

          {/* Evidence 대표 검증 정보 */}
          <section style={styles.section}>
            <h3 style={styles.sectionHeading}>
              Evidence Verification Summary
            </h3>

            <p style={styles.verificationDescription}>
              아래 정보는 선택된 버전이 아닌 Evidence 전체의 대표
              검증 상태입니다.
            </p>

            <div style={styles.infoGrid}>
              <strong>Verifier</strong>
              <span>{currentEvidence.verifiedByName || "-"}</span>

              <strong>Result</strong>
              <span
                style={{
                  ...styles.verificationResult,
                  color: statusStyle?.color,
                }}
              >
                {getVerificationStatusLabel(
                  currentEvidence.verificationStatus,
                )}
              </span>

              <strong>Comment</strong>
              <span style={styles.longText}>
                {currentEvidence.verificationRemark || "-"}
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

  uploadBox: {
    display: "flex",
    flexDirection: "column",
    gap: 10,
    padding: 14,
    border: "1px solid #bbdefb",
    borderRadius: 6,
    backgroundColor: "#ffffff",
  },

  selectedFileInfo: {
    padding: 10,
    borderRadius: 5,
    backgroundColor: "#f1f8e9",
    color: "#33691e",
    fontSize: 13,
    overflowWrap: "anywhere",
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

  downloadButton: {
    marginTop: 16,
    padding: "9px 16px",
    border: "1px solid #2e7d32",
    borderRadius: 6,
    backgroundColor: "#2e7d32",
    color: "#ffffff",
    cursor: "pointer",
    fontWeight: 600,
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

  verificationDescription: {
    marginTop: 0,
    marginBottom: 14,
    color: "#666666",
    fontSize: 13,
    lineHeight: 1.6,
  },

  verificationForm: {
    display: "flex",
    flexDirection: "column",
    gap: 14,
    padding: 16,
    border: "1px solid #d6e4f0",
    borderRadius: 8,
    backgroundColor: "#f8fbff",
  },

  fieldHelp: {
    color: "#c62828",
    fontSize: 12,
    fontWeight: 400,
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