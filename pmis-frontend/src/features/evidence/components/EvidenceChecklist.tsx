
import { useCallback, useEffect, useMemo, useState } from 'react';
import type { CSSProperties, FormEvent } from 'react';

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

const REQUIREMENT_TYPE_OPTIONS: {
  value: RequirementType;
  label: string;
}[] = [
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

const EVIDENCE_TYPE_OPTIONS: {
  value: EvidenceType;
  label: string;
}[] = [
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
  APPROVED: '검증 완료',
  REJECTED: '반려',
};

function formatDate(value?: string | null): string {
  if (!value) return '-';

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleDateString('ko-KR');
}

function getErrorMessage(error: unknown): string {
  return error instanceof Error
    ? error.message
    : '요청 처리 중 오류가 발생했습니다.';
}

export default function EvidenceChecklist({
  projectId,
  selectedEvidenceId,
  onSelectEvidence,
}: EvidenceChecklistProps) {
  const [requirements, setRequirements] = useState<EvidenceRequirement[]>([]);
  const [evidenceByRequirement, setEvidenceByRequirement] =
    useState<Record<number, Evidence[]>>({});

  const [loading, setLoading] = useState(false);
  const [evidenceLoading, setEvidenceLoading] = useState(false);

  const [error, setError] = useState<string | null>(null);
  const [evidenceError, setEvidenceError] = useState<string | null>(null);

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

  const [activeEvidenceRequirementId, setActiveEvidenceRequirementId] =
    useState<number | null>(null);
  const [evidenceForm, setEvidenceForm] =
    useState<EvidenceForm>(INITIAL_EVIDENCE_FORM);
  const [savingEvidence, setSavingEvidence] = useState(false);
  const [evidenceFormError, setEvidenceFormError] =
    useState<string | null>(null);
  const [evidenceFormSuccess, setEvidenceFormSuccess] =
    useState<string | null>(null);

  const fetchRequirements = useCallback(async () => {
    if (!projectId) {
      setRequirements([]);
      setEvidenceByRequirement({});
      setError(null);
      setEvidenceError(null);
      return;
    }

    setLoading(true);
    setEvidenceLoading(true);
    setError(null);
    setEvidenceError(null);

    try {
      const result = await getProjectEvidenceRequirements(projectId);
      setRequirements(result);

      const entries = await Promise.all(
        result.map(async (requirement) => {
          try {
            const evidence = await getRequirementEvidence(requirement.id);
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

  const summary = useMemo(() => {
    const required = requirements.filter((item) => item.required);

    return {
      total: requirements.length,
      required: required.length,
      submitted: required.filter((item) => item.status === 'PRESENT').length,
      missing: required.filter((item) => item.status === 'MISSING').length,
      evidenceCount: Object.values(evidenceByRequirement).reduce(
        (total, items) => total + items.length,
        0,
      ),
    };
  }, [requirements, evidenceByRequirement]);

  const openCreateRequirementForm = () => {
    setEditingRequirementId(null);
    setRequirementForm(INITIAL_REQUIREMENT_FORM);
    setRequirementFormError(null);
    setRequirementFormSuccess(null);
    setShowRequirementForm(true);
  };

  const openEditRequirementForm = (requirement: EvidenceRequirement) => {
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

        await updateEvidenceRequirement(editingRequirementId, request);
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

  const openEvidenceForm = (requirement: EvidenceRequirement) => {
    setActiveEvidenceRequirementId(requirement.id);
    setEvidenceForm({
      ...INITIAL_EVIDENCE_FORM,
      wbsId: requirement.wbsId ? String(requirement.wbsId) : '',
    });
    setEvidenceFormError(null);
    setEvidenceFormSuccess(null);
  };

  const closeEvidenceForm = () => {
    setActiveEvidenceRequirementId(null);
    setEvidenceForm(INITIAL_EVIDENCE_FORM);
    setEvidenceFormError(null);
  };

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
      await fetchRequirements();
      onSelectEvidence?.(createdEvidence);
      closeEvidenceForm();
    } catch (submitError) {
      setEvidenceFormError(getErrorMessage(submitError));
    } finally {
      setSavingEvidence(false);
    }
  };

  const getRequirementStatus = (requirement: EvidenceRequirement) =>
    REQUIREMENT_STATUS_CONFIG[requirement.status] ?? {
      label: requirement.status,
      color: '#475569',
      background: '#e2e8f0',
    };

  if (!projectId) {
    return (
      <section style={styles.container}>
        <div style={styles.emptyState}>
          <span style={styles.emptyIcon}>◫</span>
          <h3 style={styles.emptyTitle}>프로젝트를 선택해 주세요</h3>
          <p style={styles.emptyDescription}>
            프로젝트를 선택하면 증적 요구사항과 제출 현황을 확인할 수 있습니다.
          </p>
        </div>
      </section>
    );
  }

  return (
    <section style={styles.container}>
      <header style={styles.header}>
        <div>
          <div style={styles.eyebrow}>PROJECT EVIDENCE</div>
          <h2 style={styles.heading}>증적 관리</h2>
          <p style={styles.subheading}>
            프로젝트 요구사항과 연결된 증적의 제출 현황을 관리합니다.
          </p>
        </div>

        <button
          type="button"
          style={styles.primaryButton}
          onClick={openCreateRequirementForm}
        >
          <span aria-hidden="true">＋</span> 요구사항 등록
        </button>
      </header>

      <div style={styles.summaryGrid}>
        <SummaryCard
          label="전체 요구사항"
          value={summary.total}
          description="등록된 요구사항"
          icon="▤"
          accent="#2563eb"
        />
        <SummaryCard
          label="필수 요구사항"
          value={summary.required}
          description="제출 대상"
          icon="◈"
          accent="#7c3aed"
        />
        <SummaryCard
          label="제출 완료"
          value={summary.submitted}
          description="필수 항목 기준"
          icon="✓"
          accent="#059669"
        />
        <SummaryCard
          label="연결된 증적"
          value={summary.evidenceCount}
          description={`미제출 ${summary.missing}개 항목`}
          icon="▧"
          accent="#d97706"
        />
      </div>

      {error && (
        <div role="alert" style={styles.errorBanner}>
          <span>{error}</span>
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
        <form onSubmit={handleRequirementSubmit} style={styles.formCard}>
          <div style={styles.formHeader}>
            <div>
              <span style={styles.sectionEyebrow}>REQUIREMENT</span>
              <h3 style={styles.formHeading}>
                {editingRequirementId !== null
                  ? '요구사항 수정'
                  : '새 요구사항 등록'}
              </h3>
            </div>
            <button
              type="button"
              style={styles.iconButton}
              onClick={closeRequirementForm}
              disabled={savingRequirement}
              aria-label="요구사항 폼 닫기"
            >
              ×
            </button>
          </div>

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
                  <option key={option.value} value={option.value}>
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
                  ? '수정 내용 저장'
                  : '요구사항 등록'}
            </button>
          </div>
        </form>
      )}

      {requirementFormSuccess && (
        <div role="status" style={styles.successBanner}>
          {requirementFormSuccess}
        </div>
      )}

      <div style={styles.listHeader}>
        <div>
          <h3 style={styles.listHeading}>요구사항별 증적</h3>
          <p style={styles.listDescription}>
            각 요구사항의 상태와 연결된 증적을 확인할 수 있습니다.
          </p>
        </div>
        <span style={styles.countBadge}>{requirements.length}개 항목</span>
      </div>

      {loading ? (
        <div style={styles.emptyState}>
          <span style={styles.loadingDot} />
          요구사항을 불러오는 중입니다.
        </div>
      ) : requirements.length === 0 ? (
        <div style={styles.emptyState}>
          <span style={styles.emptyIcon}>▤</span>
          <h3 style={styles.emptyTitle}>등록된 요구사항이 없습니다.</h3>
          <p style={styles.emptyDescription}>
            요구사항을 등록한 뒤 해당 항목에 증적을 연결할 수 있습니다.
          </p>
          <button
            type="button"
            style={styles.primaryButton}
            onClick={openCreateRequirementForm}
          >
            첫 요구사항 등록
          </button>
        </div>
      ) : (
        <div style={styles.requirementList}>
          {requirements.map((requirement) => {
            const status = getRequirementStatus(requirement);
            const evidenceList =
              evidenceByRequirement[requirement.id] ?? [];
            const evidenceFormOpen =
              activeEvidenceRequirementId === requirement.id;

            const requirementTypeLabel =
              REQUIREMENT_TYPE_OPTIONS.find(
                (option) => option.value === requirement.requirementType,
              )?.label ?? requirement.requirementType;

            return (
              <article
                key={requirement.id}
                style={styles.requirementCard}
              >
                <div style={styles.requirementHeader}>
                  <div style={styles.requirementTitleArea}>
                    <div style={styles.requirementTitleRow}>
                      <h4 style={styles.requirementTitle}>
                        {requirement.title}
                      </h4>
                      <StatusBadge
                        label={status.label}
                        color={status.color}
                        background={status.background}
                      />
                      {requirement.required && (
                        <span style={styles.requiredBadge}>필수</span>
                      )}
                    </div>

                    <div style={styles.requirementMeta}>
                      <span style={styles.metaChip}>
                        {requirementTypeLabel}
                      </span>
                      <span>요구사항 #{requirement.id}</span>
                      {requirement.wbsId != null && (
                        <span>WBS #{requirement.wbsId}</span>
                      )}
                      <span>
                        제출 기한 {formatDate(requirement.dueDate)}
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
                      onClick={() => openEditRequirementForm(requirement)}
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
                      {evidenceFormOpen ? '등록 취소' : '＋ 증적 등록'}
                    </button>
                  </div>
                </div>

                <div style={styles.progressArea}>
                  <div style={styles.progressHeader}>
                    <span>연결된 증적</span>
                    <strong>{evidenceList.length}건</strong>
                  </div>
                  <div style={styles.progressTrack}>
                    <div
                      style={{
                        ...styles.progressFill,
                        width: evidenceList.length > 0 ? '100%' : '0%',
                      }}
                    />
                  </div>
                </div>

                {evidenceFormOpen && (
                  <form
                    onSubmit={handleEvidenceSubmit}
                    style={styles.evidenceForm}
                  >
                    <div style={styles.formHeader}>
                      <div>
                        <span style={styles.sectionEyebrow}>NEW EVIDENCE</span>
                        <h4 style={styles.formHeading}>증적 등록</h4>
                        <p style={styles.formHelp}>
                          파일 메타데이터와 Runtime 실행 정보를 등록합니다.
                          실제 파일 업로드 및 OCR은 상세 화면에서 실행합니다.
                        </p>
                      </div>
                      <button
                        type="button"
                        style={styles.iconButton}
                        onClick={closeEvidenceForm}
                        disabled={savingEvidence}
                        aria-label="증적 등록 폼 닫기"
                      >
                        ×
                      </button>
                    </div>

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
                            <option key={option.value} value={option.value}>
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
                    <h5 style={styles.evidenceHeading}>연결된 증적 목록</h5>
                    <span style={styles.countBadge}>
                      {evidenceList.length}건
                    </span>
                  </div>

                  {evidenceLoading ? (
                    <div style={styles.evidenceEmpty}>
                      증적을 불러오는 중입니다.
                    </div>
                  ) : evidenceList.length === 0 ? (
                    <div style={styles.evidenceEmpty}>
                      <span style={styles.smallEmptyIcon}>▧</span>
                      <span>아직 연결된 증적이 없습니다.</span>
                    </div>
                  ) : (
                    <div style={styles.evidenceList}>
                      {evidenceList.map((evidence) => {
                        const isSelected =
                          evidence.id === selectedEvidenceId;

                        const verificationLabel =
                          VERIFICATION_STATUS_LABELS[
                            evidence.verificationStatus
                          ] ?? evidence.verificationStatus;

                        const verificationStyle =
                          evidence.verificationStatus === 'APPROVED'
                          ? styles.verifiedBadge
                          : evidence.verificationStatus === 'REJECTED'
                            ? styles.rejectedBadge
                            : styles.pendingBadge;

                        return (
                          <button
                            key={evidence.id}
                            type="button"
                            onClick={() => onSelectEvidence?.(evidence)}
                            aria-pressed={isSelected}
                            style={{
                              ...styles.evidenceRow,
                              ...(isSelected
                                ? styles.evidenceRowSelected
                                : {}),
                            }}
                          >
                            <span style={styles.evidenceIcon}>
                              {evidence.fileName ? '📎' : '▤'}
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

                            <span
                              style={{
                                ...styles.badge,
                                ...verificationStyle,
                              }}
                            >
                              {verificationLabel}
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

function SummaryCard({
  label,
  value,
  description,
  icon,
  accent,
}: {
  label: string;
  value: number;
  description: string;
  icon: string;
  accent: string;
}) {
  return (
    <article
      style={{
        ...styles.summaryCard,
        borderTop: `3px solid ${accent}`,
      }}
    >
      <div style={styles.summaryCardTop}>
        <span style={styles.summaryLabel}>{label}</span>
        <span
          style={{
            ...styles.summaryIcon,
            color: accent,
            background: `${accent}12`,
          }}
        >
          {icon}
        </span>
      </div>
      <strong style={styles.summaryValue}>{value}</strong>
      <span style={styles.summaryDescription}>{description}</span>
    </article>
  );
}

function StatusBadge({
  label,
  color,
  background,
}: {
  label: string;
  color: string;
  background: string;
}) {
  return (
    <span
      style={{
        ...styles.badge,
        color,
        background,
      }}
    >
      {label}
    </span>
  );
}

const styles: Record<string, CSSProperties> = {
  container: {
    display: 'flex',
    flexDirection: 'column',
    gap: 20,
    width: '100%',
    minWidth: 0,
    boxSizing: 'border-box',
    color: '#172033',
  },

  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    gap: 16,
    flexWrap: 'wrap',
    padding: '4px 0',
  },

  eyebrow: {
    marginBottom: 6,
    color: '#64748b',
    fontSize: 10,
    fontWeight: 800,
    letterSpacing: '0.14em',
  },

  heading: {
    margin: 0,
    color: '#172033',
    fontSize: 24,
    fontWeight: 800,
    letterSpacing: '-0.04em',
  },

  subheading: {
    margin: '7px 0 0',
    color: '#64748b',
    fontSize: 13,
    lineHeight: 1.6,
  },

  summaryGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(min(100%, 165px), 1fr))',
    gap: 12,
  },

  summaryCard: {
    display: 'flex',
    flexDirection: 'column',
    gap: 9,
    minWidth: 0,
    padding: 16,
    border: '1px solid #e2e8f0',
    borderRadius: 12,
    background: '#fff',
    boxShadow: '0 2px 8px rgba(15, 23, 42, 0.025)',
  },

  summaryCardTop: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 8,
  },

  summaryLabel: {
    color: '#64748b',
    fontSize: 12,
    fontWeight: 650,
  },

  summaryIcon: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: 32,
    height: 32,
    borderRadius: 9,
    fontSize: 17,
    fontWeight: 800,
  },

  summaryValue: {
    color: '#0f172a',
    fontSize: 29,
    lineHeight: 1.15,
    fontWeight: 800,
    letterSpacing: '-0.04em',
  },

  summaryDescription: {
    color: '#94a3b8',
    fontSize: 11,
  },

  listHeader: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    gap: 10,
    marginTop: 2,
  },

  listHeading: {
    margin: 0,
    color: '#172033',
    fontSize: 17,
    fontWeight: 800,
  },

  listDescription: {
    margin: '5px 0 0',
    color: '#64748b',
    fontSize: 12,
  },

  countBadge: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '5px 9px',
    borderRadius: 999,
    background: '#f1f5f9',
    color: '#475569',
    fontSize: 11,
    fontWeight: 700,
    whiteSpace: 'nowrap',
  },

  primaryButton: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 6,
    minHeight: 36,
    padding: '8px 13px',
    border: '1px solid #2563eb',
    borderRadius: 8,
    background: '#2563eb',
    color: '#fff',
    fontSize: 12,
    fontWeight: 700,
    cursor: 'pointer',
    boxShadow: '0 2px 4px rgba(37, 99, 235, 0.12)',
  },

  secondaryButton: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    minHeight: 34,
    padding: '7px 11px',
    border: '1px solid #dbe3ee',
    borderRadius: 8,
    background: '#fff',
    color: '#334155',
    fontSize: 12,
    fontWeight: 650,
    cursor: 'pointer',
  },

  textButton: {
    border: 0,
    background: 'transparent',
    color: '#1d4ed8',
    fontSize: 12,
    fontWeight: 700,
    cursor: 'pointer',
  },

  iconButton: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: 32,
    height: 32,
    border: '1px solid #e2e8f0',
    borderRadius: 8,
    background: '#fff',
    color: '#64748b',
    fontSize: 20,
    cursor: 'pointer',
  },

  formCard: {
    display: 'flex',
    flexDirection: 'column',
    gap: 16,
    padding: 20,
    border: '1px solid #dbeafe',
    borderRadius: 13,
    background: '#fff',
    boxShadow: '0 4px 16px rgba(37, 99, 235, 0.05)',
  },

  formHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    gap: 12,
  },

  sectionEyebrow: {
    display: 'block',
    marginBottom: 5,
    color: '#64748b',
    fontSize: 10,
    fontWeight: 800,
    letterSpacing: '0.12em',
  },

  formHeading: {
    margin: 0,
    color: '#172033',
    fontSize: 17,
    fontWeight: 800,
  },

  formHelp: {
    margin: '7px 0 0',
    color: '#64748b',
    fontSize: 12,
    lineHeight: 1.6,
  },

  formGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(min(100%, 220px), 1fr))',
    gap: 14,
  },

  field: {
    display: 'flex',
    flexDirection: 'column',
    gap: 7,
    minWidth: 0,
  },

  fullWidth: {
    gridColumn: '1 / -1',
  },

  label: {
    color: '#475569',
    fontSize: 12,
    fontWeight: 700,
  },

  input: {
    boxSizing: 'border-box',
    width: '100%',
    minHeight: 39,
    padding: '9px 11px',
    border: '1px solid #dbe3ee',
    borderRadius: 8,
    outlineColor: '#93c5fd',
    background: '#fff',
    color: '#172033',
    fontSize: 13,
  },

  textarea: {
    boxSizing: 'border-box',
    width: '100%',
    padding: '10px 11px',
    border: '1px solid #dbe3ee',
    borderRadius: 8,
    outlineColor: '#93c5fd',
    background: '#fff',
    color: '#172033',
    fontSize: 13,
    resize: 'vertical',
    lineHeight: 1.6,
  },

  checkboxField: {
    display: 'flex',
    alignItems: 'center',
    gap: 9,
    color: '#334155',
    fontSize: 12,
    fontWeight: 600,
  },

  formActions: {
    display: 'flex',
    justifyContent: 'flex-end',
    flexWrap: 'wrap',
    gap: 8,
    paddingTop: 14,
    borderTop: '1px solid #eef2f7',
  },

  errorBanner: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    gap: 10,
    padding: 13,
    border: '1px solid #fecaca',
    borderRadius: 9,
    background: '#fff7f7',
    color: '#991b1b',
    fontSize: 12,
  },

  errorMessage: {
    padding: 11,
    border: '1px solid #fecaca',
    borderRadius: 8,
    background: '#fff7f7',
    color: '#b91c1c',
    fontSize: 12,
    lineHeight: 1.6,
  },

  successBanner: {
    padding: 12,
    border: '1px solid #bbf7d0',
    borderRadius: 9,
    background: '#f0fdf4',
    color: '#166534',
    fontSize: 12,
  },

  emptyState: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 10,
    padding: '40px 20px',
    border: '1px dashed #cbd5e1',
    borderRadius: 12,
    background: '#fff',
    textAlign: 'center',
    color: '#64748b',
    fontSize: 13,
  },

  emptyIcon: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: 44,
    height: 44,
    borderRadius: 12,
    background: '#eff6ff',
    color: '#2563eb',
    fontSize: 22,
  },

  emptyTitle: {
    margin: 0,
    color: '#172033',
    fontSize: 15,
    fontWeight: 800,
  },

  emptyDescription: {
    maxWidth: 400,
    margin: 0,
    color: '#64748b',
    fontSize: 12,
    lineHeight: 1.7,
  },

  loadingDot: {
    width: 9,
    height: 9,
    borderRadius: '50%',
    background: '#3b82f6',
  },

  requirementList: {
    display: 'flex',
    flexDirection: 'column',
    gap: 14,
  },

  requirementCard: {
    minWidth: 0,
    padding: 18,
    border: '1px solid #e2e8f0',
    borderRadius: 12,
    background: '#fff',
    boxShadow: '0 2px 8px rgba(15, 23, 42, 0.025)',
  },

  requirementHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
    gap: 16,
  },

  requirementTitleArea: {
    display: 'flex',
    flexDirection: 'column',
    gap: 10,
    flex: 1,
    minWidth: 0,
  },

  requirementTitleRow: {
    display: 'flex',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: 8,
  },

  requirementTitle: {
    margin: 0,
    color: '#172033',
    fontSize: 15,
    fontWeight: 800,
    lineHeight: 1.5,
  },

  badge: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: 'fit-content',
    padding: '4px 8px',
    borderRadius: 999,
    fontSize: 10,
    fontWeight: 750,
    whiteSpace: 'nowrap',
  },

  requiredBadge: {
    display: 'inline-flex',
    padding: '4px 8px',
    border: '1px solid #fed7aa',
    borderRadius: 999,
    background: '#fff7ed',
    color: '#c2410c',
    fontSize: 10,
    fontWeight: 750,
  },

  metaChip: {
    padding: '3px 7px',
    borderRadius: 5,
    background: '#f1f5f9',
    color: '#475569',
    fontWeight: 650,
  },

  requirementMeta: {
    display: 'flex',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: 8,
    color: '#64748b',
    fontSize: 11,
  },

  description: {
    margin: 0,
    color: '#64748b',
    fontSize: 12,
    lineHeight: 1.7,
    whiteSpace: 'pre-wrap',
    overflowWrap: 'anywhere',
  },

  requirementActions: {
    display: 'flex',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: 8,
  },

  progressArea: {
    display: 'flex',
    flexDirection: 'column',
    gap: 8,
    marginTop: 18,
    paddingTop: 14,
    borderTop: '1px solid #f1f5f9',
  },

  progressHeader: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
    color: '#64748b',
    fontSize: 11,
  },

  progressTrack: {
    height: 5,
    overflow: 'hidden',
    borderRadius: 999,
    background: '#e2e8f0',
  },

  progressFill: {
    height: '100%',
    borderRadius: 999,
    background: '#10b981',
    transition: 'width 180ms ease',
  },

  evidenceForm: {
    display: 'flex',
    flexDirection: 'column',
    gap: 16,
    marginTop: 18,
    padding: 16,
    border: '1px solid #dbeafe',
    borderRadius: 10,
    background: '#f8fbff',
  },

  evidenceSection: {
    marginTop: 18,
    paddingTop: 16,
    borderTop: '1px solid #eef2f7',
  },

  evidenceSectionHeader: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
    marginBottom: 12,
  },

  evidenceHeading: {
    margin: 0,
    color: '#334155',
    fontSize: 12,
    fontWeight: 800,
  },

  evidenceEmpty: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    padding: 18,
    border: '1px dashed #dbe3ee',
    borderRadius: 9,
    color: '#94a3b8',
    fontSize: 12,
  },

  smallEmptyIcon: {
    color: '#94a3b8',
    fontSize: 16,
  },

  evidenceList: {
    display: 'flex',
    flexDirection: 'column',
    gap: 8,
  },

  evidenceRow: {
    display: 'flex',
    alignItems: 'center',
    gap: 12,
    width: '100%',
    minWidth: 0,
    padding: 12,
    border: '1px solid #e2e8f0',
    borderRadius: 9,
    background: '#fff',
    color: '#334155',
    textAlign: 'left',
    cursor: 'pointer',
  },

  evidenceRowSelected: {
    border: '1px solid #93c5fd',
    background: '#eff6ff',
    boxShadow: '0 0 0 2px rgba(59, 130, 246, 0.06)',
  },

  evidenceIcon: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0,
    width: 34,
    height: 34,
    borderRadius: 8,
    background: '#f1f5f9',
    fontSize: 15,
  },

  evidenceInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: 5,
    flex: 1,
    minWidth: 0,
  },

  evidenceTitle: {
    color: '#172033',
    fontSize: 12,
    fontWeight: 750,
    overflowWrap: 'anywhere',
  },

  evidenceMeta: {
    color: '#64748b',
    fontSize: 10,
    lineHeight: 1.5,
    overflowWrap: 'anywhere',
  },

  verifiedBadge: {
    color: '#166534',
    background: '#dcfce7',
  },

  pendingBadge: {
    color: '#92400e',
    background: '#fef3c7',
  },

  rejectedBadge: {
    color: '#991b1b',
    background: '#fee2e2',
  },
};
