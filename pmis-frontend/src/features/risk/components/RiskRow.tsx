import type {
  RiskImpact,
  RiskPriority,
  RiskProbability,
  RiskResponse,
  RiskStatus,
} from '../types/risk';


interface RiskRowProps {

  risk: RiskResponse;


  onSelect: (
    risk: RiskResponse,
  ) => void;


  onEdit: (
    risk: RiskResponse,
  ) => void;


  onDelete: (
    riskId: number,
  ) => void;

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
 * Risk Row
 * =====================================================
 *
 * Risk 목록의 개별 Row를 표시한다.
 *
 * 컬럼 순서:
 *
 * - Risk Key
 * - 제목
 * - Priority
 * - Probability
 * - Impact
 * - 담당자
 * - Status
 * - 관리
 *
 * Risk Key는 Backend에서 자동 생성되며
 * Frontend에서는 수정하지 않는다.
 */
const RiskRow = ({

  risk,

  onSelect,
  onEdit,
  onDelete,

}: RiskRowProps) => {


  /**
   * ===================================================
   * Render
   * ===================================================
   */
  return (

    <tr
      className="risk-row"
    >


      {/* ================================================
          Risk Key
          ================================================ */}

      <td
        className="risk-row__key"
      >
        {risk.riskKey ?? '-'}
      </td>


      {/* ================================================
          Title
          ================================================ */}

      <td
        className="risk-row__title"
      >

        <button
          type="button"
          className="
            risk-row__title-button
          "
          onClick={() =>
            onSelect(risk)
          }
        >

          {risk.title}

        </button>

      </td>


      {/* ================================================
          Priority
          ================================================ */}

      <td
        className="risk-row__priority"
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

      </td>


      {/* ================================================
          Probability
          ================================================ */}

      <td
        className="risk-row__probability"
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

      </td>


      {/* ================================================
          Impact
          ================================================ */}

      <td
        className="risk-row__impact"
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

      </td>


      {/* ================================================
          Assignee
          ================================================ */}

      <td
        className="risk-row__assignee"
      >

        {risk.assigneeId
          ? `User #${risk.assigneeId}`
          : '-'
        }

      </td>


      {/* ================================================
          Status
          ================================================ */}

      <td
        className="risk-row__status"
      >

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

      </td>


      {/* ================================================
          Actions
          ================================================ */}

      <td
        className="risk-row__actions"
      >

        <div
          className="
            risk-row__action-group
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
              onSelect(risk)
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
              button--sm
            "
            onClick={() =>
              onDelete(
                risk.id,
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


export default RiskRow;