import {
  useCallback,
  useEffect,
  useState,
} from 'react';


import {
  changeRiskStatus,
  createRisk,
  deleteRisk,
  getRisk,
  getProjectRisks,
  searchRisks,
  updateRisk,
} from './api/riskApi';


import RiskDetail from './components/RiskDetail';
import RiskForm from './components/RiskForm';
import RiskList from './components/RiskList';
import RiskMatrix from './components/RiskMatrix';
import RiskSummary from './components/RiskSummary';
import RiskToolbar from './components/RiskToolbar';


import type {
  RiskCreateRequest,
  RiskImpact,
  RiskPriority,
  RiskProbability,
  RiskResponse,
  RiskSearchParams,
  RiskStatus,
  RiskStatusUpdateRequest,
  RiskUpdateRequest,
} from './types/risk';


import './styles/Risk.css';


const PROJECT_ID = 2;


const RiskPage = () => {

  const [risks, setRisks] = useState<RiskResponse[]>([]);

  const [selectedRisk, setSelectedRisk] =
    useState<RiskResponse | null>(null);

  const [editingRisk, setEditingRisk] =
    useState<RiskResponse | null>(null);

  const [isCreating, setIsCreating] =
    useState(false);

  const [keyword, setKeyword] =
    useState('');

  const [status, setStatus] =
    useState<RiskStatus | ''>('');

  const [priority, setPriority] =
    useState<RiskPriority | ''>('');

  const [probability, setProbability] =
    useState<RiskProbability | ''>('');

  const [impact, setImpact] =
    useState<RiskImpact | ''>('');

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState('');


  /**
   * =====================================================
   * Risk 목록 조회
   * =====================================================
   *
   * GET /api/projects/{projectId}/risks
   *
   * getProjectRisks()는 PageResponse의 content 배열을
   * 반환하므로 그대로 Risk 목록에 저장한다.
   */
  const loadRisks = useCallback(async () => {

    setLoading(true);

    setError('');


    try {

      const data =
        await getProjectRisks(PROJECT_ID);


      setRisks(data);

      setSelectedRisk(null);

    } catch (err) {

      console.error(
        'Failed to load risks.',
        err,
      );

      setError(
        'Risk 목록을 불러오지 못했습니다.',
      );

    } finally {

      setLoading(false);

    }

  }, []);


  /**
   * =====================================================
   * 최초 Risk 목록 조회
   * =====================================================
   */
  useEffect(() => {

    void loadRisks();

  }, [loadRisks]);


  /**
   * =====================================================
   * Risk 검색
   * =====================================================
   *
   * GET /api/risks
   *
   * searchRisks()는 PageResponse<RiskResponse>를
   * 반환하므로 response.content를 사용한다.
   */
  const handleSearch = async () => {

    setLoading(true);

    setError('');


    try {

      const params: RiskSearchParams = {

        projectId: PROJECT_ID,

        page: 0,

        size: 20,

      };


      if (keyword.trim()) {

        params.keyword =
          keyword.trim();

      }


      if (status) {

        params.status =
          status;

      }


      if (priority) {

        params.priority =
          priority;

      }


      if (probability) {

        params.probability =
          probability;

      }


      if (impact) {

        params.impact =
          impact;

      }


      const response =
        await searchRisks(params);


      /**
       * 중요
       *
       * response 자체는 PageResponse 객체다.
       *
       * 잘못된 처리:
       *
       * setRisks(response);
       *
       * 올바른 처리:
       *
       * setRisks(response.content);
       */
      setRisks(response.content);

      setSelectedRisk(null);

      setEditingRisk(null);

      setIsCreating(false);

    } catch (err) {

      console.error(
        'Failed to search risks.',
        err,
      );

      setError(
        'Risk 검색에 실패했습니다.',
      );

    } finally {

      setLoading(false);

    }

  };


  /**
   * =====================================================
   * Risk 상세 조회
   * =====================================================
   *
   * GET /api/risks/{id}
   */
  const handleSelect = async (
    risk: RiskResponse,
  ) => {

    setError('');


    try {

      const data =
        await getRisk(risk.id);


      setSelectedRisk(data);

      setEditingRisk(null);

      setIsCreating(false);

    } catch (err) {

      console.error(
        'Failed to load risk detail.',
        err,
      );

      setError(
        'Risk 상세 정보를 불러오지 못했습니다.',
      );

    }

  };


  /**
   * =====================================================
   * Risk 생성 화면 열기
   * =====================================================
   */
  const handleCreateOpen = () => {

    setSelectedRisk(null);

    setEditingRisk(null);

    setIsCreating(true);

    setError('');

  };


  /**
   * =====================================================
   * Risk 생성
   * =====================================================
   *
   * POST /api/projects/{projectId}/risks
   */
  const handleCreate = async (
    request: RiskCreateRequest,
  ) => {

    setLoading(true);

    setError('');


    try {

      const created =
        await createRisk(
          PROJECT_ID,
          request,
        );


      setRisks((current) => [

        ...current,

        created,

      ]);


      setSelectedRisk(created);

      setEditingRisk(null);

      setIsCreating(false);

    } catch (err) {

      console.error(
        'Failed to create risk.',
        err,
      );

      setError(
        'Risk 생성에 실패했습니다.',
      );

    } finally {

      setLoading(false);

    }

  };


  /**
   * =====================================================
   * Risk 수정 화면 열기
   * =====================================================
   */
  const handleEdit = (
    risk: RiskResponse,
  ) => {

    setSelectedRisk(risk);

    setEditingRisk(risk);

    setIsCreating(false);

    setError('');

  };


  /**
   * =====================================================
   * Risk 수정
   * =====================================================
   *
   * PUT /api/risks/{id}
   */
  const handleUpdate = async (
    riskId: number,
    request: RiskUpdateRequest,
  ) => {

    setLoading(true);

    setError('');


    try {

      const updated =
        await updateRisk(
          riskId,
          request,
        );


      setRisks((current) =>

        current.map((risk) =>

          risk.id === updated.id
            ? updated
            : risk,

        ),

      );


      setSelectedRisk(updated);

      setEditingRisk(null);

      setIsCreating(false);

    } catch (err) {

      console.error(
        'Failed to update risk.',
        err,
      );

      setError(
        'Risk 수정에 실패했습니다.',
      );

    } finally {

      setLoading(false);

    }

  };


  /**
   * =====================================================
   * Risk 상태 변경
   * =====================================================
   *
   * PATCH /api/risks/{id}/status
   */
  const handleStatusChange = async (
    riskId: number,
    request: RiskStatusUpdateRequest,
  ) => {

    setLoading(true);

    setError('');


    try {

      const updated =
        await changeRiskStatus(
          riskId,
          request,
        );


      setRisks((current) =>

        current.map((risk) =>

          risk.id === updated.id
            ? updated
            : risk,

        ),

      );


      setSelectedRisk(updated);

    } catch (err) {

      console.error(
        'Failed to change risk status.',
        err,
      );

      setError(
        'Risk 상태 변경에 실패했습니다.',
      );

    } finally {

      setLoading(false);

    }

  };


  /**
   * =====================================================
   * Risk 삭제
   * =====================================================
   *
   * DELETE /api/risks/{id}
   */
  const handleDelete = async (
    riskId: number,
  ) => {

    const risk =
      risks.find(
        (item) =>
          item.id === riskId,
      );


    if (!risk) {

      setError(
        '삭제할 Risk를 찾을 수 없습니다.',
      );

      return;

    }


    const confirmed =
      window.confirm(
        `"${risk.title}" Risk를 삭제하시겠습니까?`,
      );


    if (!confirmed) {

      return;

    }


    setLoading(true);

    setError('');


    try {

      await deleteRisk(riskId);


      setRisks((current) =>

        current.filter(
          (item) =>
            item.id !== riskId,
        ),

      );


      setSelectedRisk(null);

      setEditingRisk(null);

      setIsCreating(false);

    } catch (err) {

      console.error(
        'Failed to delete risk.',
        err,
      );

      setError(
        'Risk 삭제에 실패했습니다.',
      );

    } finally {

      setLoading(false);

    }

  };


  /**
   * =====================================================
   * Risk Form 닫기
   * =====================================================
   */
  const handleFormClose = () => {

    setIsCreating(false);

    setEditingRisk(null);

    setError('');

  };


  return (

    <div className="page risk-page">

      {/* =====================================================
          Page Header
          ===================================================== */}

      <div className="page__header risk-page__header">

        <div className="page__header-content">

          <div className="page__eyebrow">

            RISK MANAGEMENT

          </div>


          <h1 className="page__title">

            Risk

          </h1>


          <p className="page__description">

            프로젝트 수행 중 발생할 수 있는
            잠재적인 위험을 등록하고 관리합니다.

          </p>

        </div>


        <div className="risk-page__summary">

          <span className="risk-page__summary-label">

            Total

          </span>


          <strong className="risk-page__summary-value">

            {risks.length}

          </strong>


          <span className="risk-page__summary-unit">

            risks

          </span>

        </div>

      </div>


      {/* =====================================================
          Risk Toolbar
          ===================================================== */}

      <div className="risk-page__toolbar">

        <RiskToolbar

          keyword={keyword}

          status={status}

          priority={priority}

          probability={probability}

          impact={impact}

          onKeywordChange={setKeyword}

          onStatusChange={setStatus}

          onPriorityChange={setPriority}

          onProbabilityChange={setProbability}

          onImpactChange={setImpact}

          onSearch={handleSearch}

          onCreate={handleCreateOpen}

        />

      </div>


      {/* =====================================================
          Error Message
          ===================================================== */}

      {error && (

        <div
          className="risk-page__error"
          role="alert"
        >

          <span className="risk-page__error-icon">

            !

          </span>


          <span>

            {error}

          </span>

        </div>

      )}


      {/* =====================================================
          Loading Message
          ===================================================== */}

      {loading && (

        <div className="risk-page__loading">

          <div className="spinner" />


          <span>

            Risk 정보를 처리하는 중입니다...

          </span>

        </div>

      )}


      {/* =====================================================
          Risk Content
          ===================================================== */}

      {!loading && (

        <>

          {/* =====================================================
              Risk Summary
              ===================================================== */}

          <div className="risk-page__summary-section">

            <RiskSummary

              risks={risks}

            />

          </div>


          {/* =====================================================
              Risk Matrix
              ===================================================== */}

          <div className="risk-page__matrix">

            <RiskMatrix

              risks={risks}

            />

          </div>


          {/* =====================================================
              Risk List
              ===================================================== */}

          <div className="risk-page__content">

            <div className="risk-page__content-header">

              <div>

                <h2 className="risk-page__content-title">

                  Risk List

                </h2>


                <p className="risk-page__content-description">

                  프로젝트에 등록된 Risk 목록입니다.

                </p>

              </div>


              <span className="risk-page__content-count">

                Total&nbsp;

                <strong>

                  {risks.length}

                </strong>

              </span>

            </div>


            <RiskList

              risks={risks}

              onSelect={handleSelect}

              onEdit={handleEdit}

              onDelete={handleDelete}

            />

          </div>

        </>

      )}


      {/* =====================================================
          Risk Detail
          ===================================================== */}

      <div className="risk-page__detail">

        <RiskDetail

          risk={selectedRisk}

          onEdit={handleEdit}

          onDelete={(risk) =>

            handleDelete(risk.id)

          }

          onStatusChange={handleStatusChange}

        />

      </div>


      {/* =====================================================
          Risk Create / Edit Form
          ===================================================== */}

      {(isCreating || editingRisk) && (

        <div className="risk-page__form">

          <RiskForm

            projectId={PROJECT_ID}

            risk={editingRisk}

            onSubmit={async (request) => {

              if (editingRisk) {

                await handleUpdate(

                  editingRisk.id,

                  request as RiskUpdateRequest,

                );

                return;

              }


              await handleCreate(

                request as RiskCreateRequest,

              );

            }}

            onCancel={handleFormClose}

          />

        </div>

      )}

    </div>

  );

};


export default RiskPage;
