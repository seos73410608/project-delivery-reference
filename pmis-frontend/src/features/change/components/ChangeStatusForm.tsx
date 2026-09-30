import { useEffect, useState } from "react";

import type {
  ChangeResponse,
  ChangeStatus,
  ChangeStatusUpdateRequest,
} from "../types/change";

import { CHANGE_STATUS_LABELS } from "../types/change";

interface ChangeStatusFormProps {
  change: ChangeResponse;
  loading?: boolean;
  onSubmit: (request: ChangeStatusUpdateRequest) => void | Promise<void>;
  onCancel?: () => void;
}

const STATUS_OPTIONS = Object.entries(
  CHANGE_STATUS_LABELS,
) as [ChangeStatus, string][];

export default function ChangeStatusForm({
  change,
  loading = false,
  onSubmit,
  onCancel,
}: ChangeStatusFormProps) {
  const [status, setStatus] = useState<ChangeStatus>(change.status);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    setStatus(change.status);
    setErrorMessage("");
  }, [change.id, change.status]);

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();
    setErrorMessage("");

    if (status === change.status) {
      setErrorMessage("변경할 상태를 선택해 주세요.");
      return;
    }

    try {
      await onSubmit({ status });
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "상태를 변경하는 중 오류가 발생했습니다.",
      );
    }
  };

  return (
    <form className="change-status-form" onSubmit={handleSubmit}>
      <div className="change-status-form__current">
        <span className="change-status-form__label">현재 상태</span>
        <strong className="change-status-form__current-value">
          {CHANGE_STATUS_LABELS[change.status]}
        </strong>
      </div>

      <div className="change-status-form__field">
        <label htmlFor="change-status-select">
          변경할 상태
        </label>

        <select
          id="change-status-select"
          value={status}
          onChange={(event) =>
            setStatus(event.target.value as ChangeStatus)
          }
          disabled={loading}
          required
        >
          {STATUS_OPTIONS.map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
      </div>

      {errorMessage && (
        <div className="change-status-form__error" role="alert">
          {errorMessage}
        </div>
      )}

      <div className="change-status-form__actions">
        {onCancel && (
          <button
            type="button"
            className="change-status-form__button change-status-form__button--secondary"
            onClick={onCancel}
            disabled={loading}
          >
            취소
          </button>
        )}

        <button
          type="submit"
          className="change-status-form__button change-status-form__button--primary"
          disabled={loading || status === change.status}
        >
          {loading ? "변경 중..." : "상태 변경"}
        </button>
      </div>
    </form>
  );
}