import {
  useCallback,
  useEffect,
  useMemo,
  useState,
} from 'react';

import { useNavigate } from 'react-router-dom';

import {
  useAuth,
} from '@/features/auth/context/AuthProvider';

import {
  getOperations,
} from '../api/operationApi';

import type {
  OperationResponse,
  OperationSearchParams,
  OperationStatus,
  OperationType,
} from '../types/operation';

import './OperationPage.css';

const OPERATION_TYPE_LABELS: Record<OperationType, string> = {
  BUSINESS_SERVICE: '비즈니스 서비스',
  APPLICATION_SERVICE: '애플리케이션 서비스',
  INFRASTRUCTURE_SERVICE: '인프라 서비스',
  PLATFORM_SERVICE: '플랫폼 서비스',
  DATABASE_SERVICE: '데이터베이스 서비스',
  OTHER: '기타',
};

const OPERATION_STATUS_LABELS: Record<OperationStatus, string> = {
  PLANNED: '계획',
  ACTIVE: '운영 중',
  SUSPENDED: '중지',
  CLOSED: '종료',
};

function OperationPage() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const isAdmin = user?.role === 'ROLE_ADMIN';

  const [operations, setOperations] = useState<OperationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [searchParams, setSearchParams] =
    useState<OperationSearchParams>({});

  const [searchCode, setSearchCode] = useState('');
  const [searchName, setSearchName] = useState('');
  const [searchType, setSearchType] = useState('');
  const [searchStatus, setSearchStatus] = useState('');

  const fetchOperations = useCallback(async () => {
    setLoading(true);
    setError('');

    try {
      const result = await getOperations();
      setOperations(result);
    } catch {
      setError('운영 서비스 목록을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void fetchOperations();
  }, [fetchOperations]);

  const filteredOperations = useMemo(() => {
    return operations.filter((operation) => {
      const codeMatches =
        !searchParams.operationCode ||
        operation.operationCode
          .toLowerCase()
          .includes(searchParams.operationCode.toLowerCase());

      const nameMatches =
        !searchParams.operationName ||
        operation.operationName
          .toLowerCase()
          .includes(searchParams.operationName.toLowerCase());

      const typeMatches =
        !searchParams.operationType ||
        operation.operationType === searchParams.operationType;

      const statusMatches =
        !searchParams.status ||
        operation.status === searchParams.status;

      return (
        codeMatches &&
        nameMatches &&
        typeMatches &&
        statusMatches
      );
    });
  }, [operations, searchParams]);

  const handleSearch = () => {
    setSearchParams({
      operationCode: searchCode.trim() || undefined,
      operationName: searchName.trim() || undefined,
      operationType: searchType
        ? searchType as OperationType
        : undefined,
      status: searchStatus
        ? searchStatus as OperationStatus
        : undefined,
    });
  };

  const handleReset = () => {
    setSearchCode('');
    setSearchName('');
    setSearchType('');
    setSearchStatus('');
    setSearchParams({});
  };

  const handleCreate = () => {
    navigate('/operation/create');
  };

  const handleDetail = (operationId: number) => {
    navigate(`/operation/${operationId}/detail`);
  };

  const formatDate = (date: string | null) => {
    return date || '-';
  };

  return (
    <div className="operation-page">
      <header className="operation-page__header">
        <div>
          <h1>Operation</h1>
          <p>운영 서비스 정보를 관리합니다.</p>
        </div>

        {isAdmin && (
          <button
            type="button"
            className="operation-button operation-button--primary"
            onClick={handleCreate}
          >
            운영 서비스 등록
          </button>
        )}
      </header>

      <section className="operation-summary">
        <div className="operation-summary__item">
          <span>전체 운영 서비스</span>
          <strong>{operations.length}</strong>
        </div>

        <div className="operation-summary__item">
          <span>운영 중</span>
          <strong>
            {operations.filter((item) => item.status === 'ACTIVE').length}
          </strong>
        </div>

        <div className="operation-summary__item">
          <span>계획</span>
          <strong>
            {operations.filter((item) => item.status === 'PLANNED').length}
          </strong>
        </div>

        <div className="operation-summary__item">
          <span>중지</span>
          <strong>
            {operations.filter((item) => item.status === 'SUSPENDED').length}
          </strong>
        </div>
      </section>

      <section className="operation-search">
        <div className="operation-search__field">
          <label htmlFor="operationCode">운영 서비스 코드</label>
          <input
            id="operationCode"
            value={searchCode}
            onChange={(event) => setSearchCode(event.target.value)}
            onKeyDown={(event) => {
              if (event.key === 'Enter') handleSearch();
            }}
            placeholder="코드 입력"
          />
        </div>

        <div className="operation-search__field">
          <label htmlFor="operationName">운영 서비스명</label>
          <input
            id="operationName"
            value={searchName}
            onChange={(event) => setSearchName(event.target.value)}
            onKeyDown={(event) => {
              if (event.key === 'Enter') handleSearch();
            }}
            placeholder="서비스명 입력"
          />
        </div>

        <div className="operation-search__field">
          <label htmlFor="operationType">서비스 유형</label>
          <select
            id="operationType"
            value={searchType}
            onChange={(event) => setSearchType(event.target.value)}
          >
            <option value="">전체</option>
            {Object.entries(OPERATION_TYPE_LABELS).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="operation-search__field">
          <label htmlFor="operationStatus">운영 상태</label>
          <select
            id="operationStatus"
            value={searchStatus}
            onChange={(event) => setSearchStatus(event.target.value)}
          >
            <option value="">전체</option>
            {Object.entries(OPERATION_STATUS_LABELS).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="operation-search__actions">
          <button
            type="button"
            className="operation-button operation-button--primary"
            onClick={handleSearch}
          >
            검색
          </button>

          <button
            type="button"
            className="operation-button operation-button--secondary"
            onClick={handleReset}
          >
            초기화
          </button>
        </div>
      </section>

      <section className="operation-list">
        <div className="operation-list__header">
          <h2>운영 서비스 목록</h2>
          <span>
            검색 결과 {filteredOperations.length}건
          </span>
        </div>

        {loading ? (
          <div className="operation-message">
            운영 서비스 목록을 불러오는 중입니다.
          </div>
        ) : error ? (
          <div className="operation-message operation-message--error">
            {error}
            <button
              type="button"
              className="operation-button operation-button--secondary"
              onClick={() => void fetchOperations()}
            >
              다시 시도
            </button>
          </div>
        ) : filteredOperations.length === 0 ? (
          <div className="operation-message">
            조회된 운영 서비스가 없습니다.
          </div>
        ) : (
          <div className="operation-table-wrapper">
            <table className="operation-table">
              <thead>
                <tr>
                  <th>운영 서비스 코드</th>
                  <th>운영 서비스명</th>
                  <th>서비스 유형</th>
                  <th>운영 담당자</th>
                  <th>운영 상태</th>
                  <th>시작일</th>
                  <th>종료일</th>
                </tr>
              </thead>

              <tbody>
                {filteredOperations.map((operation) => (
                  <tr key={operation.id}>
                    <td>{operation.operationCode}</td>

                    <td>
                      <button
                        type="button"
                        className="operation-table__link"
                        onClick={() => handleDetail(operation.id)}
                      >
                        {operation.operationName}
                      </button>
                    </td>

                    <td>
                      {OPERATION_TYPE_LABELS[operation.operationType]}
                    </td>

                    <td>{operation.managerName || '-'}</td>

                    <td>
                      <span
                        className={`operation-status operation-status--${operation.status.toLowerCase()}`}
                      >
                        {OPERATION_STATUS_LABELS[operation.status]}
                      </span>
                    </td>

                    <td>{formatDate(operation.effectiveFrom)}</td>
                    <td>{formatDate(operation.effectiveTo)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}

export default OperationPage;