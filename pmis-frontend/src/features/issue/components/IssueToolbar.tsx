import type {
  IssuePriority,
  IssueStatus,
} from '../types/issue';


interface IssueToolbarProps {

  keyword: string;

  status: IssueStatus | '';

  priority: IssuePriority | '';


  onKeywordChange: (
    keyword: string,
  ) => void;


  onStatusChange: (
    status: IssueStatus | '',
  ) => void;


  onPriorityChange: (
    priority: IssuePriority | '',
  ) => void;


  onSearch: () => void;

  onCreate: () => void;

}


/**
 * =====================================================
 * Issue Toolbar
 * =====================================================
 *
 * Issue 검색 및 필터 기능과
 * Issue 생성 기능을 제공한다.
 *
 * 주요 기능:
 *
 * - Issue 제목 / 내용 검색
 * - Issue Status 필터
 * - Issue Priority 필터
 * - Enter 검색
 * - Issue 검색 실행
 * - Issue 생성 화면 호출
 */
const IssueToolbar = ({

  keyword,
  status,
  priority,

  onKeywordChange,
  onStatusChange,
  onPriorityChange,

  onSearch,
  onCreate,

}: IssueToolbarProps) => {


  /**
   * ===================================================
   * 검색어 Enter 검색
   * ===================================================
   */
  const handleKeyDown = (

    event:
      React.KeyboardEvent<
        HTMLInputElement
      >,

  ) => {

    if (
      event.key === 'Enter'
    ) {

      onSearch();

    }

  };


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <div
      className="issue-toolbar"
    >


      {/* ================================================
          Search / Filter
          ================================================ */}

      <div
        className="issue-toolbar__filters"
      >


        {/* -----------------------------------------------
            Keyword
            ----------------------------------------------- */}

        <div
          className="
            form-field
            issue-toolbar__field
            issue-toolbar__field--keyword
          "
        >

          <label
            className="form-field__label"
            htmlFor="issue-keyword"
          >
            Issue 검색
          </label>


          <input
            id="issue-keyword"
            className="form-input"
            type="text"
            value={keyword}
            placeholder="Issue 제목 또는 내용 검색"
            onChange={(event) =>
              onKeywordChange(
                event.target.value,
              )
            }
            onKeyDown={handleKeyDown}
          />

        </div>


        {/* -----------------------------------------------
            Status
            ----------------------------------------------- */}

        <div
          className="
            form-field
            issue-toolbar__field
            issue-toolbar__field--status
          "
        >

          <label
            className="form-field__label"
            htmlFor="issue-status-filter"
          >
            상태
          </label>


          <select
            id="issue-status-filter"
            className="form-select"
            value={status}
            onChange={(event) =>
              onStatusChange(
                event.target.value as
                  | IssueStatus
                  | '',
              )
            }
          >

            <option value="">
              전체 상태
            </option>


            <option value="OPEN">
              신규
            </option>


            <option value="IN_PROGRESS">
              진행 중
            </option>


            <option value="RESOLVED">
              해결
            </option>


            <option value="CLOSED">
              종료
            </option>


            <option value="ON_HOLD">
              보류
            </option>


            <option value="CANCELLED">
              취소
            </option>

          </select>

        </div>


        {/* -----------------------------------------------
            Priority
            ----------------------------------------------- */}

        <div
          className="
            form-field
            issue-toolbar__field
            issue-toolbar__field--priority
          "
        >

          <label
            className="form-field__label"
            htmlFor="issue-priority-filter"
          >
            중요도
          </label>


          <select
            id="issue-priority-filter"
            className="form-select"
            value={priority}
            onChange={(event) =>
              onPriorityChange(
                event.target.value as
                  | IssuePriority
                  | '',
              )
            }
          >

            <option value="">
              전체 중요도
            </option>


            <option value="LOW">
              낮음
            </option>


            <option value="MEDIUM">
              보통
            </option>


            <option value="HIGH">
              높음
            </option>


            <option value="CRITICAL">
              긴급
            </option>

          </select>

        </div>


        {/* -----------------------------------------------
            Search
            ----------------------------------------------- */}

        <div
          className="issue-toolbar__search"
        >

          <button
            type="button"
            className="
              button
              button--secondary
            "
            onClick={onSearch}
          >
            검색
          </button>

        </div>


      </div>


      {/* ================================================
          Actions
          ================================================ */}

      <div
        className="issue-toolbar__actions"
      >

        <button
          type="button"
          className="
            button
            button--primary
          "
          onClick={onCreate}
        >
          + Issue 생성
        </button>

      </div>


    </div>

  );

};


export default IssueToolbar;