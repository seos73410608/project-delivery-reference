import { useEffect, useState } from "react";

import type {
  ChangeCreateRequest,
  ChangeImpactLevel,
  ChangePriority,
  ChangeResponse,
  ChangeType,
} from "../types/change";

import {
  CHANGE_IMPACT_LEVEL_LABELS,
  CHANGE_PRIORITY_LABELS,
  CHANGE_TYPE_LABELS,
} from "../types/change";

interface ChangeFormProps {
  initialValues?: Partial<ChangeResponse>;
  loading?: boolean;
  submitLabel?: string;
  onSubmit: (request: ChangeCreateRequest) => void | Promise<void>;
  onCancel?: () => void;
}

interface ChangeFormState {
  title: string;
  description: string;
  priority: ChangePriority;
  changeType: ChangeType;
  impactLevel: ChangeImpactLevel;
  requesterId: string;
  assigneeId: string;
  identifiedDate: string;
  requestedDate: string;
  dueDate: string;
  impactAnalysis: string;
  implementationPlan: string;
}

const DEFAULT_VALUES: ChangeFormState = {
  title: "",
  description: "",
  priority: "MEDIUM",
  changeType: "OTHER",
  impactLevel: "MEDIUM",
  requesterId: "",
  assigneeId: "",
  identifiedDate: "",
  requestedDate: "",
  dueDate: "",
  impactAnalysis: "",
  implementationPlan: "",
};

const PRIORITY_OPTIONS = Object.entries(
  CHANGE_PRIORITY_LABELS,
) as [ChangePriority, string][];

const TYPE_OPTIONS = Object.entries(
  CHANGE_TYPE_LABELS,
) as [ChangeType, string][];

const IMPACT_OPTIONS = Object.entries(
  CHANGE_IMPACT_LEVEL_LABELS,
) as [ChangeImpactLevel, string][];

function toDateInputValue(value?: string | null): string {
  return value ? value.slice(0, 10) : "";
}

function toFormState(
  initialValues?: Partial<ChangeResponse>,
): ChangeFormState {
  return {
    title: initialValues?.title ?? "",
    description: initialValues?.description ?? "",
    priority: initialValues?.priority ?? DEFAULT_VALUES.priority,
    changeType: initialValues?.changeType ?? DEFAULT_VALUES.changeType,
    impactLevel:
      initialValues?.impactLevel ?? DEFAULT_VALUES.impactLevel,
    requesterId:
      initialValues?.requesterId == null
        ? ""
        : String(initialValues.requesterId),
    assigneeId:
      initialValues?.assigneeId == null
        ? ""
        : String(initialValues.assigneeId),
    identifiedDate: toDateInputValue(initialValues?.identifiedDate),
    requestedDate: toDateInputValue(initialValues?.requestedDate),
    dueDate: toDateInputValue(initialValues?.dueDate),
    impactAnalysis: initialValues?.impactAnalysis ?? "",
    implementationPlan: initialValues?.implementationPlan ?? "",
  };
}

function toOptionalNumber(value: string): number | undefined {
  if (!value.trim()) {
    return undefined;
  }

  const parsed = Number(value);
  return Number.isInteger(parsed) && parsed > 0 ? parsed : undefined;
}

