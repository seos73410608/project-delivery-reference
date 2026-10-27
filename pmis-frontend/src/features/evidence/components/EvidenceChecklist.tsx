import {
  useCallback,
  useEffect,
  useState,
} from 'react';
import type {
  CSSProperties,
  FormEvent,
} from 'react';

import {
  createEvidence,
  getRequirementEvidence,
} from '@/features/evidence/api/evidenceApi';

import {
  createEvidenceRequirement,
  getProjectEvidenceRequirements,
  updateEvidenceRequirement,
} from '@/features/evidence/api/evidenceRequirementApi';

import type {
  Evidence,
  EvidenceType,
} from '@/features/evidence/types/evidence';

import type {
  EvidenceRequirement,
  EvidenceRequirementCreateRequest,
  EvidenceRequirementUpdateRequest,
  RequirementStatus,
  RequirementType,
} from '@/features/evidence/types/evidenceRequirement';

interface EvidenceChecklistProps {
  projectId?: number;
  selectedEvidenceId?: number | null;
  onSelectEvidence?: (evidence: Evidence) => void;
}

type RequirementForm = EvidenceRequirementCreateRequest;

interface EvidenceForm {
  evidenceType: EvidenceType;
  title: string;
  description: string;
  wbsId: string;
  fileName: string;
  filePath: string;
  sourceFileId: string;
  executionUuid: string;
  sourceJobId: string;
  submittedAt: string;
}

const INITIAL_REQUIREMENT_FORM: RequirementForm = {
  wbsId: null,
  title: '',
  description: '',
  requirementType: 'INSTALLATION',
  required: true,
  dueDate: '',
  sortOrder: 1,
};

const INITIAL_EVIDENCE_FORM: EvidenceForm = {
  evidenceType: 'DOCUMENT',
  title: '',
  description: '',
  wbsId: '',
  fileName: '',
  filePath: '',
  sourceFileId: '',
  executionUuid: '',
  sourceJobId: '',
  submittedAt: '',
};

const REQUIREMENT_STATUS_CONFIG: Record<
  RequirementStatus,
  { label: string; color: string; background: string }
> = {
  PENDING: {
    label: '대기',
    color: '#92400e',
    background: '#fef3c7',
  },
  PRESENT: {
    label: '제출 완료',
    color: '#166534',
    background: '#dcfce7',
  },
  MISSING: {
    label: '미제출',
    color: '#991b1b',
    background: '#fee2e2',
  },
  NOT_REQUIRED: {
    label: '불필요',
    color: '#475569',
    background: '#e2e8f0',
  },
};

const REQUIREMENT_TYPE_OPTIONS: Array<{
  value: RequirementType;
  label: string;
}> = [
  { value: 'INSTALLATION', label: '설치' },
  { value: 'CONFIGURATION', label: '구성' },
  { value: 'ACCOUNT', label: '계정' },
  { value: 'TEST', label: '테스트' },
  { value: 'MIGRATION', label: '이관' },
  { value: 'INSPECTION', label: '점검' },
  { value: 'REPORT', label: '보고서' },
  { value: 'OPERATION', label: '운영' },
  { value: 'SECURITY', label: '보안' },
  { value: 'OTHER', label: '기타' },
];

const EVIDENCE_TYPE_OPTIONS: Array<{
  value: EvidenceType;
  label: string;
}> = [
  { value: 'DOCUMENT', label: '문서' },
  { value: 'REPORT', label: '보고서' },
  { value: 'SCREENSHOT', label: '스크린샷' },
  { value: 'IMAGE', label: '이미지' },
  { value: 'LOG', label: '로그' },
  { value: 'TEST_RESULT', label: '테스트 결과' },
  { value: 'CONFIGURATION', label: '구성 정보' },
  { value: 'OTHER', label: '기타' },
];

const VERIFICATION_STATUS_LABELS: Record<string, string> = {
  PENDING: '검토 대기',
  VERIFIED: '검증 완료',
  REJECTED: '반려',
};

function formatDate(value?: string | null): string {
  if (!value) {
    return '-';
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleDateString('ko-KR');
}

function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  return '요청 처리 중 오류가 발생했습니다.';
}

