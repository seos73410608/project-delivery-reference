
import { useEffect, useState } from 'react';

import type {
  OperationResponse,
  ProjectOperationRelationshipType,
  ProjectOperationResponse,
} from '../types/projectOperation';

import './ProjectOperationForm.css';


/* =========================================================
   Props
   ========================================================= */

interface ProjectOperationFormProps {
  projectId: number;
  operations: OperationResponse[];
  projectOperation?: ProjectOperationResponse | null;
  loading?: boolean;
  saving: boolean;
  error: string | null;
  onSubmit: (values: {
    operationId?: number;
    relationshipType: ProjectOperationRelationshipType;
    effectiveFrom: string;
    effectiveTo: string;
    description: string;
  }) => void;
  onCancel: () => void;
}


/* =========================================================
   Labels
   ========================================================= */

const RELATIONSHIP_TYPE_OPTIONS: {
  value: ProjectOperationRelationshipType;
  label: string;
}[] = [
  { value: 'BUILD_RESULT', label: '구축 결과' },
  { value: 'ENHANCEMENT', label: '기능 개선' },
  { value: 'MIGRATION', label: '마이그레이션' },
  { value: 'OTHER', label: '기타' },
];

const OPERATION_TYPE_LABELS: Record<string, string> = {
  BUSINESS_SERVICE: '비즈니스 서비스',
  APPLICATION_SERVICE: '애플리케이션',
  INFRASTRUCTURE_SERVICE: '인프라',
  PLATFORM_SERVICE: '플랫폼',
  DATABASE_SERVICE: '데이터베이스',
  OTHER: '기타',
};


/* =========================================================
   Component
   ========================================================= */