export default function ChangeForm({
  initialValues,
  loading = false,
  submitLabel = "저장",
  onSubmit,
  onCancel,
}: ChangeFormProps) {
  const [form, setForm] = useState<ChangeFormState>(() =>
    toFormState(initialValues),
  );
  const [validationMessage, setValidationMessage] = useState("");

  useEffect(() => {
    setForm(toFormState(initialValues));
    setValidationMessage("");
  }, [initialValues]);

  const updateField = <K extends keyof ChangeFormState>(
    field: K,
    value: ChangeFormState[K],
  ) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }));
  };

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();
    setValidationMessage("");

    const title = form.title.trim();

    if (!title) {
      setValidationMessage("변경 요청 제목을 입력해 주세요.");
      return;
    }

    if (form.requestedDate && form.identifiedDate &&
        form.requestedDate < form.identifiedDate) {
      setValidationMessage("요청일은 식별일보다 빠를 수 없습니다.");
      return;
    }

    if (form.dueDate && form.requestedDate &&
        form.dueDate < form.requestedDate) {
      setValidationMessage("완료 예정일은 요청일보다 빠를 수 없습니다.");
      return;
    }

    if (form.requesterId.trim() &&
        toOptionalNumber(form.requesterId) === undefined) {
      setValidationMessage("요청자 ID는 1 이상의 정수로 입력해 주세요.");
      return;
    }

    if (form.assigneeId.trim() &&
        toOptionalNumber(form.assigneeId) === undefined) {
      setValidationMessage("담당자 ID는 1 이상의 정수로 입력해 주세요.");
      return;
    }

    const request: ChangeCreateRequest = {
      title,
      description: form.description.trim() || undefined,
      priority: form.priority,
      changeType: form.changeType,
      impactLevel: form.impactLevel,
      requesterId: toOptionalNumber(form.requesterId),
      assigneeId: toOptionalNumber(form.assigneeId),
      identifiedDate: form.identifiedDate || undefined,
      requestedDate: form.requestedDate || undefined,
      dueDate: form.dueDate || undefined,
      impactAnalysis: form.impactAnalysis.trim() || undefined,
      implementationPlan:
        form.implementationPlan.trim() || undefined,
    };

    await onSubmit(request);
  };

  return (
    <form className="change-form" onSubmit={handleSubmit}>
      <div className="change-form__body">
        <div className="change-form__field change-form__field--full">
          <label htmlFor="change-title">
            변경 요청 제목 <span className="change-form__required">*</span>
          </label>
          <input
            id="change-title"
            type="text"
            value={form.title}
            onChange={(event) =>
              updateField("title", event.target.value)
            }
            maxLength={200}
            placeholder="변경 요청 제목을 입력하세요."
            required
            disabled={loading}
          />
        </div>

        <div className="change-form__field change-form__field--full">
          <label htmlFor="change-description">변경 요청 내용</label>
          <textarea
            id="change-description"
            value={form.description}
            onChange={(event) =>
              updateField("description", event.target.value)
            }
            rows={4}
            placeholder="변경 요청의 배경과 상세 내용을 입력하세요."
            disabled={loading}
          />
        </div>

        <div className="change-form__field">
          <label htmlFor="change-priority">우선순위</label>
          <select
            id="change-priority"
            value={form.priority}
            onChange={(event) =>
              updateField(
                "priority",
                event.target.value as ChangePriority,
              )
            }
            disabled={loading}
          >
            {PRIORITY_OPTIONS.map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="change-form__field">
          <label htmlFor="change-type">변경 유형</label>
          <select
            id="change-type"
            value={form.changeType}
            onChange={(event) =>
              updateField(
                "changeType",
                event.target.value as ChangeType,
              )
            }
            disabled={loading}
          >
            {TYPE_OPTIONS.map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="change-form__field">
          <label htmlFor="change-impact-level">영향도</label>
          <select
            id="change-impact-level"
            value={form.impactLevel}
            onChange={(event) =>
              updateField(
                "impactLevel",
                event.target.value as ChangeImpactLevel,
              )
            }
            disabled={loading}
          >
            {IMPACT_OPTIONS.map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="change-form__field">
          <label htmlFor="change-requester-id">요청자 ID</label>
          <input
            id="change-requester-id"
            type="number"
            min={1}
            step={1}
            value={form.requesterId}
            onChange={(event) =>
              updateField("requesterId", event.target.value)
            }
            placeholder="요청자 ID"
            disabled={loading}
          />
        </div>

        <div className="change-form__field">
          <label htmlFor="change-assignee-id">담당자 ID</label>
          <input
            id="change-assignee-id"
            type="number"
            min={1}
            step={1}
            value={form.assigneeId}
            onChange={(event) =>
              updateField("assigneeId", event.target.value)
            }
            placeholder="담당자 ID"
            disabled={loading}
          />
        </div>

        <div className="change-form__field">
          <label htmlFor="change-identified-date">식별일</label>
          <input
            id="change-identified-date"
            type="date"
            value={form.identifiedDate}
            onChange={(event) =>
              updateField("identifiedDate", event.target.value)
            }
            disabled={loading}
          />
        </div>

        <div className="change-form__field">
          <label htmlFor="change-requested-date">요청일</label>
          <input
            id="change-requested-date"
            type="date"
            value={form.requestedDate}
            onChange={(event) =>
              updateField("requestedDate", event.target.value)
            }
            disabled={loading}
          />
        </div>

        <div className="change-form__field">
          <label htmlFor="change-due-date">완료 예정일</label>
          <input
            id="change-due-date"
            type="date"
            value={form.dueDate}
            onChange={(event) =>
              updateField("dueDate", event.target.value)
            }
            min={form.requestedDate || undefined}
            disabled={loading}
          />
        </div>

        <div className="change-form__field change-form__field--full">
          <label htmlFor="change-impact-analysis">영향 분석</label>
          <textarea
            id="change-impact-analysis"
            value={form.impactAnalysis}
            onChange={(event) =>
              updateField("impactAnalysis", event.target.value)
            }
            rows={4}
            placeholder="일정, 비용, 품질, 시스템 등에 미치는 영향을 작성하세요."
            disabled={loading}
          />
        </div>

        <div className="change-form__field change-form__field--full">
          <label htmlFor="change-implementation-plan">구현 계획</label>
          <textarea
            id="change-implementation-plan"
            value={form.implementationPlan}
            onChange={(event) =>
              updateField("implementationPlan", event.target.value)
            }
            rows={4}
            placeholder="변경 사항의 구현 및 검증 계획을 작성하세요."
            disabled={loading}
          />
        </div>
      </div>

      {validationMessage && (
        <div className="change-form__error" role="alert">
          {validationMessage}
        </div>
      )}

      <div className="change-form__actions">
        {onCancel && (
          <button
            type="button"
            className="change-form__button change-form__button--secondary"
            onClick={onCancel}
            disabled={loading}
          >
            취소
          </button>
        )}

        <button
          type="submit"
          className="change-form__button change-form__button--primary"
          disabled={loading}
        >
          {loading ? "저장 중..." : submitLabel}
        </button>
      </div>
    </form>
  );
}