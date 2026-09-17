import type {
  RiskImpact,
  RiskProbability,
  RiskResponse,
} from '../types/risk';

interface RiskMatrixProps {
  risks: RiskResponse[];
}

/**
 * Risk Matrix
 *
 * Probability × Impact
 *
 * Probability:
 * - LOW
 * - MEDIUM
 * - HIGH
 *
 * Impact:
 * - LOW
 * - MEDIUM
 * - HIGH
 * - CRITICAL
 *
 * Matrix Level:
 *
 *               Impact
 *             LOW  MED  HIGH  CRIT
 * Probability
 * LOW          L    L    M     H
 * MEDIUM       L    M    H     H
 * HIGH         M    H    H     C
 *
 * Risk Level은 DB에 저장하지 않고
 * Probability와 Impact를 기반으로 화면에서 계산한다.
 */
const RiskMatrix = ({ risks }: RiskMatrixProps) => {
  /**
   * Probability 순서
   *
   * 화면에서는 LOW → MEDIUM → HIGH 순서로 표시한다.
   */
  const probabilities: RiskProbability[] = [
    'LOW',
    'MEDIUM',
    'HIGH',
  ];

  /**
   * Impact 순서
   *
   * 화면에서는 LOW → MEDIUM → HIGH → CRITICAL 순서로 표시한다.
   */
  const impacts: RiskImpact[] = [
    'LOW',
    'MEDIUM',
    'HIGH',
    'CRITICAL',
  ];

  /**
   * Probability Label
   */
  const getProbabilityLabel = (
    probability: RiskProbability,
  ): string => {
    switch (probability) {
      case 'LOW':
        return 'Low';

      case 'MEDIUM':
        return 'Medium';

      case 'HIGH':
        return 'High';

      default:
        return probability;
    }
  };

  /**
   * Impact Label
   */
  const getImpactLabel = (
    impact: RiskImpact,
  ): string => {
    switch (impact) {
      case 'LOW':
        return 'Low';

      case 'MEDIUM':
        return 'Medium';

      case 'HIGH':
        return 'High';

      case 'CRITICAL':
        return 'Critical';

      default:
        return impact;
    }
  };

  /**
   * Risk Level 계산
   *
   * RISK_DESIGN.md의 Matrix 정의를 그대로 사용한다.
   *
   *               Impact
   *             LOW  MED  HIGH  CRIT
   * Probability
   * LOW          L    L    M     H
   * MEDIUM       L    M    H     H
   * HIGH         M    H    H     C
   */
  const getRiskLevel = (
    probability: RiskProbability,
    impact: RiskImpact,
  ): 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' => {
    if (probability === 'LOW') {
      if (impact === 'LOW') {
        return 'LOW';
      }

      if (impact === 'MEDIUM') {
        return 'LOW';
      }

      if (impact === 'HIGH') {
        return 'MEDIUM';
      }

      return 'HIGH';
    }

    if (probability === 'MEDIUM') {
      if (impact === 'LOW') {
        return 'LOW';
      }

      if (impact === 'MEDIUM') {
        return 'MEDIUM';
      }

      return 'HIGH';
    }

    /**
     * probability === HIGH
     */
    if (impact === 'LOW') {
      return 'MEDIUM';
    }

    if (impact === 'MEDIUM') {
      return 'HIGH';
    }

    if (impact === 'HIGH') {
      return 'HIGH';
    }

    return 'CRITICAL';
  };

  /**
   * Risk Level Label
   */
  const getRiskLevelLabel = (
    level: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL',
  ): string => {
    switch (level) {
      case 'LOW':
        return 'Low';

      case 'MEDIUM':
        return 'Medium';

      case 'HIGH':
        return 'High';

      case 'CRITICAL':
        return 'Critical';

      default:
        return level;
    }
  };

  /**
   * Risk Level CSS Class
   */
  const getRiskLevelClassName = (
    level: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL',
  ): string => {
    switch (level) {
      case 'LOW':
        return 'risk-matrix__cell--low';

      case 'MEDIUM':
        return 'risk-matrix__cell--medium';

      case 'HIGH':
        return 'risk-matrix__cell--high';

      case 'CRITICAL':
        return 'risk-matrix__cell--critical';

      default:
        return '';
    }
  };

  /**
   * 특정 Matrix Cell에 해당하는 Risk 목록 조회
   */
  const getRisksForCell = (
    probability: RiskProbability,
    impact: RiskImpact,
  ): RiskResponse[] => {
    return risks.filter(
      (risk) =>
        risk.probability === probability &&
        risk.impact === impact,
    );
  };

  /**
   * Matrix 전체 Risk 개수
   */
  const totalRiskCount = risks.length;

  /**
   * Risk Level별 집계
   *
   * Matrix에서 계산된 Level을 기준으로 집계한다.
   */
  const levelCounts = risks.reduce(
    (
      counts,
      risk,
    ) => {
      const level = getRiskLevel(
        risk.probability,
        risk.impact,
      );

      counts[level] += 1;

      return counts;
    },
    {
      LOW: 0,
      MEDIUM: 0,
      HIGH: 0,
      CRITICAL: 0,
    },
  );

  return (
    <section className="risk-matrix">
      {/* ============================================================
          Header
          ============================================================ */}

      <div className="risk-matrix__header">
        <div>
          <h2 className="risk-matrix__title">
            Risk Matrix
          </h2>

          <p className="risk-matrix__description">
            Probability와 Impact를 기준으로 Risk 수준을
            시각적으로 표시합니다.
          </p>
        </div>

        <div className="risk-matrix__summary">
          <span className="risk-matrix__summary-label">
            Total Risks
          </span>

          <strong className="risk-matrix__summary-value">
            {totalRiskCount}
          </strong>
        </div>
      </div>

      {/* ============================================================
          Matrix
          ============================================================ */}

      <div className="risk-matrix__body">
        <div className="risk-matrix__table">
          {/* ========================================================
              Top-left corner
              ======================================================== */}

          <div className="risk-matrix__corner">
            <span>
              Probability
            </span>

            <span>
              Impact
            </span>
          </div>

          {/* ========================================================
              Impact Header
              ======================================================== */}

          {impacts.map(
            (impact) => (
              <div
                key={impact}
                className="risk-matrix__axis risk-matrix__axis--impact"
              >
                {getImpactLabel(impact)}
              </div>
            ),
          )}

          {/* ========================================================
              Probability Rows
              ======================================================== */}

          {probabilities.map(
            (probability) => (
              <div
                key={probability}
                className="risk-matrix__row"
              >
                {/* ==================================================
                    Probability Axis
                    ================================================== */}

                <div className="risk-matrix__axis risk-matrix__axis--probability">
                  {getProbabilityLabel(
                    probability,
                  )}
                </div>

                {/* ==================================================
                    Impact Cells
                    ================================================== */}

                {impacts.map(
                  (impact) => {
                    const cellRisks =
                      getRisksForCell(
                        probability,
                        impact,
                      );

                    const riskLevel =
                      getRiskLevel(
                        probability,
                        impact,
                      );

                    return (
                      <div
                        key={`${probability}-${impact}`}
                        className={`risk-matrix__cell ${getRiskLevelClassName(
                          riskLevel,
                        )}`}
                        title={`${getProbabilityLabel(
                          probability,
                        )} Probability / ${getImpactLabel(
                          impact,
                        )} Impact`}
                      >
                        <span className="risk-matrix__cell-level">
                          {getRiskLevelLabel(
                            riskLevel,
                          )}
                        </span>

                        <strong className="risk-matrix__cell-count">
                          {cellRisks.length}
                        </strong>

                        <span className="risk-matrix__cell-unit">
                          Risk
                        </span>
                      </div>
                    );
                  },
                )}
              </div>
            ),
          )}
        </div>
      </div>

      {/* ============================================================
          Legend
          ============================================================ */}

      <div className="risk-matrix__legend">
        <div className="risk-matrix__legend-title">
          Risk Level
        </div>

        <div className="risk-matrix__legend-items">
          <div className="risk-matrix__legend-item">
            <span className="risk-matrix__legend-marker risk-matrix__legend-marker--low" />

            <span>
              Low
            </span>

            <strong>
              {levelCounts.LOW}
            </strong>
          </div>

          <div className="risk-matrix__legend-item">
            <span className="risk-matrix__legend-marker risk-matrix__legend-marker--medium" />

            <span>
              Medium
            </span>

            <strong>
              {levelCounts.MEDIUM}
            </strong>
          </div>

          <div className="risk-matrix__legend-item">
            <span className="risk-matrix__legend-marker risk-matrix__legend-marker--high" />

            <span>
              High
            </span>

            <strong>
              {levelCounts.HIGH}
            </strong>
          </div>

          <div className="risk-matrix__legend-item">
            <span className="risk-matrix__legend-marker risk-matrix__legend-marker--critical" />

            <span>
              Critical
            </span>

            <strong>
              {levelCounts.CRITICAL}
            </strong>
          </div>
        </div>
      </div>
    </section>
  );
};

export default RiskMatrix;