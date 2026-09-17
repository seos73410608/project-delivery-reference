import type {
  RiskImpact,
  RiskPriority,
  RiskProbability,
  RiskResponse,
  RiskStatus,
  RiskStatusUpdateRequest,
} from '../types/risk';


interface RiskDetailProps {

  risk:
    RiskResponse
    | null;


  onEdit: (
    risk: RiskResponse,
  ) => void;


  onDelete: (
    risk: RiskResponse,
  ) => void;


  onStatusChange: (
    riskId: number,
    request: RiskStatusUpdateRequest,
  ) => void | Promise<void>;

}


/**
 * =====================================================
 * Risk Status 표시명
 * =====================================================
 */
const getStatusLabel = (

  status: RiskStatus,

): string => {

  switch (status) {

    case 'OPEN':
      return '신규';

    case 'IN_PROGRESS':
      return '진행 중';

    case 'MITIGATED':
      return '완화';

    case 'CLOSED':
      return '종료';

    case 'ACCEPTED':
      return '수용';

    case 'CANCELLED':
      return '취소';

    default:
      return status;

  }

};


/**
 * =====================================================
 * Risk Status Badge Class
 * =====================================================
 */
const getStatusClassName = (

  status: RiskStatus,

): string => {

  switch (status) {

    case 'OPEN':
      return 'badge badge--secondary';

    case 'IN_PROGRESS':
      return 'badge badge--primary';

    case 'MITIGATED':
      return 'badge badge--success';

    case 'CLOSED':
      return 'badge badge--dark';

    case 'ACCEPTED':
      return 'badge badge--warning';

    case 'CANCELLED':
      return 'badge badge--danger';

    default:
      return 'badge';

  }

};


/**
 * =====================================================
 * Risk Priority 표시명
 * =====================================================
 */
