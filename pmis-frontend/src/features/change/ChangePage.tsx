import { useCallback, useEffect, useMemo, useState } from "react";

import {
  getProjectChanges,
  updateChangeApproval,
  updateChangeStatus,
} from "@/features/change/api/changeApi";
import ChangeApprovalForm from "@/features/change/components/ChangeApprovalForm";
import ChangeDetail from "@/features/change/components/ChangeDetail";
import ChangeDialog from "@/features/change/components/ChangeDialog";
import ChangeList from "@/features/change/components/ChangeList";
import ChangeSummary from "@/features/change/components/ChangeSummary";
import ChangeToolbar from "@/features/change/components/ChangeToolbar";
import type {
  ChangeApprovalRequest,
  ChangePriority,
  ChangeResponse,
  ChangeStatus,
  ChangeStatusUpdateRequest,
} from "@/features/change/types/change";

import "./styles/Change.css";

type FilterValue<T extends string> = T | "ALL";

interface ChangePageProps {
  projectId: number;
  onCreateClick?: () => void;
  onChangeClick?: (change: ChangeResponse) => void;
}

export default function ChangePage({
  projectId,
  onCreateClick,
  onChangeClick,
}: ChangePageProps) {
  const [changes, setChanges] = useState<ChangeResponse[]>([]);
  const [searchText, setSearchText] = useState("");
  const [status, setStatus] =
    useState<FilterValue<ChangeStatus>>("ALL");
  const [priority, setPriority] =
    useState<FilterValue<ChangePriority>>("ALL");

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  /*
   * Dialog 상태
   *
   * selectedChange === null
   *   → 등록 모드
   *
   * selectedChange !== null
   *   → 수정 모드
   */
  const [dialogOpen, setDialogOpen] = useState(false);

  const [selectedChange, setSelectedChange] =
    useState<ChangeResponse | null>(null);

  const [workflowLoading, setWorkflowLoading] = useState(false);

  const loadChanges = useCallback(async () => {
    if (!projectId) {
      setChanges([]);
      setSelectedChange(null);
      setErrorMessage("프로젝트를 선택해 주세요.");
      return;
    }

    setLoading(true);
    setErrorMessage("");

    try {
      const data = await getProjectChanges(projectId);

      setChanges(data);

      /*
       * 상세가 열려 있는 상태에서 저장/상태변경/승인처리 후
       * 최신 데이터로 selectedChange도 갱신한다.
       */
      setSelectedChange((current) => {
        if (!current) {
          return null;
        }

        return (
          data.find((change) => change.id === current.id) ?? null
        );
      });
    } catch (error) {
      console.error("변경 요청 목록 조회 실패:", error);

      setErrorMessage(
        "변경 요청 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.",
      );

      setChanges([]);
      setSelectedChange(null);
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    void loadChanges();
  }, [loadChanges]);

  const filteredChanges = useMemo(() => {
    const keyword = searchText.trim().toLowerCase();

    return changes.filter((change) => {
      const matchesKeyword =
        keyword.length === 0 ||
        change.title.toLowerCase().includes(keyword) ||
        (change.changeKey ?? "").toLowerCase().includes(keyword) ||
        (change.description ?? "").toLowerCase().includes(keyword);

      const matchesStatus =
        status === "ALL" || change.status === status;

      const matchesPriority =
        priority === "ALL" || change.priority === priority;

      return matchesKeyword && matchesStatus && matchesPriority;
    });
  }, [changes, searchText, status, priority]);

  /*
   * 목록에서 변경 요청 선택
   *
   * 여기서는 Dialog를 열지 않고
   * 상세 영역만 표시한다.
   */
  const handleChangeClick = (change: ChangeResponse) => {
    setSelectedChange(change);
    onChangeClick?.(change);
  };

  /*
   * 신규 변경 요청 등록
   *
   * selectedChange를 반드시 null로 만든다.
   * 그래야 ChangeDialog가 등록 모드로 동작한다.
   */
  const handleCreateClick = () => {
    setSelectedChange(null);
    setDialogOpen(true);

    onCreateClick?.();
  };

  /*
   * 기존 변경 요청 수정
   *
   * selectedChange를 유지한 상태에서 Dialog를 연다.
   * ChangeDialog가 change !== null을 보고 수정 모드로 동작한다.
   */
  const handleEditClick = () => {
    if (!selectedChange) {
      return;
    }

    setDialogOpen(true);
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
  };

  /*
   * 등록/수정 저장 완료
   */
  const handleChangeSaved = async (
    _savedChange: ChangeResponse,
  ) => {
    await loadChanges();
  };

  /*
   * 상세 닫기
   */
  const handleCloseDetail = () => {
    setSelectedChange(null);
  };

  /*
   * 상태 변경 또는 승인/반려 후
   * 목록과 상세에 동일한 최신 객체를 반영한다.
   */
  const replaceChange = (updatedChange: ChangeResponse) => {
    setChanges((currentChanges) =>
      currentChanges.map((change) =>
        change.id === updatedChange.id
          ? updatedChange
          : change,
      ),
    );

    setSelectedChange(updatedChange);
  };

  /*
   * 상태 변경
   */
  const handleStatusSubmit = async (
    request: ChangeStatusUpdateRequest,
  ) => {
    if (!selectedChange) {
      return;
    }

    setWorkflowLoading(true);
    setErrorMessage("");

    try {
      const updatedChange = await updateChangeStatus(
        selectedChange.id,
        request,
      );

      replaceChange(updatedChange);
    } catch (error) {
      console.error("변경 요청 상태 변경 실패:", error);

      throw error;
    } finally {
      setWorkflowLoading(false);
    }
  };

  /*
   * 승인 / 반려
   */
  const handleApprovalSubmit = async (
    request: ChangeApprovalRequest,
  ) => {
    if (!selectedChange) {
      return;
    }

    setWorkflowLoading(true);
    setErrorMessage("");

    try {
      const updatedChange = await updateChangeApproval(
        selectedChange.id,
        request,
      );

      replaceChange(updatedChange);
    } catch (error) {
      console.error("변경 요청 승인 처리 실패:", error);

      throw error;
    } finally {
      setWorkflowLoading(false);
    }
  };

  return (
    <main
      style={{
        display: "flex",
        flexDirection: "column",
        gap: 20,
        padding: 24,
      }}
    >
      {/* Page Header */}
      <header
        style={{
          display: "flex",
          alignItems: "flex-start",
          justifyContent: "space-between",
          gap: 16,
          flexWrap: "wrap",
        }}
      >
        <div>
          <h1
            style={{
              margin: 0,
              color: "#0f172a",
              fontSize: 24,
              fontWeight: 700,
            }}
          >
            변경 관리
          </h1>

          <p
            style={{
              margin: "8px 0 0",
              color: "#64748b",
              fontSize: 14,
            }}
          >
            프로젝트 변경 요청을 조회하고 진행 상태를 관리합니다.
          </p>
        </div>
      </header>

      {/* Summary */}
      <ChangeSummary changes={changes} />

      {/* Toolbar */}
      <ChangeToolbar
        searchText={searchText}
        status={status}
        priority={priority}
        onSearchTextChange={setSearchText}
        onStatusChange={setStatus}
        onPriorityChange={setPriority}
        onCreateClick={handleCreateClick}
      />

      {/* Error */}
      {errorMessage && (
        <div
          role="alert"
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 12,
            padding: "12px 16px",
            border: "1px solid #fecaca",
            borderRadius: 6,
            backgroundColor: "#fef2f2",
            color: "#b91c1c",
            fontSize: 14,
          }}
        >
          <span>{errorMessage}</span>

          <button
            type="button"
            onClick={() => void loadChanges()}
            style={{
              padding: "6px 10px",
              border: "1px solid #fca5a5",
              borderRadius: 4,
              backgroundColor: "#ffffff",
              color: "#991b1b",
              cursor: "pointer",
            }}
          >
            다시 시도
          </button>
        </div>
      )}

      {/* Change List */}
      <section
        aria-label="변경 요청 목록"
        style={{
          display: "flex",
          flexDirection: "column",
          gap: 10,
        }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            gap: 12,
            flexWrap: "wrap",
          }}
        >
          <h2
            style={{
              margin: 0,
              color: "#1e293b",
              fontSize: 16,
              fontWeight: 700,
            }}
          >
            변경 요청 목록
          </h2>

          <span
            style={{
              color: "#64748b",
              fontSize: 13,
            }}
          >
            {filteredChanges.length}건
          </span>
        </div>

        <ChangeList
          changes={filteredChanges}
          loading={loading}
          onChangeClick={handleChangeClick}
        />
      </section>

      {/* Change Detail */}
      {selectedChange && (
        <section
          aria-label="변경 요청 상세"
          style={{
            display: "flex",
            flexDirection: "column",
            gap: 16,
            padding: 20,
            border: "1px solid #e2e8f0",
            borderRadius: 8,
            backgroundColor: "#ffffff",
          }}
        >
          <ChangeDetail
            change={selectedChange}
            loading={workflowLoading}
            onStatusSubmit={handleStatusSubmit}
            onEdit={handleEditClick}
            onClose={handleCloseDetail}
          />

          {/* Approval */}
          {selectedChange.status === "PENDING_APPROVAL" && (
            <section
              aria-label="변경 요청 승인"
              style={{
                paddingTop: 16,
                borderTop: "1px solid #e2e8f0",
              }}
            >
              <h3
                style={{
                  margin: "0 0 16px",
                  color: "#1e293b",
                  fontSize: 16,
                  fontWeight: 700,
                }}
              >
                변경 요청 승인
              </h3>

              <ChangeApprovalForm
                change={selectedChange}
                loading={workflowLoading}
                onSubmit={handleApprovalSubmit}
                onCancel={handleCloseDetail}
              />
            </section>
          )}
        </section>
      )}

      {/* Create / Edit Dialog */}
      <ChangeDialog
        open={dialogOpen}
        projectId={projectId}
        change={selectedChange}
        onClose={handleCloseDialog}
        onSaved={handleChangeSaved}
      />
    </main>
  );
}