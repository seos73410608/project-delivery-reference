import type { ReactNode } from "react";

import type {
  ChangeResponse,
  ChangeStatusUpdateRequest,
} from "../types/change";

import {
  CHANGE_IMPACT_LEVEL_LABELS,
  CHANGE_PRIORITY_LABELS,
  CHANGE_STATUS_LABELS,
  CHANGE_TYPE_LABELS,
} from "../types/change";

import ChangeStatusForm from "./ChangeStatusForm";

interface ChangeDetailProps {
  change: ChangeResponse;
  loading?: boolean;
  onStatusSubmit?: (
    request: ChangeStatusUpdateRequest,
  ) => void | Promise<void>;
  onEdit?: () => void;
  onClose?: () => void;
}

interface DetailItemProps {
  label: string;
  children: ReactNode;
  fullWidth?: boolean;
}

function DetailItem({
  label,
  children,
  fullWidth = false,
}: DetailItemProps) {
  return (
    <div
      className={`change-detail__item${
        fullWidth ? " change-detail__item--full" : ""
      }`}
    >
      <dt className="change-detail__label">{label}</dt>
      <dd className="change-detail__value">{children}</dd>
    </div>
  );
}

function displayText(value?: string | null): string {
  return value?.trim() ? value : "—";
}

function displayDate(value?: string | null): string {
  if (!value) {
    return "—";
  }

  return value.slice(0, 10);
}

export default function ChangeDetail({
  change,
  loading = false,
  onStatusSubmit,
  onEdit,
  onClose,
}: ChangeDetailProps) {
  return (
    <article className="change-detail">
      <header className="change-detail__header">
        <div className="change-detail__heading">
          <div className="change-detail__key">
            {displayText(change.changeKey)}
          </div>

          <h2 className="change-detail__title">
            {change.title}
          </h2>

          <div className="change-detail__badges">
            <span
              className={`change-detail__badge change-detail__badge--status-${change.status.toLowerCase()}`}
            >
              {CHANGE_STATUS_LABELS[change.status]}
            </span>

            <span
              className={`change-detail__badge change-detail__badge--priority-${change.priority.toLowerCase()}`}
            >
              {CHANGE_PRIORITY_LABELS[change.priority]}
            </span>
          </div>
        </div>

        <div className="change-detail__actions">
          {onEdit && (
            <button
              type="button"
              className="change-detail__edit"
              onClick={onEdit}
              disabled={loading}
            >
              수정
            </button>
          )}

          {onClose && (
            <button
              type="button"
              className="change-detail__close"
              onClick={onClose}
              disabled={loading}
              aria-label="상세 정보 닫기"
            >
              ×
            </button>
          )}
        </div>
      </header>

      <section className="change-detail__section">
        <h3 className="change-detail__section-title">
          요청 정보
        </h3>

        <dl className="change-detail__grid">
          <DetailItem label="변경 유형">
            {CHANGE_TYPE_LABELS[change.changeType]}
          </DetailItem>

          <DetailItem label="영향도">
            {CHANGE_IMPACT_LEVEL_LABELS[change.impactLevel]}
          </DetailItem>

          <DetailItem label="요청자 ID">
            {change.requesterId ?? "—"}
          </DetailItem>

          <DetailItem label="담당자 ID">
            {change.assigneeId ?? "—"}
          </DetailItem>

          <DetailItem label="식별일">
            {displayDate(change.identifiedDate)}
          </DetailItem>

          <DetailItem label="요청일">
            {displayDate(change.requestedDate)}
          </DetailItem>

          <DetailItem label="완료 예정일">
            {displayDate(change.dueDate)}
          </DetailItem>

          <DetailItem label="승인일">
            {displayDate(change.approvedDate)}
          </DetailItem>

          <DetailItem label="구현일">
            {displayDate(change.implementedDate)}
          </DetailItem>

          <DetailItem label="검증일">
            {displayDate(change.verifiedDate)}
          </DetailItem>

          <DetailItem label="종료일">
            {displayDate(change.closedDate)}
          </DetailItem>

          <DetailItem label="기한 초과">
            {change.overdue ? (
              <span className="change-detail__overdue">
                초과
              </span>
            ) : (
              "아니요"
            )}
          </DetailItem>
        </dl>
      </section>

      <section className="change-detail__section">
        <h3 className="change-detail__section-title">
          변경 요청 내용
        </h3>

        <p className="change-detail__paragraph">
          {displayText(change.description)}
        </p>
      </section>

      <section className="change-detail__section">
        <h3 className="change-detail__section-title">
          영향 분석
        </h3>

        <p className="change-detail__paragraph">
          {displayText(change.impactAnalysis)}
        </p>
      </section>

      <section className="change-detail__section">
        <h3 className="change-detail__section-title">
          구현 계획
        </h3>

        <p className="change-detail__paragraph">
          {displayText(change.implementationPlan)}
        </p>
      </section>

      <section className="change-detail__section">
        <h3 className="change-detail__section-title">
          검증 결과
        </h3>

        <p className="change-detail__paragraph">
          {displayText(change.verificationResult)}
        </p>
      </section>

      <section className="change-detail__section">
        <h3 className="change-detail__section-title">
          승인 의견
        </h3>

        <p className="change-detail__paragraph">
          {displayText(change.approvalComment)}
        </p>
      </section>

      {onStatusSubmit && (
        <section className="change-detail__section change-detail__section--status">
          <h3 className="change-detail__section-title">
            진행 상태 변경
          </h3>

          <ChangeStatusForm
            change={change}
            loading={loading}
            onSubmit={onStatusSubmit}
          />
        </section>
      )}
    </article>
  );
}