const getPriorityLabel = (

  priority: RiskPriority,

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
 * Risk Priority Badge Class
 * =====================================================
 */
const getPriorityClassName = (

  priority: RiskPriority,

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
 * Risk Probability 표시명
 * =====================================================
 */
const getProbabilityLabel = (

  probability: RiskProbability,

): string => {

  switch (probability) {

    case 'LOW':
      return '낮음';

    case 'MEDIUM':
      return '보통';

    case 'HIGH':
      return '높음';

    default:
      return probability;

  }

};


/**
 * =====================================================
 * Risk Probability Badge Class
 * =====================================================
 */
const getProbabilityClassName = (

  probability: RiskProbability,

): string => {

  switch (probability) {

    case 'LOW':
      return 'badge badge--secondary';

    case 'MEDIUM':
      return 'badge badge--primary';

    case 'HIGH':
      return 'badge badge--warning';

    default:
      return 'badge';

  }

};


/**
 * =====================================================
 * Risk Impact 표시명
 * =====================================================
 */
const getImpactLabel = (

  impact: RiskImpact,

): string => {

  switch (impact) {

    case 'LOW':
      return '낮음';

    case 'MEDIUM':
      return '보통';

    case 'HIGH':
      return '높음';

    case 'CRITICAL':
      return '긴급';

    default:
      return impact;

  }

};


/**
 * =====================================================
 * Risk Impact Badge Class
 * =====================================================
 */
const getImpactClassName = (

  impact: RiskImpact,

): string => {

  switch (impact) {

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
 * Risk 상세 정보
 * =====================================================
 */
const RiskDetail = ({

  risk,

  onEdit,
  onDelete,
  onStatusChange,

}: RiskDetailProps) => {


  /**
   * ===================================================
   * Empty State
   * ===================================================
   */
  if (!risk) {

    return (

      <section
        className="risk-detail"
      >

        <div
          className="
            risk-detail__empty
          "
        >

          <p>
            목록에서 Risk를 선택하면
            상세 정보를 확인할 수 있습니다.
          </p>

        </div>

      </section>

    );

  }


  /**
   * ===================================================
   * Status Change
   * ===================================================
   */
  const handleStatusChange = (

    event:
      React.ChangeEvent<
        HTMLSelectElement
      >,

  ) => {

    const nextStatus =
      event.target.value as RiskStatus;


    /**
     * -----------------------------------------------
     * 현재 Status와 동일한 경우
     * -----------------------------------------------
     */
    if (
      nextStatus === risk.status
    ) {

      return;

    }


    /**
     * -----------------------------------------------
     * Status 변경 확인
     * -----------------------------------------------
     */
    const confirmed =
      window.confirm(
        `"${risk.title}" Risk의 상태를 "${getStatusLabel(
          nextStatus,
        )}"(으)로 변경하시겠습니까?`,
      );


    if (!confirmed) {

      return;

    }


    const request:
      RiskStatusUpdateRequest = {

      status:
        nextStatus,

    };


    void onStatusChange(
      risk.id,
      request,
    );

  };


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <section
      className="risk-detail"
    >


      {/* ================================================
          Header
          ================================================ */}

      <div
        className="risk-detail__header"
      >

        <div
          className="
            risk-detail__header-content
          "
        >

          <div
            className="
              risk-detail__eyebrow
            "
          >
            {risk.riskKey ?? '-'}
          </div>


          <h2
            className="
              risk-detail__title
            "
          >
            Risk 상세
          </h2>


          <p
            className="
              risk-detail__subtitle
            "
          >
            선택한 Risk의 상세 정보를
            확인합니다.
          </p>

        </div>


        <div
          className="
            risk-detail__badges
          "
        >

          <span
            className={
              getPriorityClassName(
                risk.priority,
              )
            }
          >
            {getPriorityLabel(
              risk.priority,
            )}
          </span>


          <span
            className={
              getStatusClassName(
                risk.status,
              )
            }
          >
            {getStatusLabel(
              risk.status,
            )}
          </span>

        </div>

      </div>


      {/* ================================================
          Body
          ================================================ */}

      <div
        className="risk-detail__body"
      >

        <dl
          className="risk-detail__grid"
        >


          {/* ---------------------------------------------
              Risk ID
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              Risk ID
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.id}
            </dd>

          </div>


          {/* ---------------------------------------------
              Risk Key
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              Risk Key
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.riskKey ?? '-'}
            </dd>

          </div>


          {/* ---------------------------------------------
              Project ID
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              Project ID
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.projectId}
            </dd>

          </div>


          {/* ---------------------------------------------
              Risk 제목
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              Risk 제목
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.title}
            </dd>

          </div>


          {/* ---------------------------------------------
              상태
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              상태
            </dt>


            <dd
              className="
                risk-detail__value
                risk-detail__status-control
              "
            >

              <select
                className="
                  risk-detail__status-select
                "
                value={
                  risk.status
                }
                onChange={
                  handleStatusChange
                }
              >

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


              <span
                className={
                  getStatusClassName(
                    risk.status,
                  )
                }
              >
                {getStatusLabel(
                  risk.status,
                )}
              </span>

            </dd>

          </div>


          {/* ---------------------------------------------
              중요도
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              중요도
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >

              <span
                className={
                  getPriorityClassName(
                    risk.priority,
                  )
                }
              >
                {getPriorityLabel(
                  risk.priority,
                )}
              </span>

            </dd>

          </div>


          {/* ---------------------------------------------
              발생 가능성
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              발생 가능성
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >

              <span
                className={
                  getProbabilityClassName(
                    risk.probability,
                  )
                }
              >
                {getProbabilityLabel(
                  risk.probability,
                )}
              </span>

            </dd>

          </div>


          {/* ---------------------------------------------
              영향도
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              영향도
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >

              <span
                className={
                  getImpactClassName(
                    risk.impact,
                  )
                }
              >
                {getImpactLabel(
                  risk.impact,
                )}
              </span>

            </dd>

          </div>


          {/* ---------------------------------------------
              담당자 ID
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              담당자 ID
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.assigneeId ?? '-'}
            </dd>

          </div>


          {/* ---------------------------------------------
              등록자 ID
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              등록자 ID
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.reporterId ?? '-'}
            </dd>

          </div>


          {/* ---------------------------------------------
              식별일
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              식별일
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.identifiedDate ?? '-'}
            </dd>

          </div>


          {/* ---------------------------------------------
              대응 목표일
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              대응 목표일
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.dueDate ?? '-'}
            </dd>

          </div>


          {/* ---------------------------------------------
              완화일
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              완화일
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.mitigatedDate ?? '-'}
            </dd>

          </div>


          {/* ---------------------------------------------
              정렬 순서
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              정렬 순서
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.sortOrder ?? '-'}
            </dd>

          </div>


          {/* ---------------------------------------------
              생성일시
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              생성일시
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.createdAt}
            </dd>

          </div>


          {/* ---------------------------------------------
              수정일시
              --------------------------------------------- */}

          <div
            className="risk-detail__item"
          >

            <dt
              className="
                risk-detail__label
              "
            >
              수정일시
            </dt>


            <dd
              className="
                risk-detail__value
              "
            >
              {risk.updatedAt}
            </dd>

          </div>


        </dl>


        {/* ================================================
            Response Plan
            ================================================ */}

        <div
          className="
            risk-detail__response-plan
          "
        >

          <span
            className="
              risk-detail__response-plan-label
            "
          >
            대응 계획
          </span>


          <div
            className="
              risk-detail__response-plan-content
            "
          >
            {risk.responsePlan || '-'}
          </div>

        </div>


        {/* ================================================
            Description
            ================================================ */}

        <div
          className="
            risk-detail__description
          "
        >

          <span
            className="
              risk-detail__description-label
            "
          >
            Risk 내용
          </span>


          <div
            className="
              risk-detail__description-content
            "
          >
            {risk.description || '-'}
          </div>

        </div>


      </div>


      {/* ================================================
          Actions
          ================================================ */}

      <div
        className="
          risk-detail__actions
        "
      >

        <button
          type="button"
          className="
            button
            button--primary
          "
          onClick={() =>
            onEdit(risk)
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
            onDelete(risk)
          }
        >
          삭제
        </button>

      </div>


    </section>

  );

};


export default RiskDetail;