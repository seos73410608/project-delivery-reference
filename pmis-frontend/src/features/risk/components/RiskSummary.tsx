import type {
  RiskResponse,
  RiskStatus,
} from '../types/risk';


interface RiskSummaryProps {

  risks:
    RiskResponse[];

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
 * Risk Summary
 * =====================================================
 */
const RiskSummary = ({

  risks,

}: RiskSummaryProps) => {


  /**
   * ===================================================
   * Summary Calculation
   * ===================================================
   */

  const totalCount =
    risks.length;


  const openCount =
    risks.filter(
      (risk) =>
        risk.status === 'OPEN',
    ).length;


  const inProgressCount =
    risks.filter(
      (risk) =>
        risk.status === 'IN_PROGRESS',
    ).length;


  const mitigatedCount =
    risks.filter(
      (risk) =>
        risk.status === 'MITIGATED',
    ).length;


  const closedCount =
    risks.filter(
      (risk) =>
        risk.status === 'CLOSED',
    ).length;


  const acceptedCount =
    risks.filter(
      (risk) =>
        risk.status === 'ACCEPTED',
    ).length;


  const cancelledCount =
    risks.filter(
      (risk) =>
        risk.status === 'CANCELLED',
    ).length;


  const highRiskCount =
    risks.filter(

      (risk) =>

        risk.priority === 'HIGH' ||

        risk.priority === 'CRITICAL',

    ).length;


  /**
   * ===================================================
   * Overdue Calculation
   *
   * dueDate < today
   * AND
   * status != MITIGATED
   * AND
   * status != CLOSED
   * AND
   * status != CANCELLED
   *
   * ===================================================
   */
  const today =
    new Date()
      .toISOString()
      .slice(
        0,
        10,
      );


  const overdueCount =
    risks.filter(

      (risk) => {

        if (
          !risk.dueDate
        ) {

          return false;

        }


        if (
          risk.dueDate >= today
        ) {

          return false;

        }


        return (

          risk.status !== 'MITIGATED' &&

          risk.status !== 'CLOSED' &&

          risk.status !== 'CANCELLED'

        );

      },

    ).length;


  /**
   * ===================================================
   * Active Risk
   *
   * Risk Management 관점에서
   * 아직 종료되지 않은 Risk
   * ===================================================
   */
  const activeCount =
    risks.filter(

      (risk) =>

        risk.status !== 'MITIGATED' &&

        risk.status !== 'CLOSED' &&

        risk.status !== 'CANCELLED',

    ).length;


  /**
   * ===================================================
   * Empty State
   * ===================================================
   */
  if (
    totalCount === 0
  ) {

    return (

      <section
        className="
          risk-summary
        "
      >

        <div
          className="
            risk-summary__header
          "
        >

          <div>

            <h2
              className="
                risk-summary__title
              "
            >
              Risk Summary
            </h2>


            <p
              className="
                risk-summary__description
              "
            >
              프로젝트 Risk 현황을 요약합니다.
            </p>

          </div>

        </div>


        <div
          className="
            risk-summary__empty
          "
        >

          <p>
            등록된 Risk가 없습니다.
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
      className="
        risk-summary
      "
    >


      {/* ================================================
          Header
          ================================================ */}

      <div
        className="
          risk-summary__header
        "
      >

        <div>

          <h2
            className="
              risk-summary__title
            "
          >
            Risk Summary
          </h2>


          <p
            className="
              risk-summary__description
            "
          >
            프로젝트 Risk 현황을 요약합니다.
          </p>

        </div>


        <div
          className="
            risk-summary__total
          "
        >

          <span
            className="
              risk-summary__total-label
            "
          >
            Total
          </span>


          <strong
            className="
              risk-summary__total-value
            "
          >
            {totalCount}
          </strong>


          <span
            className="
              risk-summary__total-unit
            "
          >
            risks
          </span>

        </div>

      </div>


      {/* ================================================
          Summary Cards
          ================================================ */}

      <div
        className="
          risk-summary__grid
        "
      >


        {/* ---------------------------------------------
            Total
            --------------------------------------------- */}

        <div
          className="
            risk-summary__card
            risk-summary__card--total
          "
        >

          <span
            className="
              risk-summary__card-label
            "
          >
            전체 Risk
          </span>


          <strong
            className="
              risk-summary__card-value
            "
          >
            {totalCount}
          </strong>


          <span
            className="
              risk-summary__card-description
            "
          >
            등록된 전체 Risk
          </span>

        </div>


        {/* ---------------------------------------------
            Active
            --------------------------------------------- */}

        <div
          className="
            risk-summary__card
            risk-summary__card--active
          "
        >

          <span
            className="
              risk-summary__card-label
            "
          >
            진행 중 Risk
          </span>


          <strong
            className="
              risk-summary__card-value
            "
          >
            {activeCount}
          </strong>


          <span
            className="
              risk-summary__card-description
            "
          >
            아직 종료되지 않은 Risk
          </span>

        </div>


        {/* ---------------------------------------------
            High Risk
            --------------------------------------------- */}

        <div
          className="
            risk-summary__card
            risk-summary__card--high
          "
        >

          <span
            className="
              risk-summary__card-label
            "
          >
            High / Critical
          </span>


          <strong
            className="
              risk-summary__card-value
            "
          >
            {highRiskCount}
          </strong>


          <span
            className="
              risk-summary__card-description
            "
          >
            중요도가 높은 Risk
          </span>

        </div>


        {/* ---------------------------------------------
            Overdue
            --------------------------------------------- */}

        <div
          className="
            risk-summary__card
            risk-summary__card--overdue
          "
        >

          <span
            className="
              risk-summary__card-label
            "
          >
            Overdue
          </span>


          <strong
            className="
              risk-summary__card-value
            "
          >
            {overdueCount}
          </strong>


          <span
            className="
              risk-summary__card-description
            "
          >
            대응 목표일 초과
          </span>

        </div>

      </div>


      {/* ================================================
          Status Summary
          ================================================ */}

      <div
        className="
          risk-summary__status
        "
      >

        <div
          className="
            risk-summary__status-header
          "
        >

          <h3
            className="
              risk-summary__status-title
            "
          >
            Status Distribution
          </h3>

        </div>


        <div
          className="
            risk-summary__status-list
          "
        >


          {/* -------------------------------------------
              OPEN
              ------------------------------------------- */}

          <div
            className="
              risk-summary__status-item
            "
          >

            <span
              className="
                risk-summary__status-label
              "
            >
              {getStatusLabel('OPEN')}
            </span>


            <strong
              className="
                risk-summary__status-value
              "
            >
              {openCount}
            </strong>

          </div>


          {/* -------------------------------------------
              IN_PROGRESS
              ------------------------------------------- */}

          <div
            className="
              risk-summary__status-item
            "
          >

            <span
              className="
                risk-summary__status-label
              "
            >
              {getStatusLabel('IN_PROGRESS')}
            </span>


            <strong
              className="
                risk-summary__status-value
              "
            >
              {inProgressCount}
            </strong>

          </div>


          {/* -------------------------------------------
              MITIGATED
              ------------------------------------------- */}

          <div
            className="
              risk-summary__status-item
            "
          >

            <span
              className="
                risk-summary__status-label
              "
            >
              {getStatusLabel('MITIGATED')}
            </span>


            <strong
              className="
                risk-summary__status-value
              "
            >
              {mitigatedCount}
            </strong>

          </div>


          {/* -------------------------------------------
              CLOSED
              ------------------------------------------- */}

          <div
            className="
              risk-summary__status-item
            "
          >

            <span
              className="
                risk-summary__status-label
              "
            >
              {getStatusLabel('CLOSED')}
            </span>


            <strong
              className="
                risk-summary__status-value
              "
            >
              {closedCount}
            </strong>

          </div>


          {/* -------------------------------------------
              ACCEPTED
              ------------------------------------------- */}

          <div
            className="
              risk-summary__status-item
            "
          >

            <span
              className="
                risk-summary__status-label
              "
            >
              {getStatusLabel('ACCEPTED')}
            </span>


            <strong
              className="
                risk-summary__status-value
              "
            >
              {acceptedCount}
            </strong>

          </div>


          {/* -------------------------------------------
              CANCELLED
              ------------------------------------------- */}

          <div
            className="
              risk-summary__status-item
            "
          >

            <span
              className="
                risk-summary__status-label
              "
            >
              {getStatusLabel('CANCELLED')}
            </span>


            <strong
              className="
                risk-summary__status-value
              "
            >
              {cancelledCount}
            </strong>

          </div>


        </div>

      </div>


    </section>

  );

};


export default RiskSummary;