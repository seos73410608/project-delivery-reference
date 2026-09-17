import type {
  RiskResponse,
} from '../types/risk';

import RiskRow from './RiskRow';


interface RiskListProps {

  risks: RiskResponse[];


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
 * Risk 목록
 * =====================================================
 *
 * Risk 데이터를 Table 형태로 표시한다.
 *
 * 주요 표시 정보:
 *
 * - Risk Key
 * - 제목
 * - Priority
 * - Probability
 * - Impact
 * - 담당자
 * - 상태
 * - 관리
 */
const RiskList = ({

  risks,

  onSelect,
  onEdit,
  onDelete,

}: RiskListProps) => {


  /**
   * ===================================================
   * Empty State
   * ===================================================
   */
  if (
    risks.length === 0
  ) {

    return (

      <div
        className="card risk-list"
      >

        <div
          className="risk-list__empty"
        >

          <p>
            등록된 Risk가 없습니다.
          </p>


          <span>
            검색 조건을 변경하거나 새로운
            Risk를 등록해주세요.
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
      className="card risk-list"
    >


      {/* ================================================
          Card Header
          ================================================ */}

      <div
        className="
          card__header
          risk-list__header
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
            Risk 목록
          </h2>


          <p
            className="
              card__description
            "
          >
            프로젝트 수행 중 발생할 가능성이 있는
            Risk를 확인하고 관리합니다.
          </p>

        </div>


        <div
          className="card__meta"
        >

          총{' '}

          <strong>
            {risks.length}
          </strong>

          건

        </div>

      </div>


      {/* ================================================
          Table
          ================================================ */}

      <div
        className="
          risk-list__table-wrapper
        "
      >

        <table
          className="risk-list__table"
        >

          <thead>

            <tr>

              <th>
                Risk Key
              </th>


              <th>
                제목
              </th>


              <th>
                중요도
              </th>


              <th>
                발생 가능성
              </th>


              <th>
                영향도
              </th>


              <th>
                담당자
              </th>


              <th>
                상태
              </th>


              <th>
                관리
              </th>

            </tr>

          </thead>


          <tbody>

            {risks.map(
              (risk) => (

                <RiskRow

                  key={risk.id}

                  risk={risk}

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


export default RiskList;