import type {
  RiskImpact,
  RiskPriority,
  RiskProbability,
  RiskStatus,
} from '../types/risk';


interface RiskToolbarProps {

  keyword: string;

  status: RiskStatus | '';

  priority: RiskPriority | '';

  probability: RiskProbability | '';

  impact: RiskImpact | '';


  onKeywordChange: (
    keyword: string,
  ) => void;


  onStatusChange: (
    status: RiskStatus | '',
  ) => void;


  onPriorityChange: (
    priority: RiskPriority | '',
  ) => void;


  onProbabilityChange: (
    probability: RiskProbability | '',
  ) => void;


  onImpactChange: (
    impact: RiskImpact | '',
  ) => void;


  onSearch: () => void;

  onCreate: () => void;

}


/**
 * =====================================================
 * Risk Toolbar
 * =====================================================
 *
 * Risk 검색 및 필터 기능과
 * Risk 생성 기능을 제공한다.
 *
 * 주요 기능:
 *
 * - Risk 제목 / 내용 검색
 * - Risk Status 필터
 * - Risk Priority 필터
 * - Risk Probability 필터
 * - Risk Impact 필터
 * - Enter 검색
 * - Risk 검색 실행
 * - Risk 생성 화면 호출
 */
const RiskToolbar = ({

  keyword,
  status,
  priority,
  probability,
  impact,

  onKeywordChange,
  onStatusChange,
  onPriorityChange,
  onProbabilityChange,
  onImpactChange,

  onSearch,
  onCreate,

}: RiskToolbarProps) => {


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
      className="risk-toolbar"
    >


      {/* ================================================
          Search / Filter
          ================================================ */}

      <div
        className="
          risk-toolbar__filters
        "
      >


        {/* -----------------------------------------------
            Keyword
            ----------------------------------------------- */}

        <div
          className="
            form-field
            risk-toolbar__field
            risk-toolbar__field--keyword
          "
        >

          <label
            className="form-field__label"
            htmlFor="risk-keyword"
          >
            Risk 검색
          </label>


          <input
            id="risk-keyword"
            className="form-input"
            type="text"
            value={keyword}
            placeholder="Risk 제목 또는 내용 검색"
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
            risk-toolbar__field
            risk-toolbar__field--status
          "
        >

          <label
            className="form-field__label"
            htmlFor="risk-status-filter"
          >
            상태
          </label>


          <select
            id="risk-status-filter"
            className="form-select"
            value={status}
            onChange={(event) =>
              onStatusChange(
                event.target.value as
                  | RiskStatus
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


            <option value="MITIGATED">
              완화
            </option>


            <option value="CLOSED">
              종료
            </option>


            <option value="ACCEPTED">
              수용
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
            risk-toolbar__field
            risk-toolbar__field--priority
          "
        >

          <label
            className="form-field__label"
            htmlFor="risk-priority-filter"
          >
            중요도
          </label>


          <select
            id="risk-priority-filter"
            className="form-select"
            value={priority}
            onChange={(event) =>
              onPriorityChange(
                event.target.value as
                  | RiskPriority
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
            Probability
            ----------------------------------------------- */}

        <div
          className="
            form-field
            risk-toolbar__field
            risk-toolbar__field--probability
          "
        >

          <label
            className="form-field__label"
            htmlFor="risk-probability-filter"
          >
            발생 가능성
          </label>


          <select
            id="risk-probability-filter"
            className="form-select"
            value={probability}
            onChange={(event) =>
              onProbabilityChange(
                event.target.value as
                  | RiskProbability
                  | '',
              )
            }
          >

            <option value="">
              전체 가능성
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

          </select>

        </div>


        {/* -----------------------------------------------
            Impact
            ----------------------------------------------- */}

        <div
          className="
            form-field
            risk-toolbar__field
            risk-toolbar__field--impact
          "
        >

          <label
            className="form-field__label"
            htmlFor="risk-impact-filter"
          >
            영향도
          </label>


          <select
            id="risk-impact-filter"
            className="form-select"
            value={impact}
            onChange={(event) =>
              onImpactChange(
                event.target.value as
                  | RiskImpact
                  | '',
              )
            }
          >

            <option value="">
              전체 영향도
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
          className="
            risk-toolbar__search
          "
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
        className="
          risk-toolbar__actions
        "
      >

        <button
          type="button"
          className="
            button
            button--primary
          "
          onClick={onCreate}
        >
          + Risk 생성
        </button>

      </div>


    </div>

  );

};


export default RiskToolbar;