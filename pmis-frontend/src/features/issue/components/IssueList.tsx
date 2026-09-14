import type {
  IssueResponse,
} from '../types/issue';

import IssueRow from './IssueRow';


interface IssueListProps {

  issues: IssueResponse[];


  onSelect: (
    issue: IssueResponse,
  ) => void;


  onEdit: (
    issue: IssueResponse,
  ) => void;


  onDelete: (
    issueId: number,
  ) => void;

}


/**
 * =====================================================
 * Issue 목록
 * =====================================================
 *
 * Issue 데이터를 Table 형태로 표시한다.
 *
 * 주요 표시 정보:
 *
 * - Issue Key
 * - 제목
 * - 상태
 * - 중요도
 * - 담당자
 * - 발생일
 * - 조치 목표일
 * - 관리
 */
const IssueList = ({

  issues,

  onSelect,
  onEdit,
  onDelete,

}: IssueListProps) => {


  /**
   * ===================================================
   * Empty State
   * ===================================================
   */
  if (
    issues.length === 0
  ) {

    return (

      <div
        className="card issue-list"
      >

        <div
          className="issue-list__empty"
        >

          <p>
            등록된 Issue가 없습니다.
          </p>


          <span>
            검색 조건을 변경하거나 새로운
            Issue를 등록해주세요.
          </span>

        </div>

      </div>

    );

  }


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <section
      className="card issue-list"
    >


      {/* ================================================
          Card Header
          ================================================ */}

      <div
        className="
          card__header
          issue-list__header
        "
      >

        <div
          className="
            card__header-content
          "
        >

          <h2
            className="card__title"
          >
            Issue 목록
          </h2>


          <p
            className="
              card__description
            "
          >
            프로젝트 수행 중 발생한
            Issue를 확인하고 관리합니다.
          </p>

        </div>


        <div
          className="card__meta"
        >

          총{' '}

          <strong>
            {issues.length}
          </strong>

          건

        </div>

      </div>


      {/* ================================================
          Table
          ================================================ */}

      <div
        className="
          issue-list__table-wrapper
        "
      >

        <table
          className="issue-list__table"
        >

          <thead>

            <tr>

              <th>
                Issue Key
              </th>


              <th>
                제목
              </th>


              <th>
                상태
              </th>


              <th>
                중요도
              </th>


              <th>
                담당자
              </th>


              <th>
                발생일
              </th>


              <th>
                조치 목표일
              </th>


              <th>
                관리
              </th>

            </tr>

          </thead>


          <tbody>

            {issues.map(
              (issue) => (

                <IssueRow

                  key={issue.id}

                  issue={issue}

                  onSelect={onSelect}

                  onEdit={onEdit}

                  onDelete={onDelete}

                />

              ),
            )}

          </tbody>

        </table>

      </div>


    </section>

  );

};


export default IssueList;