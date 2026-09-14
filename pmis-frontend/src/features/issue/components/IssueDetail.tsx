import type {
  IssuePriority,
  IssueResponse,
  IssueStatus,
} from '../types/issue';


interface IssueDetailProps {

  issue:
    IssueResponse
    | null;


  onEdit: (
    issue: IssueResponse,
  ) => void;


  onDelete: (
    issue: IssueResponse,
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
 * Issue 상세 정보
 * =====================================================
 */
const IssueDetail = ({

  issue,

  onEdit,
  onDelete,

}: IssueDetailProps) => {


  /**
   * ===================================================
   * Empty State
   * ===================================================
   */
  if (!issue) {

    return (

      <section
        className="issue-detail"
      >

        <div
          className="
            issue-detail__empty
          "
        >

          <p>
            목록에서 Issue를 선택하면
            상세 정보를 확인할 수 있습니다.
          </p>

        </div>

      </section>

    );

  }


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <section
      className="issue-detail"
    >


      {/* ================================================
          Header
          ================================================ */}

      <div
        className="issue-detail__header"
      >

        <div
          className="
            issue-detail__header-content
          "
        >

          <div
            className="
              issue-detail__eyebrow
            "
          >
            {issue.issueKey}
          </div>


          <h2
            className="
              issue-detail__title
            "
          >
            Issue 상세
          </h2>


          <p
            className="
              issue-detail__subtitle
            "
          >
            선택한 Issue의 상세 정보를
            확인합니다.
          </p>

        </div>


        <div
          className="
            issue-detail__badges
          "
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

        </div>

      </div>


      {/* ================================================
          Body
          ================================================ */}

      <div
        className="issue-detail__body"
      >

        <dl
          className="issue-detail__grid"
        >


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              Issue ID
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.id}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              Issue Key
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.issueKey}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              Project ID
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.projectId}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              Issue 제목
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.title}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              상태
            </dt>

            <dd
              className="
                issue-detail__value
              "
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
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              중요도
            </dt>

            <dd
              className="
                issue-detail__value
              "
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
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              담당자 ID
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.assigneeId ?? '-'}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              등록자 ID
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.reporterId ?? '-'}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              발생일
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.occurredDate ?? '-'}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              조치 목표일
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.dueDate ?? '-'}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              해결일
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.resolvedDate ?? '-'}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              정렬 순서
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.sortOrder ?? '-'}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              생성일시
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.createdAt}
            </dd>
          </div>


          <div
            className="issue-detail__item"
          >
            <dt
              className="
                issue-detail__label
              "
            >
              수정일시
            </dt>

            <dd
              className="
                issue-detail__value
              "
            >
              {issue.updatedAt}
            </dd>
          </div>


        </dl>


        {/* ================================================
            Description
            ================================================ */}

        <div
          className="
            issue-detail__description
          "
        >

          <span
            className="
              issue-detail__description-label
            "
          >
            Issue 내용
          </span>


          <div
            className="
              issue-detail__description-content
            "
          >
            {issue.description || '-'}
          </div>

        </div>


      </div>


      {/* ================================================
          Actions
          ================================================ */}

      <div
        className="
          issue-detail__actions
        "
      >

        <button
          type="button"
          className="
            button
            button--primary
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
          "
          onClick={() =>
            onDelete(issue)
          }
        >
          삭제
        </button>

      </div>


    </section>

  );

};


export default IssueDetail;