export default function EvidenceChecklist({
  projectId,
  selectedEvidenceId,
  onSelectEvidence,
}: EvidenceChecklistProps) {
  const [requirements, setRequirements] = useState<
    EvidenceRequirement[]
  >([]);

  const [evidenceByRequirement, setEvidenceByRequirement] =
    useState<Record<number, Evidence[]>>({});

  const [loading, setLoading] = useState(false);
  const [evidenceLoading, setEvidenceLoading] = useState(false);

  const [error, setError] = useState<string | null>(null);
  const [evidenceError, setEvidenceError] = useState<string | null>(
    null,
  );

  // 요구사항 생성 및 수정
  const [showRequirementForm, setShowRequirementForm] = useState(false);
  const [editingRequirementId, setEditingRequirementId] =
    useState<number | null>(null);

  const [requirementForm, setRequirementForm] =
    useState<RequirementForm>(INITIAL_REQUIREMENT_FORM);

  const [savingRequirement, setSavingRequirement] = useState(false);
  const [requirementFormError, setRequirementFormError] =
    useState<string | null>(null);

  const [requirementFormSuccess, setRequirementFormSuccess] =
    useState<string | null>(null);

  // 증적 등록
  const [activeEvidenceRequirementId, setActiveEvidenceRequirementId] =
    useState<number | null>(null);

  const [evidenceForm, setEvidenceForm] = useState<EvidenceForm>(
    INITIAL_EVIDENCE_FORM,
  );

  const [savingEvidence, setSavingEvidence] = useState(false);
  const [evidenceFormError, setEvidenceFormError] =
    useState<string | null>(null);

  const [evidenceFormSuccess, setEvidenceFormSuccess] =
    useState<string | null>(null);

  /**
   * 프로젝트의 요구사항 및 각 요구사항에 연결된 증적을 조회한다.
   */
  const fetchRequirements = useCallback(async () => {
    if (!projectId) {
      setRequirements([]);
      setEvidenceByRequirement({});
      setError(null);
      setEvidenceError(null);
      return;
    }

    setLoading(true);
    setError(null);
    setEvidenceError(null);

    try {
      const result = await getProjectEvidenceRequirements(projectId);

      setRequirements(result);

      setEvidenceLoading(true);

      const entries = await Promise.all(
        result.map(async (requirement) => {
          try {
            const evidence = await getRequirementEvidence(
              requirement.id,
            );

            return [requirement.id, evidence] as const;
          } catch {
            return [requirement.id, [] as Evidence[]] as const;
          }
        }),
      );

      setEvidenceByRequirement(
        Object.fromEntries(entries) as Record<number, Evidence[]>,
      );
    } catch (fetchError) {
      setError(getErrorMessage(fetchError));
    } finally {
      setLoading(false);
      setEvidenceLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    void fetchRequirements();
  }, [fetchRequirements]);

  /**
   * 요구사항 생성 폼을 연다.
   */
  const openCreateRequirementForm = () => {
    setEditingRequirementId(null);
    setRequirementForm(INITIAL_REQUIREMENT_FORM);
    setRequirementFormError(null);
    setRequirementFormSuccess(null);
    setShowRequirementForm(true);
  };

  /**
   * 요구사항 수정 폼을 연다.
   */
  const openEditRequirementForm = (
    requirement: EvidenceRequirement,
  ) => {
    setEditingRequirementId(requirement.id);

    setRequirementForm({
      wbsId: requirement.wbsId ?? null,
      title: requirement.title,
      description: requirement.description ?? '',
      requirementType: requirement.requirementType,
      required: requirement.required,
      dueDate: requirement.dueDate ?? '',
      sortOrder: requirement.sortOrder,
    });

    setRequirementFormError(null);
    setRequirementFormSuccess(null);
    setShowRequirementForm(true);
  };

  const closeRequirementForm = () => {
    setShowRequirementForm(false);
    setEditingRequirementId(null);
    setRequirementForm(INITIAL_REQUIREMENT_FORM);
    setRequirementFormError(null);
  };

  /**
   * 요구사항 입력값을 갱신한다.
   */
  const handleRequirementFormChange = (
    event: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >,
  ) => {
    const { name, value } = event.target;

    if (name === 'required') {
      setRequirementForm((previous) => ({
        ...previous,
        required: (event.target as HTMLInputElement).checked,
      }));
      return;
    }

    if (name === 'sortOrder') {
      setRequirementForm((previous) => ({
        ...previous,
        sortOrder: Number(value),
      }));
      return;
    }

    if (name === 'wbsId') {
      setRequirementForm((previous) => ({
        ...previous,
        wbsId: value ? Number(value) : null,
      }));
      return;
    }

    setRequirementForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /**
   * 요구사항을 생성하거나 수정한다.
   */
  const handleRequirementSubmit = async (
    event: FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();

    if (!projectId) {
      setRequirementFormError('프로젝트를 먼저 선택해 주세요.');
      return;
    }

    if (!requirementForm.title.trim()) {
      setRequirementFormError('요구사항 제목을 입력해 주세요.');
      return;
    }

    setSavingRequirement(true);
    setRequirementFormError(null);
    setRequirementFormSuccess(null);

    try {
      if (editingRequirementId !== null) {
        const request: EvidenceRequirementUpdateRequest = {
          wbsId: requirementForm.wbsId,
          title: requirementForm.title.trim(),
          description: requirementForm.description?.trim() || null,
          requirementType: requirementForm.requirementType,
          required: requirementForm.required,
          dueDate: requirementForm.dueDate || null,
          sortOrder: requirementForm.sortOrder,
        };

        await updateEvidenceRequirement(
          editingRequirementId,
          request,
        );

        setRequirementFormSuccess('요구사항이 수정되었습니다.');
      } else {
        const request: EvidenceRequirementCreateRequest = {
          ...requirementForm,
          title: requirementForm.title.trim(),
          description: requirementForm.description?.trim() || null,
          dueDate: requirementForm.dueDate || null,
        };

        await createEvidenceRequirement(projectId, request);

        setRequirementFormSuccess('요구사항이 등록되었습니다.');
      }

      closeRequirementForm();
      await fetchRequirements();
    } catch (submitError) {
      setRequirementFormError(getErrorMessage(submitError));
    } finally {
      setSavingRequirement(false);
    }
  };

  /**
   * 특정 요구사항의 증적 등록 폼을 연다.
   */
  const openEvidenceForm = (
    requirement: EvidenceRequirement,
  ) => {
    setActiveEvidenceRequirementId(requirement.id);

    setEvidenceForm({
      ...INITIAL_EVIDENCE_FORM,
      wbsId: requirement.wbsId
        ? String(requirement.wbsId)
        : '',
    });

    setEvidenceFormError(null);
    setEvidenceFormSuccess(null);
  };

  const closeEvidenceForm = () => {
    setActiveEvidenceRequirementId(null);
    setEvidenceForm(INITIAL_EVIDENCE_FORM);
    setEvidenceFormError(null);
  };

  /**
   * 증적 등록 폼의 입력값을 갱신한다.
   */
  const handleEvidenceFormChange = (
    event: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >,
  ) => {
    const { name, value } = event.target;

    setEvidenceForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  /**
   * 증적을 등록한다.
   *
   * POST /api/evidence-requirements/{requirementId}/evidence
   * 성공 상태: 201 Created
   */
  const handleEvidenceSubmit = async (
    event: FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();

    if (activeEvidenceRequirementId === null) {
      setEvidenceFormError('증적을 등록할 요구사항을 선택해 주세요.');
      return;
    }

    if (!evidenceForm.title.trim()) {
      setEvidenceFormError('증적 제목을 입력해 주세요.');
      return;
    }

    if (evidenceForm.title.trim().length > 200) {
      setEvidenceFormError('증적 제목은 200자 이내로 입력해 주세요.');
      return;
    }

    setSavingEvidence(true);
    setEvidenceFormError(null);
    setEvidenceFormSuccess(null);

    try {
      const request = {
        evidenceType: evidenceForm.evidenceType,
        title: evidenceForm.title.trim(),
        ...(evidenceForm.description.trim()
          ? { description: evidenceForm.description.trim() }
          : {}),
        ...(evidenceForm.wbsId
          ? { wbsId: Number(evidenceForm.wbsId) }
          : {}),
        ...(evidenceForm.fileName.trim()
          ? { fileName: evidenceForm.fileName.trim() }
          : {}),
        ...(evidenceForm.filePath.trim()
          ? { filePath: evidenceForm.filePath.trim() }
          : {}),
        ...(evidenceForm.sourceFileId.trim()
          ? { sourceFileId: evidenceForm.sourceFileId.trim() }
          : {}),
        ...(evidenceForm.executionUuid.trim()
          ? { executionUuid: evidenceForm.executionUuid.trim() }
          : {}),
        ...(evidenceForm.sourceJobId.trim()
          ? { sourceJobId: evidenceForm.sourceJobId.trim() }
          : {}),
        ...(evidenceForm.submittedAt
          ? {
              submittedAt: new Date(
                evidenceForm.submittedAt,
              ).toISOString(),
            }
          : {}),
      };

      const createdEvidence = await createEvidence(
        activeEvidenceRequirementId,
        request,
      );

      setEvidenceFormSuccess('증적이 등록되었습니다.');

      // 등록된 증적을 목록에 반영한다.
      await fetchRequirements();

      // 부모 컴포넌트에서 상세 화면을 열 수 있도록 전달한다.
      onSelectEvidence?.(createdEvidence);

      closeEvidenceForm();
    } catch (submitError) {
      setEvidenceFormError(getErrorMessage(submitError));
    } finally {
      setSavingEvidence(false);
    }
  };

  const getRequirementStatus = (
    requirement: EvidenceRequirement,
  ) => {
    return REQUIREMENT_STATUS_CONFIG[requirement.status] ?? {
      label: requirement.status,
      color: '#475569',
      background: '#e2e8f0',
    };
  };

  if (!projectId) {
    return (
      <section style={styles.container}>
        <div style={styles.emptyState}>
          프로젝트를 선택하면 증적 요구사항을 확인할 수 있습니다.
        </div>
      </section>
    );
  }

  return (
    <section style={styles.container}>
      <header style={styles.header}>
        <div>
          <h2 style={styles.heading}>증적 체크리스트</h2>
          <p style={styles.subheading}>
            프로젝트 요구사항과 제출 증적을 관리합니다.
          </p>
        </div>

        <button
          type="button"
          style={styles.primaryButton}
          onClick={openCreateRequirementForm}
        >
          + 요구사항 등록
        </button>
      </header>

      {error && (
        <div role="alert" style={styles.errorBanner}>
          {error}
          <button
            type="button"
            style={styles.textButton}
            onClick={() => void fetchRequirements()}
          >
            다시 시도
          </button>
        </div>
      )}

      {showRequirementForm && (
        <form
          onSubmit={handleRequirementSubmit}
          style={styles.formCard}
        >
          <h3 style={styles.formHeading}>
            {editingRequirementId !== null
              ? '요구사항 수정'
              : '요구사항 등록'}
          </h3>

          {requirementFormError && (
            <div role="alert" style={styles.errorMessage}>
              {requirementFormError}
            </div>
          )}

          <div style={styles.formGrid}>
            <label style={styles.field}>
              <span style={styles.label}>요구사항 제목 *</span>
              <input
                name="title"
                value={requirementForm.title}
                onChange={handleRequirementFormChange}
                maxLength={200}
                required
                style={styles.input}
                placeholder="예: 서버 설치 결과 보고서"
              />
            </label>

            <label style={styles.field}>
              <span style={styles.label}>요구사항 유형</span>
              <select
                name="requirementType"
                value={requirementForm.requirementType}
                onChange={handleRequirementFormChange}
                style={styles.input}
              >
                {REQUIREMENT_TYPE_OPTIONS.map((option) => (
                  <option
                    key={option.value}
                    value={option.value}
                  >
                    {option.label}
                  </option>
                ))}
              </select>
            </label>

            <label style={styles.field}>
              <span style={styles.label}>WBS ID</span>
              <input
                name="wbsId"
                type="number"
                min={1}
                value={requirementForm.wbsId ?? ''}
                onChange={handleRequirementFormChange}
                style={styles.input}
                placeholder="선택 입력"
              />
            </label>

            <label style={styles.field}>
              <span style={styles.label}>제출 기한</span>
              <input
                name="dueDate"
                type="date"
                value={requirementForm.dueDate ?? ''}
                onChange={handleRequirementFormChange}
                style={styles.input}
              />
            </label>

            <label style={{ ...styles.field, ...styles.fullWidth }}>
              <span style={styles.label}>설명</span>
              <textarea
                name="description"
                value={requirementForm.description ?? ''}
                onChange={handleRequirementFormChange}
                rows={3}
                style={styles.textarea}
                placeholder="요구사항에 대한 설명"
              />
            </label>

            <label style={styles.checkboxField}>
              <input
                name="required"
                type="checkbox"
                checked={requirementForm.required}
                onChange={handleRequirementFormChange}
              />
              필수 제출 항목
            </label>

            <label style={styles.field}>
              <span style={styles.label}>정렬 순서</span>
              <input
                name="sortOrder"
                type="number"
                min={1}
                value={requirementForm.sortOrder}
                onChange={handleRequirementFormChange}
                style={styles.input}
              />
            </label>
          </div>

          <div style={styles.formActions}>
            <button
              type="button"
              style={styles.secondaryButton}
              onClick={closeRequirementForm}
              disabled={savingRequirement}
            >
              취소
            </button>

            <button
              type="submit"
              style={styles.primaryButton}
              disabled={savingRequirement}
            >
              {savingRequirement
                ? '저장 중...'
                : editingRequirementId !== null
                  ? '수정'
                  : '등록'}
            </button>
          </div>
        </form>
      )}

      {requirementFormSuccess && (
        <div role="status" style={styles.successBanner}>
          {requirementFormSuccess}
        </div>
      )}

      {loading ? (
        <div style={styles.emptyState}>요구사항을 불러오는 중...</div>
      ) : requirements.length === 0 ? (
        <div style={styles.emptyState}>
          등록된 증적 요구사항이 없습니다.
          <div style={styles.emptyStateAction}>
            요구사항을 등록한 뒤 증적을 연결할 수 있습니다.
          </div>
        </div>
      ) : (
        <div style={styles.requirementList}>
          {requirements.map((requirement) => {
            const status = getRequirementStatus(requirement);
            const evidenceList =
              evidenceByRequirement[requirement.id] ?? [];

            const evidenceFormOpen =
              activeEvidenceRequirementId === requirement.id;

            return (
              <article
                key={requirement.id}
                style={styles.requirementCard}
              >
                <div style={styles.requirementHeader}>
                  <div style={styles.requirementTitleArea}>
                    <div style={styles.requirementTitleRow}>
                      <h3 style={styles.requirementTitle}>
                        {requirement.title}
                      </h3>

                      <span
                        style={{
                          ...styles.badge,
                          color: status.color,
                          background: status.background,
                        }}
                      >
                        {status.label}
                      </span>

                      {requirement.required && (
                        <span style={styles.requiredBadge}>
                          필수
                        </span>
                      )}
                    </div>

                    <div style={styles.requirementMeta}>
                      <span>
                        {REQUIREMENT_TYPE_OPTIONS.find(
                          (option) =>
                            option.value === requirement.requirementType,
                        )?.label ?? requirement.requirementType}
                      </span>

                      <span>요구사항 #{requirement.id}</span>

                      {requirement.wbsId != null && (
                        <span>WBS #{requirement.wbsId}</span>
                      )}

                      <span>
                        제출 기한: {formatDate(requirement.dueDate)}
                      </span>
                    </div>

                    {requirement.description && (
                      <p style={styles.description}>
                        {requirement.description}
                      </p>
                    )}
                  </div>

                  <div style={styles.requirementActions}>
                    <button
                      type="button"
                      style={styles.secondaryButton}
                      onClick={() =>
                        openEditRequirementForm(requirement)
                      }
                    >
                      수정
                    </button>

                    <button
                      type="button"
                      style={styles.primaryButton}
                      onClick={() =>
                        evidenceFormOpen
                          ? closeEvidenceForm()
                          : openEvidenceForm(requirement)
                      }
                    >
                      {evidenceFormOpen
                        ? '등록 취소'
                        : '+ 증적 등록'}
                    </button>
                  </div>
                </div>

                {evidenceFormOpen && (
                  <form
                    onSubmit={handleEvidenceSubmit}
                    style={styles.evidenceForm}
                  >
                    <h4 style={styles.formHeading}>증적 등록</h4>

                    <p style={styles.formHelp}>
                      선택한 요구사항에 증적 정보를 등록합니다.
                      파일 업로드와 OCR은 별도 기능입니다.
                    </p>

                    {evidenceFormError && (
                      <div role="alert" style={styles.errorMessage}>
                        {evidenceFormError}
                      </div>
                    )}

                    <div style={styles.formGrid}>
                      <label style={styles.field}>
                        <span style={styles.label}>증적 유형 *</span>
                        <select
                          name="evidenceType"
                          value={evidenceForm.evidenceType}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                          required
                        >
                          {EVIDENCE_TYPE_OPTIONS.map((option) => (
                            <option
                              key={option.value}
                              value={option.value}
                            >
                              {option.label}
                            </option>
                          ))}
                        </select>
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>증적 제목 *</span>
                        <input
                          name="title"
                          value={evidenceForm.title}
                          onChange={handleEvidenceFormChange}
                          maxLength={200}
                          required
                          style={styles.input}
                          placeholder="예: 서버 설치 결과"
                        />
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>WBS ID</span>
                        <input
                          name="wbsId"
                          type="number"
                          min={1}
                          value={evidenceForm.wbsId}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                          placeholder="선택 입력"
                        />
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>제출 일시</span>
                        <input
                          name="submittedAt"
                          type="datetime-local"
                          value={evidenceForm.submittedAt}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                        />
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>파일명</span>
                        <input
                          name="fileName"
                          value={evidenceForm.fileName}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                          placeholder="예: installation-result.pdf"
                        />
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>파일 경로</span>
                        <input
                          name="filePath"
                          value={evidenceForm.filePath}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                          placeholder="기존 파일 경로 또는 저장 위치"
                        />
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>Runtime File ID</span>
                        <input
                          name="sourceFileId"
                          value={evidenceForm.sourceFileId}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                          placeholder="Runtime 업로드 파일 ID"
                        />
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>Execution UUID</span>
                        <input
                          name="executionUuid"
                          value={evidenceForm.executionUuid}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                          placeholder="Runtime 실행 UUID"
                        />
                      </label>

                      <label style={styles.field}>
                        <span style={styles.label}>Source Job ID</span>
                        <input
                          name="sourceJobId"
                          value={evidenceForm.sourceJobId}
                          onChange={handleEvidenceFormChange}
                          style={styles.input}
                          placeholder="Runtime 작업 ID"
                        />
                      </label>

                      <label style={{ ...styles.field, ...styles.fullWidth }}>
                        <span style={styles.label}>설명</span>
                        <textarea
                          name="description"
                          value={evidenceForm.description}
                          onChange={handleEvidenceFormChange}
                          rows={3}
                          style={styles.textarea}
                          placeholder="증적 설명"
                        />
                      </label>
                    </div>

                    <div style={styles.formActions}>
                      <button
                        type="button"
                        style={styles.secondaryButton}
                        onClick={closeEvidenceForm}
                        disabled={savingEvidence}
                      >
                        취소
                      </button>

                      <button
                        type="submit"
                        style={styles.primaryButton}
                        disabled={savingEvidence}
                      >
                        {savingEvidence ? '등록 중...' : '증적 등록'}
                      </button>
                    </div>
                  </form>
                )}

                {evidenceFormSuccess && (
                  <div role="status" style={styles.successBanner}>
                    {evidenceFormSuccess}
                  </div>
                )}

                <div style={styles.evidenceSection}>
                  <div style={styles.evidenceSectionHeader}>
                    <h4 style={styles.evidenceHeading}>연결된 증적</h4>

                    <span style={styles.evidenceCount}>
                      {evidenceList.length}건
                    </span>
                  </div>

                  {evidenceLoading ? (
                    <div style={styles.evidenceEmpty}>
                      증적을 불러오는 중...
                    </div>
                  ) : evidenceList.length === 0 ? (
                    <div style={styles.evidenceEmpty}>
                      연결된 증적이 없습니다.
                    </div>
                  ) : (
                    <div style={styles.evidenceList}>
                      {evidenceList.map((evidence) => {
                        const isSelected =
                          evidence.id === selectedEvidenceId;

                        return (
                          <button
                            key={evidence.id}
                            type="button"
                            onClick={() =>
                              onSelectEvidence?.(evidence)
                            }
                            style={{
                              ...styles.evidenceRow,
                              ...(isSelected
                                ? styles.evidenceRowSelected
                                : {}),
                            }}
                          >
                            <span style={styles.evidenceIcon}>
                              {evidence.fileName ? '📎' : '📄'}
                            </span>

                            <span style={styles.evidenceInfo}>
                              <span style={styles.evidenceTitle}>
                                {evidence.title}
                              </span>

                              <span style={styles.evidenceMeta}>
                                {EVIDENCE_TYPE_OPTIONS.find(
                                  (option) =>
                                    option.value === evidence.evidenceType,
                                )?.label ?? evidence.evidenceType}

                                {' · '}

                                {evidence.fileName || '파일 미연결'}

                                {' · '}

                                {formatDate(evidence.submittedAt)}
                              </span>
                            </span>

                            <span style={styles.verificationBadge}>
                              {VERIFICATION_STATUS_LABELS[
                                evidence.verificationStatus
                              ] ?? evidence.verificationStatus}
                            </span>
                          </button>
                        );
                      })}
                    </div>
                  )}
                </div>
              </article>
            );
          })}
        </div>
      )}

      {evidenceError && (
        <div role="alert" style={styles.errorBanner}>
          {evidenceError}
        </div>
      )}
    </section>
  );
}

const styles: Record<string, CSSProperties> = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    gap: 16,
    width: '100%',
  },

  header: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 12,
    flexWrap: 'wrap',
  },

  heading: {
    margin: 0,
    fontSize: 20,
    fontWeight: 700,
    color: '#0f172a',
  },

  subheading: {
    margin: '6px 0 0',
    fontSize: 13,
    color: '#64748b',
  },

  primaryButton: {
    border: '1px solid #2563eb',
    borderRadius: 6,
    padding: '8px 12px',
    background: '#2563eb',
    color: '#fff',
    fontSize: 13,
    fontWeight: 600,
    cursor: 'pointer',
  },

  secondaryButton: {
    border: '1px solid #cbd5e1',
    borderRadius: 6,
    padding: '8px 12px',
    background: '#fff',
    color: '#334155',
    fontSize: 13,
    fontWeight: 500,
    cursor: 'pointer',
  },

  textButton: {
    border: 0,
    background: 'transparent',
    color: '#1d4ed8',
    fontSize: 13,
    fontWeight: 600,
    cursor: 'pointer',
    marginLeft: 8,
  },

  formCard: {
    border: '1px solid #cbd5e1',
    borderRadius: 8,
    padding: 16,
    background: '#f8fafc',
  },

  evidenceForm: {
    border: '1px solid #bfdbfe',
    borderRadius: 8,
    padding: 16,
    marginTop: 16,
    background: '#eff6ff',
  },

  formHeading: {
    margin: '0 0 12px',
    fontSize: 16,
    fontWeight: 700,
    color: '#0f172a',
  },

  formHelp: {
    margin: '0 0 12px',
    fontSize: 12,
    color: '#475569',
  },

  formGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
    gap: 12,
  },

  field: {
    display: 'flex',
    flexDirection: 'column',
    gap: 6,
    minWidth: 0,
  },

  fullWidth: {
    gridColumn: '1 / -1',
  },

  label: {
    fontSize: 12,
    fontWeight: 600,
    color: '#475569',
  },

  input: {
    boxSizing: 'border-box',
    width: '100%',
    minHeight: 36,
    border: '1px solid #cbd5e1',
    borderRadius: 5,
    padding: '7px 9px',
    background: '#fff',
    color: '#0f172a',
    fontSize: 13,
  },

  textarea: {
    boxSizing: 'border-box',
    width: '100%',
    border: '1px solid #cbd5e1',
    borderRadius: 5,
    padding: '8px 9px',
    background: '#fff',
    color: '#0f172a',
    fontSize: 13,
    resize: 'vertical',
  },

  checkboxField: {
    display: 'flex',
    alignItems: 'center',
    gap: 8,
    fontSize: 13,
    color: '#334155',
  },

  formActions: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: 8,
    marginTop: 16,
  },

  errorBanner: {
    border: '1px solid #fecaca',
    borderRadius: 6,
    padding: 12,
    background: '#fef2f2',
    color: '#991b1b',
    fontSize: 13,
  },

  errorMessage: {
    marginBottom: 12,
    color: '#b91c1c',
    fontSize: 13,
  },

  successBanner: {
    border: '1px solid #bbf7d0',
    borderRadius: 6,
    padding: 10,
    background: '#f0fdf4',
    color: '#166534',
    fontSize: 13,
  },

  emptyState: {
    border: '1px dashed #cbd5e1',
    borderRadius: 8,
    padding: 28,
    textAlign: 'center',
    color: '#64748b',
    fontSize: 13,
  },

  emptyStateAction: {
    marginTop: 6,
    fontSize: 12,
  },

  requirementList: {
    display: 'flex',
    flexDirection: 'column',
    gap: 12,
  },

  requirementCard: {
    border: '1px solid #e2e8f0',
    borderRadius: 8,
    padding: 16,
    background: '#fff',
  },

  requirementHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    gap: 12,
    flexWrap: 'wrap',
  },

  requirementTitleArea: {
    display: 'flex',
    flexDirection: 'column',
    gap: 8,
    flex: 1,
    minWidth: 220,
  },

  requirementTitleRow: {
    display: 'flex',
    alignItems: 'center',
    gap: 8,
    flexWrap: 'wrap',
  },

  requirementTitle: {
    margin: 0,
    fontSize: 15,
    fontWeight: 700,
    color: '#0f172a',
  },

  badge: {
    display: 'inline-flex',
    alignItems: 'center',
    borderRadius: 999,
    padding: '3px 8px',
    fontSize: 11,
    fontWeight: 600,
  },

  requiredBadge: {
    borderRadius: 999,
    padding: '3px 8px',
    background: '#fff7ed',
    color: '#9a3412',
    fontSize: 11,
    fontWeight: 600,
  },

  requirementMeta: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: 8,
    color: '#64748b',
    fontSize: 12,
  },

  description: {
    margin: 0,
    color: '#475569',
    fontSize: 13,
    whiteSpace: 'pre-wrap',
  },

  requirementActions: {
    display: 'flex',
    alignItems: 'center',
    gap: 8,
    flexWrap: 'wrap',
  },

  evidenceSection: {
    borderTop: '1px solid #e2e8f0',
    marginTop: 16,
    paddingTop: 12,
  },

  evidenceSectionHeader: {
    display: 'flex',
    alignItems: 'center',
    gap: 8,
    marginBottom: 8,
  },

  evidenceHeading: {
    margin: 0,
    fontSize: 13,
    fontWeight: 700,
    color: '#334155',
  },

  evidenceCount: {
    borderRadius: 999,
    padding: '2px 7px',
    background: '#f1f5f9',
    color: '#475569',
    fontSize: 11,
  },

  evidenceList: {
    display: 'flex',
    flexDirection: 'column',
    gap: 6,
  },

  evidenceRow: {
    display: 'flex',
    alignItems: 'center',
    gap: 10,
    width: '100%',
    border: '1px solid #e2e8f0',
    borderRadius: 6,
    padding: '10px 12px',
    background: '#fff',
    color: '#334155',
    textAlign: 'left',
    cursor: 'pointer',
  },

  evidenceRowSelected: {
    borderColor: '#60a5fa',
    background: '#eff6ff',
  },

  evidenceIcon: {
    flexShrink: 0,
    fontSize: 17,
  },

  evidenceInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: 4,
    flex: 1,
    minWidth: 0,
  },

  evidenceTitle: {
    overflowWrap: 'anywhere',
    fontSize: 13,
    fontWeight: 600,
    color: '#0f172a',
  },

  evidenceMeta: {
    overflowWrap: 'anywhere',
    fontSize: 11,
    color: '#64748b',
  },

  verificationBadge: {
    flexShrink: 0,
    borderRadius: 5,
    padding: '4px 7px',
    background: '#f1f5f9',
    color: '#475569',
    fontSize: 11,
  },

  evidenceEmpty: {
    borderRadius: 6,
    padding: 12,
    background: '#f8fafc',
    color: '#94a3b8',
    fontSize: 12,
  },
};