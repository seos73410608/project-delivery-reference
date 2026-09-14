import type {
  IssuePriority,
  IssueResponse,
  IssueStatus,
} from '../types/issue';


interface IssueRowProps {

  issue: IssueResponse;


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
 * Issue Status 표시명
 * =====================================================
 */
const getStatusLabel = (

  status: IssueStatus,

): string => {

  switch (status) {

    case 'OPEN':
      return '신규';


    case 'IN_PROGRESS':
      return '진행 중';


    case 'RESOLVED':
      return '해결';


    case 'CLOSED':
      return '종료';


    case 'ON_HOLD':
      return '보류';


    case 'CANCELLED':
      return '취소';


    default:
      return status;

  }

};


/**
 * =====================================================
 * Issue Status Badge Class
 * =====================================================
 */
const getStatusClassName = (

  status: IssueStatus,

): string => {

  switch (status) {

    case 'OPEN':
      return 'badge badge--secondary';


    case 'IN_PROGRESS':
      return 'badge badge--primary';


    case 'RESOLVED':
      return 'badge badge--success';


    case 'CLOSED':
      return 'badge badge--dark';


    case 'ON_HOLD':
      return 'badge badge--warning';


    case 'CANCELLED':
      return 'badge badge--danger';


    default:
      return 'badge';

  }

};


/**
 * =====================================================
 * Issue Priority 표시명
 * =====================================================
 */
const getPriorityLabel = (

  priority: IssuePriority,

): string => {

  switch (priority) {

    case 'LOW':
      return '낮음';


    case 'MEDIUM':
      return '보통';


    case 'HIGH':
      return '높음';


    case 'CRITICAL':
      return '긴급';


    default:
      return priority;

  }

};


/**
 * =====================================================
 * Issue Priority Badge Class
 * =====================================================
 */
const getPriorityClassName = (

  priority: IssuePriority,

): string => {

  switch (priority) {

    case 'LOW':
      return 'badge badge--secondary';


    case 'MEDIUM':
      return 'badge badge--primary';


    case 'HIGH':
      return 'badge badge--warning';


    case 'CRITICAL':
      return 'badge badge--danger';


    default:
      return 'badge';

  }

};


/**
 * =====================================================
 * Issue Row
 * =====================================================
 *
 * Issue 목록의 개별 Row를 표시한다.
 *
 * 컬럼 순서:
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
const IssueRow = ({

  issue,

  onSelect,
  onEdit,
  onDelete,

}: IssueRowProps) => {


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <tr
      className="issue-row"
    >


      {/* ================================================
          Issue Key
          ================================================ */}

      <td
        className="issue-row__key"
      >
        {issue.issueKey}
      </td>


      {/* ================================================
          Title
          ================================================ */}

      <td
        className="issue-row__title"
      >

        <button
          type="button"
          className="
            issue-row__title-button
          "
          onClick={() =>
            onSelect(issue)
          }
        >

          {issue.title}

        </button>

      </td>


      {/* ================================================
          Status
          ================================================ */}

      <td
        className="issue-row__status"
      >

        <span
          className={
            getStatusClassName(
              issue.status,
            )
          }
        >

          {getStatusLabel(
            issue.status,
          )}

        </span>

      </td>


      {/* ================================================
          Priority
          ================================================ */}

      <td
        className="issue-row__priority"
      >

        <span
          className={
            getPriorityClassName(
              issue.priority,
            )
          }
        >

          {getPriorityLabel(
            issue.priority,
          )}

        </span>

      </td>


      {/* ================================================
          Assignee
          ================================================ */}

      <td
        className="issue-row__assignee"
      >

        {issue.assigneeId
          ? `User #${issue.assigneeId}`
          : '-'
        }

      </td>


      {/* ================================================
          Occurred Date
          ================================================ */}

      <td
        className="issue-row__date"
      >
        {issue.occurredDate ?? '-'}
      </td>


      {/* ================================================
          Due Date
          ================================================ */}

      <td
        className="issue-row__date"
      >
        {issue.dueDate ?? '-'}
      </td>


      {/* ================================================
          Actions
          ================================================ */}

      <td
        className="issue-row__actions"
      >

        <div
          className="
            issue-row__action-group
          "
        >


          <button
            type="button"
            className="
              button
              button--secondary
              button--sm
            "
            onClick={() =>
              onSelect(issue)
            }
          >
            상세
          </button>


          <button
            type="button"
            className="
              button
              button--primary
              button--sm
            "
            onClick={() =>
              onEdit(issue)
            }
          >
            수정
          </button>


          <button
            type="button"
            className="
              button
              button--danger
              button--sm
            "
            onClick={() =>
              onDelete(
                issue.id,
              )
            }
          >
            삭제
          </button>


        </div>

      </td>


    </tr>

  );

};


export default IssueRow;