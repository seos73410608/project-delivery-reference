import {
    useEffect,
    useState,
} from "react";

import {
    useParams,
} from "react-router-dom";

import EvidenceSummary from "@/components/dashboard/EvidenceSummary";
import EvidenceChecklist from "@/features/evidence/components/EvidenceChecklist";
import EvidenceMatrix from "@/features/evidence/components/EvidenceMatrix";
import EvidenceDetail from "@/features/evidence/components/EvidenceDetail";

import type {
    Evidence,
} from "@/features/evidence/types/evidence";


function EvidencePage() {

    /**
     * =====================================================
     * Router
     * =====================================================
     *
     * 프로젝트별 증적 관리:
     * /project/:projectId/evidence
     *
     * 기존 공통 증적 관리:
     * /evidence
     */

    const {
        projectId: projectIdParam,
    } = useParams<{
        projectId?: string;
    }>();

    const projectId = projectIdParam
        ? Number(projectIdParam)
        : undefined;

    const isValidProjectId =
        projectId !== undefined &&
        Number.isInteger(projectId) &&
        projectId > 0;

    /**
     * =====================================================
     * Selected Evidence
     * =====================================================
     *
     * 체크리스트에서 선택한 증적을 상세 패널에 전달한다.
     */

    const [selectedEvidence, setSelectedEvidence] =
        useState<Evidence | null>(null);

    /**
     * 프로젝트 경로가 바뀌면 이전 프로젝트의 증적 선택을 초기화한다.
     */
    useEffect(() => {
        setSelectedEvidence(null);
    }, [projectIdParam]);

    /**
     * =====================================================
     * Render
     * =====================================================
     */

    return (
        <div>

            {/* =============================================
                Page Header
                ============================================= */}

            <div
                style={{
                    marginBottom: "24px",
                }}
            >
                <h1
                    style={{
                        margin: 0,
                        fontSize: "28px",
                    }}
                >
                    Evidence Management
                </h1>

                <p
                    style={{
                        marginTop: "8px",
                        color: "#666666",
                    }}
                >
                    {projectIdParam
                        ? isValidProjectId
                            ? `Project ${projectId} evidence tracking and verification`
                            : "올바르지 않은 프로젝트 ID입니다."
                        : "Project evidence tracking and verification"}
                </p>
            </div>

            {/* =============================================
                Invalid Project ID
                ============================================= */}

            {projectIdParam && !isValidProjectId ? (

                <div
                    role="alert"
                    style={{
                        padding: "16px",
                        border: "1px solid #fecaca",
                        borderRadius: "8px",
                        backgroundColor: "#fef2f2",
                        color: "#b91c1c",
                    }}
                >
                    올바른 프로젝트 ID를 확인해주세요.
                </div>

            ) : (

                <>

                    {/* =====================================
                        Evidence Summary
                        ===================================== */}

                    <div
                        style={{
                            marginBottom: "24px",
                        }}
                    >
                        <EvidenceSummary />
                    </div>

                    {/* =====================================
                        Evidence Checklist / Detail
                        ===================================== */}

                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
                            gap: "16px",
                            marginBottom: "24px",
                        }}
                    >
                        <EvidenceChecklist
                            projectId={projectId}
                            selectedEvidenceId={
                                selectedEvidence?.id ?? null
                            }
                            onSelectEvidence={setSelectedEvidence}
                        />

                        <EvidenceDetail
                            evidence={selectedEvidence}
                        />
                    </div>

                    {/* =====================================
                        Evidence Matrix
                        ===================================== */}

                    <div>
                        <EvidenceMatrix />
                    </div>

                </>

            )}

        </div>
    );
}


export default EvidencePage;