function ProjectOperationForm({
  projectId,
  operations,
  projectOperation = null,
  loading = false,
  saving,
  error,
  onSubmit,
  onCancel,
}: ProjectOperationFormProps) {

  const isEditMode = Boolean(projectOperation);

  const [operationId, setOperationId] = useState(
    projectOperation?.operationId
      ? String(projectOperation.operationId)
      : '',
  );

  const [relationshipType, setRelationshipType] =
    useState<ProjectOperationRelationshipType>(
      projectOperation?.relationshipType ?? 'BUILD_RESULT',
    );

  const [effectiveFrom, setEffectiveFrom] = useState(
    projectOperation?.effectiveFrom ?? '',
  );

  const [effectiveTo, setEffectiveTo] = useState(
    projectOperation?.effectiveTo ?? '',
  );

  const [description, setDescription] = useState(
    projectOperation?.description ?? '',
  );

  const [validationError, setValidationError] = useState<string | null>(
    null,
  );


  useEffect(() => {
    setOperationId(
      projectOperation?.operationId
        ? String(projectOperation.operationId)
        : '',
    );

    setRelationshipType(
      projectOperation?.relationshipType ?? 'BUILD_RESULT',
    );

    setEffectiveFrom(projectOperation?.effectiveFrom ?? '');
    setEffectiveTo(projectOperation?.effectiveTo ?? '');
    setDescription(projectOperation?.description ?? '');
    setValidationError(null);
  }, [projectOperation]);


  /* =========================================================
     Submit
     ========================================================= */

  const handleSubmit = (
    event: React.FormEvent<HTMLFormElement>,
  ) => {
    event.preventDefault();

    setValidationError(null);

    if (!isEditMode && !operationId) {
      setValidationError('연결할 운영 서비스를 선택해 주세요.');
      return;
    }

    if (effectiveTo && !effectiveFrom) {
      setValidationError(
        '종료일을 입력하려면 시작일을 먼저 입력해야 합니다.',
      );
      return;
    }

    if (
      effectiveFrom &&
      effectiveTo &&
      effectiveFrom > effectiveTo
    ) {
      setValidationError('종료일은 시작일보다 빠를 수 없습니다.');
      return;
    }

    onSubmit({
      ...(isEditMode
        ? {}
        : { operationId: Number(operationId) }),
      relationshipType,
      effectiveFrom,
      effectiveTo,
      description: description.trim(),
    });
  };


  const selectedOperation = operations.find(
    (item) => item.id === Number(operationId),
  );

  const currentOperationName =
    projectOperation?.operationName ??
    selectedOperation?.operationName ??
    '';


  return (
    <form
      className="project-operation-form"
      onSubmit={handleSubmit}
    >

      <div className="project-operation-form__header">
        <h3>
          {isEditMode ? '운영 서비스 연결 수정' : '운영 서비스 연결'}
        </h3>

        <p>
          프로젝트와 운영 서비스 간 관계 정보를 관리합니다.
        </p>
      </div>


      {/* Project */}

      <div className="project-operation-form__field">
        <label htmlFor="project-operation-project">
          프로젝트 ID
        </label>

        <input
          id="project-operation-project"
          type="text"
          value={projectId}
          disabled
        />
      </div>


      {/* Operation */}

      <div className="project-operation-form__field">
        <label htmlFor="project-operation-operation">
          운영 서비스 <span className="required">*</span>
        </label>

        {isEditMode ? (
          <div className="project-operation-form__readonly">
            <strong>{currentOperationName}</strong>

            <span>
              {projectOperation?.operationCode}
            </span>

            <small>
              {projectOperation
                ? OPERATION_TYPE_LABELS[projectOperation.operationType] ??
                  projectOperation.operationType
                : ''}
            </small>
          </div>
        ) : (
          <select
            id="project-operation-operation"
            value={operationId}
            onChange={(event) => setOperationId(event.target.value)}
            disabled={loading || saving}
            required
          >
            <option value="">
              운영 서비스를 선택해 주세요.
            </option>

            {operations.map((operation) => (
              <option
                key={operation.id}
                value={operation.id}
              >
                {operation.operationCode} - {operation.operationName}
                {' ('}
                {OPERATION_TYPE_LABELS[operation.operationType] ??
                  operation.operationType}
                {')'}
              </option>
            ))}
          </select>
        )}
      </div>


      {/* Relationship Type */}

      <div className="project-operation-form__field">
        <label htmlFor="project-operation-relationship-type">
          관계 유형 <span className="required">*</span>
        </label>

        <select
          id="project-operation-relationship-type"
          value={relationshipType}
          onChange={(event) =>
            setRelationshipType(
              event.target.value as ProjectOperationRelationshipType,
            )
          }
          disabled={saving}
          required
        >
          {RELATIONSHIP_TYPE_OPTIONS.map((option) => (
            <option
              key={option.value}
              value={option.value}
            >
              {option.label}
            </option>
          ))}
        </select>
      </div>


      {/* Effective Period */}

      <div className="project-operation-form__row">

        <div className="project-operation-form__field">
          <label htmlFor="project-operation-effective-from">
            적용 시작일
          </label>

          <input
            id="project-operation-effective-from"
            type="date"
            value={effectiveFrom}
            onChange={(event) => setEffectiveFrom(event.target.value)}
            disabled={saving}
          />
        </div>

        <div className="project-operation-form__field">
          <label htmlFor="project-operation-effective-to">
            적용 종료일
          </label>

          <input
            id="project-operation-effective-to"
            type="date"
            value={effectiveTo}
            min={effectiveFrom || undefined}
            onChange={(event) => setEffectiveTo(event.target.value)}
            disabled={saving}
          />
        </div>

      </div>


      {/* Description */}

      <div className="project-operation-form__field">
        <label htmlFor="project-operation-description">
          설명
        </label>

        <textarea
          id="project-operation-description"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
          rows={4}
          placeholder="프로젝트와 운영 서비스 간 관계에 대한 설명을 입력해 주세요."
          disabled={saving}
        />
      </div>


      {/* Errors */}

      {validationError && (
        <div
          className="project-operation-form__error"
          role="alert"
        >
          {validationError}
        </div>
      )}

      {error && (
        <div
          className="project-operation-form__error"
          role="alert"
        >
          {error}
        </div>
      )}


      {/* Actions */}

      <div className="project-operation-form__actions">

        <button
          type="button"
          className="button button--secondary"
          onClick={onCancel}
          disabled={saving}
        >
          취소
        </button>

        <button
          type="submit"
          className="button button--primary"
          disabled={saving || loading}
        >
          {saving
            ? '저장 중...'
            : isEditMode
              ? '수정'
              : '연결'}
        </button>

      </div>

    </form>
  );
}

export default ProjectOperationForm;
