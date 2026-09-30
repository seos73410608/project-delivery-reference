import { useEffect, useState } from "react";

import type {
  ChangeApprovalRequest,
  ChangeResponse,
} from "../types/change";

interface ChangeApprovalFormProps {
  change: ChangeResponse;
  loading?: boolean;
  onSubmit: (request: ChangeApprovalRequest) => void | Promise<void>;
  onCancel?: () => void;
}

export default function ChangeApprovalForm({
  change,
  loading = false,
  onSubmit,
  onCancel,
}: ChangeApprovalFormProps) {
  const [approved, setApproved] = useState<boolean | null>(null);
  const [approvalComment, setApprovalComment] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    setApproved(null);
    setApprovalComment("");
    setErrorMessage("");
  }, [change.id]);

  const handleSubmit = async (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();
    setErrorMessage("");

    if (approved === null) {
      setErrorMessage("승인 또는 반려를 선택해 주세요.");
      return;
    }

    try {
      await onSubmit({
        approved,
        approvalComment: approvalComment.trim(),
      });
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "승인 처리 중 오류가 발생했습니다.",
      );
    }
  };

  return (
    <form className="change-approval-form" onSubmit={handleSubmit}>
      <div className="change-approval-form__summary">
        <span className="change-approval-form__label">변경 요청</span>
        <strong className="change-approval-form__title">
          {change.changeKey ? `${change.changeKey} · ` : ""}
          {change.title}
        </strong>
      </div>

      <fieldset
        className="change-approval-form__decision"
        disabled={loading}
      >
        <legend className="change-approval-form__label">
          처리 결과 <span aria-hidden="true">*</span>
        </legend>

        <label className="change-approval-form__option">
          <input
            type="radio"
            name="change-approval-decision"
            value="approve"
            checked={approved === true}
            onChange={() => setApproved(true)}
          />
          <span>승인</span>
        </label>

        <label className="change-approval-form__option">
          <input
            type="radio"
            name="change-approval-decision"
            value="reject"
            checked={approved === false}
            onChange={() => setApproved(false)}
          />
          <span>반려</span>
        </label>
      </fieldset>

      <div className="change-approval-form__field">
        <label htmlFor="change-approval-comment">
          승인 의견
        </label>

        <textarea
          id="change-approval-comment"
          value={approvalComment}
          onChange={(event) => setApprovalComment(event.target.value)}
          rows={4}
          maxLength={2000}
          placeholder="승인 또는 반려 사유와 검토 의견을 입력하세요."
          disabled={loading}
        />

        <div className="change-approval-form__counter">
          {approvalComment.length} / 2000
        </div>
      </div>

      {errorMessage && (
        <div className="change-approval-form__error" role="alert">
          {errorMessage}
        </div>
      )}

      <div className="change-approval-form__actions">
        {onCancel && (
          <button
            type="button"
            className="change-approval-form__button change-approval-form__button--secondary"
            onClick={onCancel}
            disabled={loading}
          >
            취소
          </button>
        )}

        <button
          type="submit"
          className="change-approval-form__button change-approval-form__button--primary"
          disabled={loading || approved === null}
        >
          {loading
            ? "처리 중..."
            : approved === true
              ? "승인 처리"
              : approved === false
                ? "반려 처리"
                : "처리"}
        </button>
      </div>
    </form>
  );
}