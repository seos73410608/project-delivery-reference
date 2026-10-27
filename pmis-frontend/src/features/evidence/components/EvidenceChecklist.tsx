import {
  useCallback,
  useEffect,
  useState,
} from "react";

import type {
  CSSProperties,
  FormEvent,
} from "react";

import {
  createEvidenceRequirement,
  getProjectEvidenceRequirements,
  updateEvidenceRequirement,
} from "@/features/evidence/api/evidenceRequirementApi";

import {
  getRequirementEvidence,
} from "@/features/evidence/api/evidenceApi";

import type {
  EvidenceRequirement,
  EvidenceRequirementCreateRequest,
  EvidenceRequirementUpdateRequest,
  RequirementStatus,
  RequirementType,
} from "@/features/evidence/types/evidenceRequirement";

import type {
  Evidence,
  VerificationStatus,
} from "@/features/evidence/types/evidence";

interface EvidenceChecklistProps {
  projectId?: number;
  selectedEvidenceId?: number | null;
  onSelectEvidence?: (evidence: Evidence) => void;
}

const REQUIREMENT_STATUS_CONFIG: Record<
  RequirementStatus,
  {
    label: string;
    icon: string;
    color: string;
  }
> = {
  PENDING: {
    label: "대기 중",
    icon: "○",
    color: "#f57c00",
  },
  PRESENT: {
    label: "증적 등록",
    icon: "☑",
    color: "#2e7d32",
  },
  MISSING: {
    label: "누락",
    icon: "✕",
    color: "#d32f2f",
  },
  NOT_REQUIRED: {
    label: "불필요",
    icon: "—",
    color: "#757575",
  },
};

const VERIFICATION_STATUS_CONFIG: Record<
  VerificationStatus,
  {
    label: string;
    color: string;
  }
> = {
  PENDING: {
    label: "검증 대기",
    color: "#f57c00",
  },
  APPROVED: {
    label: "승인",
    color: "#2e7d32",
  },
  REJECTED: {
    label: "반려",
    color: "#d32f2f",
  },
};

const REQUIREMENT_TYPE_OPTIONS: {
  value: RequirementType;
  label: string;
}[] = [
  { value: "INSTALLATION", label: "설치" },
  { value: "CONFIGURATION", label: "환경 설정" },
  { value: "ACCOUNT", label: "계정" },
  { value: "TEST", label: "테스트" },
  { value: "MIGRATION", label: "이관" },
  { value: "INSPECTION", label: "점검" },
  { value: "REPORT", label: "보고서" },
  { value: "OPERATION", label: "운영" },
  { value: "SECURITY", label: "보안" },
  { value: "OTHER", label: "기타" },
];

const EVIDENCE_TYPE_LABELS: Record<string, string> = {
  OTHER: "기타",
  LOG: "로그",
  SCREENSHOT: "스크린샷",
  TEST_RESULT: "테스트 결과",
  IMAGE: "이미지",
  REPORT: "보고서",
  CONFIGURATION: "환경 설정",
  DOCUMENT: "문서",
};

const INITIAL_FORM: EvidenceRequirementCreateRequest = {
  wbsId: null,
  title: "",
  description: "",
  requirementType: "INSTALLATION",
  required: true,
  dueDate: "",
  sortOrder: 1,
};

