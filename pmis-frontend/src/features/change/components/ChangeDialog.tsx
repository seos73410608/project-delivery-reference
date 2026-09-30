import { useEffect, useState } from "react";

import {
  createChange,
  updateChange,
} from "../api/changeApi";

import type {
  ChangeCreateRequest,
  ChangeResponse,
} from "../types/change";

import ChangeForm from "./ChangeForm";

interface ChangeDialogProps {
  open: boolean;
  projectId: number;
  change?: ChangeResponse | null;
  onClose: () => void;
  onSaved?: (change: ChangeResponse) => void | Promise<void>;
}

export default function ChangeDialog({
  open,
  projectId,
  change = null,
  onClose,
  onSaved,
}: ChangeDialogProps) {
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const isEditMode = change !== null;

  useEffect(() => {
    if (open) {
      setErrorMessage("");
      setLoading(false);
    }
  }, [open, change?.id]);

  useEffect(() => {
    if (!open) {
      return;
    }

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape" && !loading) {
        onClose();
      }
    };

    window.addEventListener("keydown", handleKeyDown);

    return () => {
      window.removeEventListener("keydown", handleKeyDown);
    };
  }, [open, loading, onClose]);

  if (!open) {
    return null;
  }

  const handleSubmit = async (request: ChangeCreateRequest) => {
    setLoading(true);
    setErrorMessage("");

    try {
      const savedChange = isEditMode
        ? await updateChange(change.id, request)
        : await createChange(projectId, request);

      await onSaved?.(savedChange);
      onClose();
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "변경 요청을 저장하는 중 오류가 발생했습니다.",
      );
    } finally {
      setLoading(false);
    }
  };

  const handleBackdropClick = (
    event: React.MouseEvent<HTMLDivElement>,
  ) => {
    if (event.target === event.currentTarget && !loading) {
      onClose();
    }
  };

  return (
    <div
      className="change-dialog__backdrop"
      onMouseDown={handleBackdropClick}
    >
      <section
        className="change-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="change-dialog-title"
      >
        <header className="change-dialog__header">
          <div>
            <h2 id="change-dialog-title" className="change-dialog__title">
              {isEditMode ? "변경 요청 수정" : "변경 요청 등록"}
            </h2>
            <p className="change-dialog__description">
              {isEditMode
                ? "변경 요청 정보를 수정합니다."
                : "새로운 변경 요청 정보를 입력합니다."}
            </p>
          </div>

          <button
            type="button"
            className="change-dialog__close"
            onClick={onClose}
            disabled={loading}
            aria-label="다이얼로그 닫기"
          >
            ×
          </button>
        </header>

        <div className="change-dialog__content">
          {errorMessage && (
            <div className="change-dialog__error" role="alert">
              {errorMessage}
            </div>
          )}

          <ChangeForm
            initialValues={change ?? undefined}
            loading={loading}
            submitLabel={isEditMode ? "수정 저장" : "등록"}
            onSubmit={handleSubmit}
            onCancel={onClose}
          />
        </div>
      </section>
    </div>
  );
}