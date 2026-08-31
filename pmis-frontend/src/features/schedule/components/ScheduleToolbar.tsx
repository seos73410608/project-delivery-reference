import type { ScheduleStatus } from '../types/schedule';

interface ScheduleToolbarProps {
  keyword: string;
  status: ScheduleStatus | '';
  wbsId: number | '';

  onKeywordChange: (keyword: string) => void;
  onStatusChange: (status: ScheduleStatus | '') => void;
  onWbsIdChange: (wbsId: number | '') => void;

  onSearch: () => void;
  onCreate: () => void;
}

/**
 * Schedule Toolbar
 *
 * Schedule 검색 및 필터 기능과
 * Schedule 생성 기능을 제공한다.
 *
 * 주요 기능:
 * - 일정명 검색
 * - WBS ID 필터
 * - Schedule Status 필터
 * - Schedule 검색 실행
 * - Schedule 생성 화면 호출
 */
const ScheduleToolbar = ({
  keyword,
  status,
  wbsId,
  onKeywordChange,
  onStatusChange,
  onWbsIdChange,
  onSearch,
  onCreate,
}: ScheduleToolbarProps) => {
  /**
   * WBS ID 변경
   *
   * 빈 값은 ''로 유지하고,
   * 입력값은 숫자로 변환하여 상위 컴포넌트에 전달한다.
   */
  const handleWbsIdChange = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const value = event.target.value;

    onWbsIdChange(
      value === '' ? '' : Number(value),
    );
  };

  /**
   * 검색어 / WBS ID Enter 검색
   */
  const handleKeyDown = (
    event: React.KeyboardEvent<HTMLInputElement>,
  ) => {
    if (event.key === 'Enter') {
      onSearch();
    }
  };

  return (
    <div className="schedule-toolbar">
      {/* 검색 / 필터 영역 */}
      <div className="schedule-toolbar__filters">
        {/* Schedule 이름 검색 */}
        <input
          type="text"
          value={keyword}
          placeholder="일정명 검색"
          onChange={(event) =>
            onKeywordChange(event.target.value)
          }
          onKeyDown={handleKeyDown}
        />

        {/* WBS ID 필터 */}
        <input
          type="number"
          value={wbsId}
          placeholder="WBS ID"
          min={1}
          onChange={handleWbsIdChange}
          onKeyDown={handleKeyDown}
        />

        {/* Schedule Status 필터 */}
        <select
          value={status}
          onChange={(event) =>
            onStatusChange(
              event.target.value as ScheduleStatus | '',
            )
          }
        >
          <option value="">전체 상태</option>
          <option value="PLANNED">계획</option>
          <option value="IN_PROGRESS">진행 중</option>
          <option value="COMPLETED">완료</option>
          <option value="ON_HOLD">보류</option>
          <option value="CANCELLED">취소</option>
        </select>

        {/* 검색 */}
        <button
          type="button"
          onClick={onSearch}
        >
          검색
        </button>
      </div>

      {/* 생성 영역 */}
      <div className="schedule-toolbar__actions">
        <button
          type="button"
          onClick={onCreate}
        >
          일정 생성
        </button>
      </div>
    </div>
  );
};

export default ScheduleToolbar;