function EvidenceChecklist({
  projectId,
  selectedEvidenceId = null,
  onSelectEvidence,
}: EvidenceChecklistProps) {
  const [requirements, setRequirements] = useState<
    EvidenceRequirement[]
  >([]);

  const [evidenceByRequirement, setEvidenceByRequirement] =
    useState<Record<number, Evidence[]>>({});

  const [loading, setLoading] = useState(true);
  const [evidenceLoading, setEvidenceLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [evidenceError, setEvidenceError] = useState<string | null>(null);

  const [showForm, setShowForm] = useState(false);
  const [editingRequirementId, setEditingRequirementId] =
    useState<number | null>(null);

  const [form, setForm] =
    useState<EvidenceRequirementCreateRequest>({
      ...INITIAL_FORM,
    });

  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [formSuccess, setFormSuccess] = useState<string | null>(null);

  const isValidProjectId =
    projectId !== undefined &&
    Number.isInteger(projectId) &&
    projectId > 0;

  /**
   * 프로젝트 요구사항 및 각 요구사항에 연결된 증적 조회
   */
  const fetchRequirements = useCallback(async () => {
    if (!isValidProjectId || projectId === undefined) {
      setRequirements([]);
      setEvidenceByRequirement({});
      setError(
        "프로젝트를 선택하면 증적 요구사항을 확인할 수 있습니다.",
      );
      setEvidenceError(null);
      setLoading(false);
      setEvidenceLoading(false);
      return;
    }

    setLoading(true);
    setError(null);
    setEvidenceError(null);

    try {
      const response =
        await getProjectEvidenceRequirements(projectId);

      setRequirements(response);

      // 요구사항별 증적 목록을 초기화한다.
      setEvidenceByRequirement({});

      if (response.length === 0) {
        return;
      }

      setEvidenceLoading(true);

      const results = await Promise.all(
        response.map(async (requirement) => {
          const evidence = await getRequirementEvidence(
            requirement.id,
          );

          return [requirement.id, evidence] as const;
        }),
      );

      setEvidenceByRequirement(
        Object.fromEntries(results),
      );
    } catch (err) {
      console.error("증적 요구사항 또는 증적 조회 실패:", err);

      setEvidenceError(
        "증적 목록을 불러오지 못했습니다. API 응답과 서버 로그를 확인해 주세요.",
      );
    } finally {
      setLoading(false);
      setEvidenceLoading(false);
    }
  }, [isValidProjectId, projectId]);

  useEffect(() => {
    void fetchRequirements();
  }, [fetchRequirements]);

  /**
   * 등록 및 수정 폼의 입력값 변경
   */
  const handleFormChange = <
    K extends keyof EvidenceRequirementCreateRequest,
  >(
    field: K,
    value: EvidenceRequirementCreateRequest[K],
  ) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  /**
   * 등록 폼 열기
   */
  const handleOpenCreateForm = () => {
    setEditingRequirementId(null);
    setForm({
      ...INITIAL_FORM,
      sortOrder: requirements.length + 1,
    });
    setFormError(null);
    setFormSuccess(null);
    setShowForm(true);
  };

  /**
   * 기존 요구사항을 수정 폼에 불러오기
   */
  const handleOpenEditForm = (
    requirement: EvidenceRequirement,
  ) => {
    setEditingRequirementId(requirement.id);

    setForm({
      wbsId: requirement.wbsId,
      title: requirement.title,
      description: requirement.description ?? "",
      requirementType: requirement.requirementType,
      required: requirement.required,
      dueDate: requirement.dueDate ?? "",
      sortOrder: requirement.sortOrder,
    });

    setFormError(null);
    setFormSuccess(null);
    setShowForm(true);
  };

  /**
   * 폼 닫기
   */
  const handleCloseForm = () => {
    if (saving) return;

    setShowForm(false);
    setEditingRequirementId(null);
    setFormError(null);
  };

  /**
   * 요구사항 등록 또는 수정
   *
   * 신규: POST /api/projects/{projectId}/evidence-requirements
   * 수정: PUT /api/evidence-requirements/{id}
   *
   * requirementKey와 status는 Backend에서 관리한다.
   */
  const handleSubmit = async (
    event: FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();

    if (!isValidProjectId || projectId === undefined) {
      setFormError("유효한 프로젝트를 선택해 주세요.");
      return;
    }

    const title = form.title.trim();

    if (!title) {
      setFormError("요구사항 제목을 입력해 주세요.");
      return;
    }

    setSaving(true);
    setFormError(null);
    setFormSuccess(null);

    try {
      if (editingRequirementId !== null) {
        const request: EvidenceRequirementUpdateRequest = {
          wbsId: form.wbsId,
          title,
          description: form.description?.trim() || null,
          requirementType: form.requirementType,
          required: form.required,
          dueDate: form.dueDate || null,
          sortOrder: form.sortOrder,
        };

        await updateEvidenceRequirement(
          editingRequirementId,
          request,
        );

        setFormSuccess("증적 요구사항이 수정되었습니다.");
      } else {
        const request: EvidenceRequirementCreateRequest = {
          ...form,
          title,
          description: form.description?.trim() || null,
          dueDate: form.dueDate || null,
        };

        await createEvidenceRequirement(projectId, request);

        setFormSuccess("증적 요구사항이 등록되었습니다.");
      }

      setShowForm(false);
      setEditingRequirementId(null);
      setForm({ ...INITIAL_FORM });

      await fetchRequirements();
    } catch (err) {
      console.error(
        editingRequirementId !== null
          ? "증적 요구사항 수정 실패:"
          : "증적 요구사항 등록 실패:",
        err,
      );

      setFormError(
        editingRequirementId !== null
          ? "증적 요구사항 수정에 실패했습니다. 입력값과 서버 응답을 확인해 주세요."
          : "증적 요구사항 등록에 실패했습니다. 입력값과 서버 응답을 확인해 주세요.",
      );
    } finally {
      setSaving(false);
    }
  };

  /**
   * PRESENT는 증적이 존재한다는 의미다.
   * 검증 승인 여부와는 별개다.
   */
  const presentCount = requirements.filter(
    (requirement) => requirement.status === "PRESENT",
  ).length;

  const totalEvidenceCount = Object.values(
    evidenceByRequirement,
  ).reduce(
    (total, evidenceList) => total + evidenceList.length,
    0,
  );

  return (
    <div style={cardStyle}>
      {/* Header */}
      <div style={headerStyle}>
        <div>
          <h3 style={{ margin: 0, fontSize: "18px" }}>
            Evidence Checklist
          </h3>

          <p style={subtitleStyle}>
            {isValidProjectId
              ? `프로젝트 ID: ${projectId}`
              : "프로젝트 미선택"}
          </p>
        </div>

        <button
          type="button"
          onClick={handleOpenCreateForm}
          disabled={loading || !isValidProjectId || saving}
          style={{
            ...primaryButtonStyle,
            opacity: loading || !isValidProjectId ? 0.5 : 1,
          }}
        >
          + 요구사항 등록
        </button>
      </div>

      {/* Registration / Edit Form */}
      {showForm && (
        <form onSubmit={handleSubmit} style={formStyle}>
          <h4 style={{ margin: 0 }}>
            {editingRequirementId !== null
              ? "증적 요구사항 수정"
              : "증적 요구사항 등록"}
          </h4>

          <div style={fieldStyle}>
            <label htmlFor="requirement-title">
              제목 <span style={{ color: "#d32f2f" }}>*</span>
            </label>

            <input
              id="requirement-title"
              type="text"
              value={form.title}
              onChange={(event) =>
                handleFormChange("title", event.target.value)
              }
              maxLength={200}
              required
              disabled={saving}
              placeholder="예: 서버 설치 결과 증적"
              style={inputStyle}
            />
          </div>

          <div style={fieldStyle}>
            <label htmlFor="requirement-description">
              설명
            </label>

            <textarea
              id="requirement-description"
              value={form.description ?? ""}
              onChange={(event) =>
                handleFormChange(
                  "description",
                  event.target.value,
                )
              }
              rows={3}
              disabled={saving}
              placeholder="요구사항 설명"
              style={{
                ...inputStyle,
                resize: "vertical",
              }}
            />
          </div>

          <div style={formGridStyle}>
            <div style={fieldStyle}>
              <label htmlFor="requirement-type">
                요구사항 유형
              </label>

              <select
                id="requirement-type"
                value={form.requirementType}
                onChange={(event) =>
                  handleFormChange(
                    "requirementType",
                    event.target.value as RequirementType,
                  )
                }
                disabled={saving}
                style={inputStyle}
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
            </div>

            <div style={fieldStyle}>
              <label htmlFor="requirement-due-date">
                제출 기한
              </label>

              <input
                id="requirement-due-date"
                type="date"
                value={form.dueDate ?? ""}
                onChange={(event) =>
                  handleFormChange(
                    "dueDate",
                    event.target.value || null,
                  )
                }
                disabled={saving}
                style={inputStyle}
              />
            </div>

            <div style={fieldStyle}>
              <label htmlFor="requirement-wbs-id">
                WBS ID (선택)
              </label>

              <input
                id="requirement-wbs-id"
                type="number"
                min={1}
                value={form.wbsId ?? ""}
                onChange={(event) => {
                  const value = event.target.value;

                  handleFormChange(
                    "wbsId",
                    value === "" ? null : Number(value),
                  );
                }}
                disabled={saving}
                placeholder="연결할 WBS ID"
                style={inputStyle}
              />
            </div>

            <div style={fieldStyle}>
              <label htmlFor="requirement-sort-order">
                정렬 순서
              </label>

              <input
                id="requirement-sort-order"
                type="number"
                min={0}
                value={form.sortOrder ?? 0}
                onChange={(event) =>
                  handleFormChange(
                    "sortOrder",
                    Number(event.target.value),
                  )
                }
                disabled={saving}
                style={inputStyle}
              />
            </div>
          </div>

          <label style={checkboxLabelStyle}>
            <input
              type="checkbox"
              checked={form.required}
              onChange={(event) =>
                handleFormChange(
                  "required",
                  event.target.checked,
                )
              }
              disabled={saving}
            />
            필수 증적 요구사항
          </label>

          {formError && (
            <div role="alert" style={errorStyle}>
              {formError}
            </div>
          )}

          <div style={formActionsStyle}>
            <button
              type="button"
              onClick={handleCloseForm}
              disabled={saving}
              style={secondaryButtonStyle}
            >
              취소
            </button>

            <button
              type="submit"
              disabled={saving}
              style={primaryButtonStyle}
            >
              {saving
                ? "저장 중..."
                : editingRequirementId !== null
                  ? "수정 저장"
                  : "등록"}
            </button>
          </div>
        </form>
      )}

      {/* Success Message */}
      {formSuccess && (
        <div role="status" style={successStyle}>
          {formSuccess}
        </div>
      )}

      {/* Requirement Loading */}
      {loading && (
        <div role="status" style={messageStyle}>
          증적 요구사항을 불러오는 중입니다...
        </div>
      )}

      {/* Requirement Error */}
      {!loading && error && (
        <div role="alert" style={errorStyle}>
          {error}
        </div>
      )}

      {/* Empty Requirements */}
      {!loading && !error && requirements.length === 0 && (
        <div style={messageStyle}>
          등록된 증적 요구사항이 없습니다.
        </div>
      )}

      {/* Requirement and Evidence List */}
      {!loading && !error && requirements.length > 0 && (
        <div>
          {requirements.map((requirement) => {
            const statusConfig =
              REQUIREMENT_STATUS_CONFIG[requirement.status];

            const evidenceList =
              evidenceByRequirement[requirement.id];

            return (
              <div
                key={requirement.id}
                style={requirementContainerStyle}
              >
                {/* Requirement */}
                <div style={requirementRowStyle}>
                  <div style={requirementInfoStyle}>
                    <span
                      aria-hidden="true"
                      style={{
                        flexShrink: 0,
                        fontSize: "16px",
                        color: statusConfig.color,
                      }}
                    >
                      {statusConfig.icon}
                    </span>

                    <div style={{ minWidth: 0 }}>
                      <div style={{ overflowWrap: "anywhere" }}>
                        {requirement.title}
                      </div>

                      <div style={metadataStyle}>
                        {requirement.requirementKey}
                        {" · "}
                        {requirement.required ? "필수" : "선택"}
                        {" · "}
                        {REQUIREMENT_TYPE_OPTIONS.find(
                          (option) =>
                            option.value === requirement.requirementType,
                        )?.label ?? requirement.requirementType}
                        {requirement.dueDate
                          ? ` · 기한 ${requirement.dueDate}`
                          : ""}
                      </div>
                    </div>
                  </div>

                  <div style={rowActionsStyle}>
                    <span
                      style={{
                        flexShrink: 0,
                        fontSize: "14px",
                        color: statusConfig.color,
                        fontWeight: 500,
                      }}
                    >
                      {statusConfig.label}
                    </span>

                    <button
                      type="button"
                      onClick={() =>
                        handleOpenEditForm(requirement)
                      }
                      disabled={saving}
                      style={smallButtonStyle}
                      aria-label={`${requirement.title} 수정`}
                    >
                      수정
                    </button>
                  </div>
                </div>

                {/* Evidence List for Requirement */}
                <div style={evidenceSectionStyle}>
                  <div style={evidenceSectionHeaderStyle}>
                    <strong style={{ fontSize: "13px" }}>
                      연결된 증적
                    </strong>

                    <span style={metadataStyle}>
                      {evidenceList
                        ? `${evidenceList.length}건`
                        : ""}
                    </span>
                  </div>

                  {evidenceLoading && evidenceList === undefined && (
                    <div style={evidenceMessageStyle}>
                      증적을 불러오는 중입니다...
                    </div>
                  )}

                  {!evidenceLoading &&
                    evidenceList !== undefined &&
                    evidenceList.length === 0 && (
                      <div style={evidenceMessageStyle}>
                        연결된 증적이 없습니다.
                      </div>
                    )}

                  {evidenceList?.map((evidence) => {
                    const verificationConfig =
                      VERIFICATION_STATUS_CONFIG[
                        evidence.verificationStatus
                      ];

                    const isSelected =
                      selectedEvidenceId === evidence.id;

                    return (
                      <button
                        key={evidence.id}
                        type="button"
                        onClick={() => onSelectEvidence?.(evidence)}
                        aria-pressed={isSelected}
                        aria-label={`${evidence.title} 증적 선택`}
                        style={{
                          ...evidenceRowStyle,
                          width: "100%",
                          boxSizing: "border-box",
                          textAlign: "left",
                          font: "inherit",
                          color: "inherit",
                          cursor: "pointer",
                          border: isSelected
                            ? "1px solid #93c5fd"
                            : "1px solid transparent",
                          borderTop: isSelected
                            ? "1px solid #93c5fd"
                            : "1px solid #e5e7eb",
                          borderRadius: "4px",
                          padding: "12px 8px",
                          backgroundColor: isSelected
                            ? "#eff6ff"
                            : "transparent",
                        }}
                      >
                        <span
                          aria-hidden="true"
                          style={{
                            color: isSelected ? "#2563eb" : "#64748b",
                            fontSize: "18px",
                            flexShrink: 0,
                          }}
                        >
                          ▤
                        </span>

                        <div style={evidenceInfoStyle}>
                          <div
                            style={{
                              fontWeight: isSelected ? 700 : 500,
                              overflowWrap: "anywhere",
                            }}
                          >
                            {evidence.title}
                          </div>

                          <div style={metadataStyle}>
                            {evidence.evidenceKey}
                            {" · "}
                            {EVIDENCE_TYPE_LABELS[
                              evidence.evidenceType
                            ] ?? evidence.evidenceType}
                            {evidence.fileName
                              ? ` · ${evidence.fileName}`
                              : ""}
                          </div>

                          {evidence.description && (
                            <div style={evidenceDescriptionStyle}>
                              {evidence.description}
                            </div>
                          )}

                          <div style={metadataStyle}>
                            등록자: {evidence.submittedByName}
                            {" · "}
                            등록일:{" "}
                            {new Date(
                              evidence.submittedAt,
                            ).toLocaleString("ko-KR")}
                          </div>
                        </div>

                        <span
                          style={{
                            ...verificationBadgeStyle,
                            color: verificationConfig.color,
                            borderColor: verificationConfig.color,
                          }}
                        >
                          {verificationConfig.label}
                        </span>
                      </button>
                    );
                  })}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Evidence Loading Error */}
      {!loading && evidenceError && (
        <div role="alert" style={errorStyle}>
          {evidenceError}
        </div>
      )}

      {/* Summary */}
      {!loading && !error && requirements.length > 0 && (
        <div style={summaryStyle}>
          요구사항 증적 등록 {presentCount} / 전체 요구사항{" "}
          {requirements.length}
          {" · "}
          조회된 증적 {totalEvidenceCount}건
        </div>
      )}
    </div>
  );
}

const cardStyle: CSSProperties = {
  padding: "20px",
  backgroundColor: "#ffffff",
  border: "1px solid #dddddd",
  borderRadius: "6px",
};

const headerStyle: CSSProperties = {
  display: "flex",
  alignItems: "flex-start",
  justifyContent: "space-between",
  gap: "12px",
  flexWrap: "wrap",
  marginBottom: "20px",
};

const subtitleStyle: CSSProperties = {
  marginTop: "8px",
  marginBottom: 0,
  color: "#666666",
};

const formStyle: CSSProperties = {
  display: "grid",
  gap: "14px",
  marginBottom: "20px",
  padding: "16px",
  border: "1px solid #dbeafe",
  borderRadius: "6px",
  backgroundColor: "#f8fbff",
};

const fieldStyle: CSSProperties = {
  display: "grid",
  gap: "6px",
};

const formGridStyle: CSSProperties = {
  display: "grid",
  gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
  gap: "14px",
};

const inputStyle: CSSProperties = {
  width: "100%",
  boxSizing: "border-box",
  padding: "9px 10px",
  border: "1px solid #cccccc",
  borderRadius: "4px",
  backgroundColor: "#ffffff",
  fontSize: "14px",
};

const checkboxLabelStyle: CSSProperties = {
  display: "flex",
  alignItems: "center",
  gap: "8px",
};

const formActionsStyle: CSSProperties = {
  display: "flex",
  justifyContent: "flex-end",
  gap: "8px",
  flexWrap: "wrap",
};

const primaryButtonStyle: CSSProperties = {
  padding: "9px 14px",
  border: "none",
  borderRadius: "4px",
  backgroundColor: "#1976d2",
  color: "#ffffff",
  cursor: "pointer",
};

const secondaryButtonStyle: CSSProperties = {
  padding: "9px 14px",
  border: "1px solid #cccccc",
  borderRadius: "4px",
  backgroundColor: "#ffffff",
  color: "#333333",
  cursor: "pointer",
};

const smallButtonStyle: CSSProperties = {
  padding: "5px 9px",
  border: "1px solid #bdbdbd",
  borderRadius: "4px",
  backgroundColor: "#ffffff",
  color: "#333333",
  cursor: "pointer",
};

const requirementContainerStyle: CSSProperties = {
  borderBottom: "1px solid #eeeeee",
};

const requirementRowStyle: CSSProperties = {
  display: "flex",
  alignItems: "center",
  justifyContent: "space-between",
  gap: "12px",
  padding: "12px 0",
};

const requirementInfoStyle: CSSProperties = {
  display: "flex",
  alignItems: "center",
  gap: "8px",
  minWidth: 0,
};

const metadataStyle: CSSProperties = {
  marginTop: "4px",
  fontSize: "12px",
  color: "#777777",
};

const rowActionsStyle: CSSProperties = {
  display: "flex",
  alignItems: "center",
  gap: "10px",
  flexShrink: 0,
};

const evidenceSectionStyle: CSSProperties = {
  margin: "0 0 14px 24px",
  padding: "12px",
  borderLeft: "3px solid #dbeafe",
  borderRadius: "0 4px 4px 0",
  backgroundColor: "#f8fafc",
};

const evidenceSectionHeaderStyle: CSSProperties = {
  display: "flex",
  alignItems: "center",
  gap: "8px",
  marginBottom: "8px",
};

const evidenceRowStyle: CSSProperties = {
  display: "flex",
  alignItems: "flex-start",
  gap: "10px",
  padding: "12px 0",
  borderTop: "1px solid #e5e7eb",
};

const evidenceInfoStyle: CSSProperties = {
  flex: 1,
  minWidth: 0,
  fontSize: "14px",
};

const evidenceDescriptionStyle: CSSProperties = {
  marginTop: "6px",
  fontSize: "13px",
  color: "#555555",
  overflowWrap: "anywhere",
};

const evidenceMessageStyle: CSSProperties = {
  padding: "8px 0",
  fontSize: "13px",
  color: "#777777",
};

const verificationBadgeStyle: CSSProperties = {
  flexShrink: 0,
  padding: "4px 7px",
  border: "1px solid",
  borderRadius: "4px",
  fontSize: "12px",
  fontWeight: 500,
};

const messageStyle: CSSProperties = {
  padding: "24px 0",
  color: "#666666",
  textAlign: "center",
};

const summaryStyle: CSSProperties = {
  marginTop: "16px",
  paddingTop: "12px",
  color: "#666666",
  fontSize: "14px",
};

const errorStyle: CSSProperties = {
  padding: "12px",
  border: "1px solid #fecaca",
  borderRadius: "4px",
  backgroundColor: "#fff5f5",
  color: "#b91c1c",
};

const successStyle: CSSProperties = {
  marginBottom: "16px",
  padding: "12px",
  border: "1px solid #bbf7d0",
  borderRadius: "4px",
  backgroundColor: "#f0fdf4",
  color: "#166534",
};

export default EvidenceChecklist;