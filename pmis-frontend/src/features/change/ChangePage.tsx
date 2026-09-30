import { useCallback, useEffect, useMemo, useState } from "react";

import ChangeDialog from "@/features/change/components/ChangeDialog";
import ChangeList from "@/features/change/components/ChangeList";
import ChangeSummary from "@/features/change/components/ChangeSummary";
import ChangeToolbar from "@/features/change/components/ChangeToolbar";
import { getProjectChanges } from "@/features/change/api/changeApi";
import type {
  ChangePriority,
  ChangeResponse,
  ChangeStatus,
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

  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedChange, setSelectedChange] =
    useState<ChangeResponse | null>(null);

  const loadChanges = useCallback(async () => {
    if (!projectId) {
      setChanges([]);
      setErrorMessage("프로젝트를 선택해 주세요.");
      return;
    }

    setLoading(true);
    setErrorMessage("");

    try {
      const data = await getProjectChanges(projectId);

      setChanges(data);
    } catch (error) {
      console.error("변경 요청 목록 조회 실패:", error);

      setErrorMessage(
        "변경 요청 목록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.",
      );

      setChanges([]);
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

  const handleChangeClick = (change: ChangeResponse) => {
    setSelectedChange(change);
    setDialogOpen(true);
    onChangeClick?.(change);
  };

  const handleCreateClick = () => {
    setSelectedChange(null);
    setDialogOpen(true);
    onCreateClick?.();
  };

  const handleCloseDialog = () => {
    setDialogOpen(false);
    setSelectedChange(null);
  };

  const handleChangeSaved = async (_savedChange: ChangeResponse) => {
    await loadChanges();
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

      <ChangeSummary changes={changes} />

      <ChangeToolbar
        searchText={searchText}
        status={status}
        priority={priority}
        onSearchTextChange={setSearchText}
        onStatusChange={setStatus}
        onPriorityChange={setPriority}
        onCreateClick={handleCreateClick}
      />

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

          <span style={{ color: "#64748b", fontSize: 13 }}>
            {filteredChanges.length}건
          </span>
        </div>

        <ChangeList
          changes={filteredChanges}
          loading={loading}
          onChangeClick={handleChangeClick}
        />
      </section>

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