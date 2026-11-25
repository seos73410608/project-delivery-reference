
import type {
  ProjectOperationResponse,
  ProjectOperationStatus,
} from '../types/projectOperation';

import './ProjectOperationList.css';


/* =========================================================
   Props
   ========================================================= */

interface ProjectOperationListProps {
  projectOperations: ProjectOperationResponse[];
  loading: boolean;
  error: string | null;
  isAdmin: boolean;
  onCreate: () => void;
  onEdit: (projectOperation: ProjectOperationResponse) => void;
  onStatusChange: (
    projectOperation: ProjectOperationResponse,
    status: ProjectOperationStatus,
  ) => void;
}


/* =========================================================
   Labels
   ========================================================= */

const RELATIONSHIP_TYPE_LABELS: Record<string, string> = {
  BUILD_RESULT: '구축 결과',
  ENHANCEMENT: '기능 개선',
  MIGRATION: '마이그레이션',
  OTHER: '기타',
};

const STATUS_LABELS: Record<ProjectOperationStatus, string> = {
  PLANNED: '계획',
  ACTIVE: '운영 중',
  COMPLETED: '완료',
  CANCELLED: '취소',
};

const STATUS_BADGE_CLASSES: Record<ProjectOperationStatus, string> = {
  PLANNED: 'badge--planned',
  ACTIVE: 'badge--in-progress',
  COMPLETED: 'badge--completed',
  CANCELLED: 'badge--cancelled',
};

const OPERATION_TYPE_LABELS: Record<string, string> = {
  BUSINESS_SERVICE: '비즈니스 서비스',
  APPLICATION_SERVICE: '애플리케이션',
  INFRASTRUCTURE_SERVICE: '인프라',
  PLATFORM_SERVICE: '플랫폼',
  DATABASE_SERVICE: '데이터베이스',
  OTHER: '기타',
};

const OPERATION_STATUS_LABELS: Record<string, string> = {
  PLANNED: '계획',
  ACTIVE: '운영 중',
  SUSPENDED: '중지',
  CLOSED: '종료',
};


/* =========================================================
   Formatting
   ========================================================= */

const formatDate = (
  value: string | null,
): string => {
  if (!value) {
    return '-';
  }

  return value;
};


/* =========================================================
   Component
   ========================================================= */

function ProjectOperationList({
  projectOperations,
  loading,
  error,
  isAdmin,
  onCreate,
  onEdit,
  onStatusChange,
}: ProjectOperationListProps) {

  const getAvailableTransitions = (
    status: ProjectOperationStatus,
  ): ProjectOperationStatus[] => {
    switch (status) {
      case 'PLANNED':
        return ['ACTIVE', 'CANCELLED'];

      case 'ACTIVE':
        return ['COMPLETED', 'CANCELLED'];

      case 'COMPLETED':
      case 'CANCELLED':
        return [];

      default:
        return [];
    }
  };


  return (
    <section className="project-operation-list">

      {/* Header */}

      <div className="project-operation-list__header">

        <div>
          <span className="project-operation-list__section-label">
            PROJECT OPERATIONS
          </span>

          <h3>
            연계 운영 서비스
            <span className="project-operation-list__count">
              {projectOperations.length}
            </span>
          </h3>

          <p>
            프로젝트와 연결된 운영 서비스 및 관계 정보를 관리합니다.
          </p>
        </div>

        {isAdmin && (
          <button
            type="button"
            className="button button--primary"
            onClick={onCreate}
            disabled={loading}
          >
            운영 서비스 연결
          </button>
        )}

      </div>


      {/* Loading */}

      {loading && (
        <div
          className="project-operation-list__state"
          role="status"
        >
          운영 서비스 관계를 불러오는 중입니다...
        </div>
      )}


      {/* Error */}

      {!loading && error && (
        <div
          className="project-operation-list__error"
          role="alert"
        >
          {error}
        </div>
      )}


      {/* Empty */}

      {!loading &&
        !error &&
        projectOperations.length === 0 && (
          <div className="project-operation-list__empty">
            <strong>
              연결된 운영 서비스가 없습니다.
            </strong>

            <span>
              프로젝트 구축 결과 또는 운영 서비스를 연결할 수 있습니다.
            </span>
          </div>
        )}


      {/* List */}

      {!loading &&
        !error &&
        projectOperations.length > 0 && (
          <div className="project-operation-list__items">

            {projectOperations.map((item) => {

              const transitions =
                getAvailableTransitions(item.status);

              return (
                <article
                  key={item.id}
                  className="project-operation-item"
                >

                  {/* Main */}

                  <div className="project-operation-item__main">

                    <div className="project-operation-item__heading">

                      <span className="project-operation-item__code">
                        {item.operationCode}
                      </span>

                      <span
                        className={`badge ${STATUS_BADGE_CLASSES[item.status]}`}
                      >
                        {STATUS_LABELS[item.status]}
                      </span>

                    </div>

                    <h4>
                      {item.operationName}
                    </h4>

                    <div className="project-operation-item__metadata">
                      <span>
                        {OPERATION_TYPE_LABELS[item.operationType] ??
                          item.operationType}
                      </span>

                      <span>
                        운영 상태: {OPERATION_STATUS_LABELS[item.operationStatus] ??
                          item.operationStatus}
                      </span>

                      <span>
                        관계 유형: {RELATIONSHIP_TYPE_LABELS[item.relationshipType] ??
                          item.relationshipType}
                      </span>
                    </div>

                    <div className="project-operation-item__period">
                      <span>
                        적용 기간
                      </span>

                      <strong>
                        {formatDate(item.effectiveFrom)}
                        {' ~ '}
                        {formatDate(item.effectiveTo)}
                      </strong>
                    </div>

                    {item.description && (
                      <p className="project-operation-item__description">
                        {item.description}
                      </p>
                    )}

                  </div>


                  {/* Actions */}

                  {isAdmin && (
                    <div className="project-operation-item__actions">

                      <button
                        type="button"
                        className="button button--secondary button--small"
                        onClick={() => onEdit(item)}
                      >
                        수정
                      </button>

                      {transitions.map((nextStatus) => (
                        <button
                          key={nextStatus}
                          type="button"
                          className="button button--secondary button--small"
                          onClick={() => onStatusChange(item, nextStatus)}
                        >
                          {STATUS_LABELS[nextStatus]}
                        </button>
                      ))}

                    </div>
                  )}

                </article>
              );
            })}

          </div>
        )}

    </section>
  );
}

export default ProjectOperationList;
