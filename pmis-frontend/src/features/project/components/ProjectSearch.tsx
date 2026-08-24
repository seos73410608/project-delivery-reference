import { useState } from 'react';

import type {
  ProjectPriority,
  ProjectSearchParams,
  ProjectStatus,
} from '../types/project';

import './ProjectSearch.css';

interface ProjectSearchProps {
  onSearch: (params: ProjectSearchParams) => void;
  onReset: () => void;
}

const INITIAL_SEARCH_PARAMS: ProjectSearchParams = {
  projectCode: '',
  projectName: '',
  customerName: '',
  status: undefined,
  priority: undefined,
};

function ProjectSearch({
  onSearch,
  onReset,
}: ProjectSearchProps) {
  const [projectCode, setProjectCode] = useState('');
  const [projectName, setProjectName] = useState('');
  const [customerName, setCustomerName] = useState('');
  const [status, setStatus] = useState<ProjectStatus | ''>('');
  const [priority, setPriority] = useState<ProjectPriority | ''>('');

  const handleSearch = () => {
    const params: ProjectSearchParams = {
      ...INITIAL_SEARCH_PARAMS,
      projectCode: projectCode.trim() || undefined,
      projectName: projectName.trim() || undefined,
      customerName: customerName.trim() || undefined,
      status: status || undefined,
      priority: priority || undefined,
      page: 0,
      size: 10,
    };

    onSearch(params);
  };

  const handleReset = () => {
    setProjectCode('');
    setProjectName('');
    setCustomerName('');
    setStatus('');
    setPriority('');

    onReset();
  };

  const handleKeyDown = (
    event: React.KeyboardEvent<HTMLInputElement>,
  ) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <section className="project-search">
      <div className="project-search__row">
        <div className="project-search__field">
          <label htmlFor="projectCode">프로젝트 코드</label>
          <input
            id="projectCode"
            type="text"
            value={projectCode}
            onChange={(event) =>
              setProjectCode(event.target.value)
            }
            onKeyDown={handleKeyDown}
            placeholder="예: PMIS-2026-001"
          />
        </div>

        <div className="project-search__field">
          <label htmlFor="projectName">프로젝트명</label>
          <input
            id="projectName"
            type="text"
            value={projectName}
            onChange={(event) =>
              setProjectName(event.target.value)
            }
            onKeyDown={handleKeyDown}
            placeholder="프로젝트명을 입력하세요"
          />
        </div>

        <div className="project-search__field">
          <label htmlFor="customerName">고객사</label>
          <input
            id="customerName"
            type="text"
            value={customerName}
            onChange={(event) =>
              setCustomerName(event.target.value)
            }
            onKeyDown={handleKeyDown}
            placeholder="고객사를 입력하세요"
          />
        </div>
      </div>

      <div className="project-search__row">
        <div className="project-search__field">
          <label htmlFor="status">상태</label>
          <select
            id="status"
            value={status}
            onChange={(event) =>
              setStatus(
                event.target.value as ProjectStatus | '',
              )
            }
          >
            <option value="">전체</option>
            <option value="PLANNING">계획</option>
            <option value="IN_PROGRESS">진행중</option>
            <option value="ON_HOLD">보류</option>
            <option value="COMPLETED">완료</option>
            <option value="CANCELLED">취소</option>
          </select>
        </div>

        <div className="project-search__field">
          <label htmlFor="priority">우선순위</label>
          <select
            id="priority"
            value={priority}
            onChange={(event) =>
              setPriority(
                event.target.value as ProjectPriority | '',
              )
            }
          >
            <option value="">전체</option>
            <option value="LOW">낮음</option>
            <option value="MEDIUM">보통</option>
            <option value="HIGH">높음</option>
            <option value="CRITICAL">긴급</option>
          </select>
        </div>

        <div className="project-search__actions">
          <button
            type="button"
            onClick={handleSearch}
          >
            검색
          </button>

          <button
            type="button"
            onClick={handleReset}
          >
            초기화
          </button>
        </div>
      </div>
    </section>
  );
}

export default ProjectSearch;