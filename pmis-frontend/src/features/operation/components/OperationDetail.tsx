import type {
    OperationResponse,
    OperationType,
    OperationStatus,
} from "../types/operation";

import "./OperationDetail.css";


interface OperationDetailProps {
    operation: OperationResponse;
}


const OPERATION_TYPE_LABELS: Record<OperationType, string> = {
    BUSINESS_SERVICE: "업무 서비스",
    APPLICATION_SERVICE: "애플리케이션 서비스",
    INFRASTRUCTURE_SERVICE: "인프라 서비스",
    PLATFORM_SERVICE: "플랫폼 서비스",
    DATABASE_SERVICE: "데이터베이스 서비스",
    OTHER: "기타",
};


const OPERATION_STATUS_LABELS: Record<OperationStatus, string> = {
    PLANNED: "계획",
    ACTIVE: "운영 중",
    SUSPENDED: "중지",
    CLOSED: "종료",
};


const OPERATION_STATUS_CLASSES: Record<OperationStatus, string> = {
    PLANNED: "operation-detail-status--planned",
    ACTIVE: "operation-detail-status--active",
    SUSPENDED: "operation-detail-status--suspended",
    CLOSED: "operation-detail-status--closed",
};


function formatDate(value: string | null): string {
    if (!value) {
        return "-";
    }

    return value;
}


function formatDateTime(value: string): string {
    if (!value) {
        return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return new Intl.DateTimeFormat("ko-KR", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
    }).format(date);
}


function OperationDetail({
    operation,
}: OperationDetailProps) {

    return (
        <div className="operation-detail">

            {/* 운영 서비스 요약 */}

            <section className="operation-detail-card">

                <div className="operation-detail-summary">

                    <div className="operation-detail-title">

                        <div className="operation-detail-code">
                            {operation.operationCode}
                        </div>

                        <h2>
                            {operation.operationName}
                        </h2>

                        <p>
                            {OPERATION_TYPE_LABELS[operation.operationType]}
                        </p>

                    </div>

                    <span
                        className={[
                            "operation-detail-status",
                            OPERATION_STATUS_CLASSES[operation.status],
                        ].join(" ")}
                    >
                        {OPERATION_STATUS_LABELS[operation.status]}
                    </span>

                </div>

                {operation.description && (
                    <div className="operation-detail-description">
                        {operation.description}
                    </div>
                )}

            </section>


            {/* 운영 서비스 정보 */}

            <section className="operation-detail-card">

                <h3 className="operation-detail-section-title">
                    운영 서비스 정보
                </h3>

                <div className="operation-detail-grid">

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            운영 코드
                        </span>
                        <span className="operation-detail-value">
                            {operation.operationCode}
                        </span>
                    </div>

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            운영 서비스명
                        </span>
                        <span className="operation-detail-value">
                            {operation.operationName}
                        </span>
                    </div>

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            운영 유형
                        </span>
                        <span className="operation-detail-value">
                            {OPERATION_TYPE_LABELS[operation.operationType]}
                        </span>
                    </div>

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            운영 상태
                        </span>
                        <span className="operation-detail-value">
                            {OPERATION_STATUS_LABELS[operation.status]}
                        </span>
                    </div>

                </div>

            </section>


            {/* 담당자 및 운영 기간 */}

            <section className="operation-detail-card">

                <h3 className="operation-detail-section-title">
                    담당자 및 운영 기간
                </h3>

                <div className="operation-detail-grid">

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            담당자
                        </span>
                        <span className="operation-detail-value">
                            {operation.managerName || "미지정"}
                        </span>
                    </div>

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            담당자 사용자 ID
                        </span>
                        <span className="operation-detail-value">
                            {operation.managerUserId ?? "-"}
                        </span>
                    </div>

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            운영 시작일
                        </span>
                        <span className="operation-detail-value">
                            {formatDate(operation.effectiveFrom)}
                        </span>
                    </div>

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            운영 종료일
                        </span>
                        <span className="operation-detail-value">
                            {formatDate(operation.effectiveTo)}
                        </span>
                    </div>

                </div>

            </section>


            {/* 등록 및 수정 정보 */}

            <section className="operation-detail-card">

                <h3 className="operation-detail-section-title">
                    등록 및 수정 정보
                </h3>

                <div className="operation-detail-grid">

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            등록일
                        </span>
                        <span className="operation-detail-value">
                            {formatDateTime(operation.createdAt)}
                        </span>
                    </div>

                    <div className="operation-detail-item">
                        <span className="operation-detail-label">
                            최종 수정일
                        </span>
                        <span className="operation-detail-value">
                            {formatDateTime(operation.updatedAt)}
                        </span>
                    </div>

                </div>

            </section>

        </div>
    );
}


export default OperationDetail;