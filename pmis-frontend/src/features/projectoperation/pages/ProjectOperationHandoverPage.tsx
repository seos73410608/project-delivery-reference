
import {
    useCallback,
    useEffect,
    useMemo,
    useState,
} from "react";

import {
    useNavigate,
    useParams,
} from "react-router-dom";

import {
    useAuth,
} from "@/features/auth/context/AuthProvider";

import {
    getApiErrorMessage,
} from "@/api/error";

import {
    getProjectOperation,
} from "../api/projectOperationApi";

import {
    getOperationParticipants,
} from "@/features/operation/api/operationApi";

import type {
    OperationParticipantResponse,
} from "@/features/operation/types/operation";

import type {
    ProjectOperationResponse,
} from "../types/projectOperation";

import {
    acceptProjectOperationHandover,
    cancelProjectOperationHandover,
    completeProjectOperationHandover,
    createInitialHandover,
    createOperatorChangeHandover,
    createReHandover,
    getProjectOperationHandoverHistory,
    rejectProjectOperationHandover,
    requestProjectOperationHandover,
    startProjectOperationHandover,
    submitProjectOperationHandoverForReview,
} from "../api/projectOperationHandoverApi";

import type {
    ProjectOperationHandoverResponse,
    ProjectOperationHandoverStatus,
    ProjectOperationHandoverType,
} from "../types/projectOperationHandover";

import "./ProjectOperationHandoverPage.css";


type HandoverCreateMode =
    | "INITIAL"
    | "OPERATOR_CHANGE"
    | "RE_HANDOVER";


const HANDOVER_TYPE_LABELS: Record<
    ProjectOperationHandoverType,
    string
> = {
    INITIAL: "최초 인계",
    OPERATOR_CHANGE: "운영자 변경",
    RE_HANDOVER: "재인계",
};


const HANDOVER_STATUS_LABELS: Record<
    ProjectOperationHandoverStatus,
    string
> = {
    DRAFT: "작성",
    REQUESTED: "인계 요청",
    IN_PROGRESS: "인계 진행 중",
    REVIEW: "검토 요청",
    ACCEPTED: "인계 승인",
    COMPLETED: "인계 완료",
    REJECTED: "거절",
    CANCELLED: "취소",
};


function formatDateTime(value: string | null): string {
    if (!value) {
        return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString("ko-KR");
}


function getParticipantLabel(
    participant: OperationParticipantResponse,
): string {
    const roleLabels: Record<string, string> = {
        SERVICE_OWNER: "서비스 책임자",
        OPERATOR: "운영자",
        SERVICE_PROVIDER: "서비스 제공자",
        SUPPORT_PROVIDER: "지원 제공자",
        CUSTOMER: "고객",
        PARTNER: "협력사",
    };

    const organizationName =
        participant.organization?.organizationName;

    return [
        participant.company.companyName,
        organizationName,
        roleLabels[participant.role] ?? participant.role,
        participant.primary ? "대표" : "",
    ].filter(Boolean).join(" / ");
}


function getHandoverActionLabel(
    status: ProjectOperationHandoverStatus,
): string[] {
    switch (status) {
        case "DRAFT":
            return ["request", "reject", "cancel"];

        case "REQUESTED":
            return ["start", "reject", "cancel"];

        case "IN_PROGRESS":
            return ["review", "reject", "cancel"];

        case "REVIEW":
            return ["accept", "reject", "cancel"];

        case "ACCEPTED":
            return ["complete"];

        default:
            return [];
    }
}


function ProjectOperationHandoverPage() {

    const navigate = useNavigate();

    const {
        projectId,
        projectOperationId,
    } = useParams<{
        projectId: string;
        projectOperationId: string;
    }>();

    const { user } = useAuth();

    const isAdmin = user?.role === "ROLE_ADMIN";

    const parsedProjectId = Number(projectId);
    const parsedProjectOperationId = Number(projectOperationId);

    const validRoute =
        /^[1-9]\d*$/.test(projectId ?? "") &&
        /^[1-9]\d*$/.test(projectOperationId ?? "");

    const [projectOperation, setProjectOperation] =
        useState<ProjectOperationResponse | null>(null);

    const [history, setHistory] =
        useState<ProjectOperationHandoverResponse[]>([]);

    const [participants, setParticipants] =
        useState<OperationParticipantResponse[]>([]);

    const [loading, setLoading] = useState(true);
    const [historyLoading, setHistoryLoading] = useState(false);
    const [participantsLoading, setParticipantsLoading] =
        useState(false);

    const [submitting, setSubmitting] = useState(false);

    const [error, setError] = useState<string | null>(null);
    const [actionError, setActionError] = useState<string | null>(null);

    const [createMode, setCreateMode] =
        useState<HandoverCreateMode>("INITIAL");

    const [plannedHandoverDate, setPlannedHandoverDate] =
        useState("");

    const [fromParticipantId, setFromParticipantId] =
        useState("");

    const [toParticipantId, setToParticipantId] =
        useState("");

    const [notes, setNotes] = useState("");


    const latestHandover = useMemo(
        () => history.length > 0
            ? history.reduce((latest, current) =>
                current.sequenceNo > latest.sequenceNo
                    ? current
                    : latest,
            )
            : null,
        [history],
    );


    const canCreateHandover = useMemo(() => {

        if (!isAdmin || !projectOperation || submitting) {
            return false;
        }

        if (
            projectOperation.status !== "PLANNED" &&
            projectOperation.status !== "ACTIVE"
        ) {
            return false;
        }

        if (!latestHandover) {
            return createMode === "INITIAL";
        }

        return latestHandover.status === "COMPLETED" &&
            createMode !== "INITIAL";

    }, [
        isAdmin,
        projectOperation,
        submitting,
        latestHandover,
        createMode,
    ]);


    /**
     * 프로젝트 운영 관계 및 인수인계 이력 조회
     */
    const fetchPageData = useCallback(async () => {

        if (!validRoute) {
            setError("올바르지 않은 프로젝트 또는 운영 관계 ID입니다.");
            setLoading(false);
            return;
        }

        try {

            setLoading(true);
            setError(null);

            const relationship = await getProjectOperation(
                parsedProjectOperationId,
            );

            if (relationship.projectId !== parsedProjectId) {
                setError(
                    "요청한 프로젝트에 연결된 운영 서비스가 아닙니다.",
                );
                setProjectOperation(null);
                setHistory([]);
                return;
            }

            setProjectOperation(relationship);

            setHistoryLoading(true);

            const handoverHistory =
                await getProjectOperationHandoverHistory(
                    parsedProjectOperationId,
                );

            setHistory(handoverHistory);

        } catch (err) {

            setError(
                getApiErrorMessage(
                    err,
                    "인수인계 정보를 불러오는 중 오류가 발생했습니다.",
                ),
            );

            setProjectOperation(null);
            setHistory([]);

        } finally {

            setHistoryLoading(false);
            setLoading(false);
        }

    }, [
        validRoute,
        parsedProjectId,
        parsedProjectOperationId,
    ]);


    useEffect(() => {
        void fetchPageData();
    }, [fetchPageData]);


    /**
     * 예정일 기준 인계 가능 참여자 조회
     */
    const fetchParticipants = useCallback(async () => {

        if (!projectOperation || !isAdmin) {
            setParticipants([]);
            return;
        }

        try {

            setParticipantsLoading(true);
            setActionError(null);

            const result = await getOperationParticipants(
                projectOperation.operationId,
                plannedHandoverDate || undefined,
            );

            setParticipants(result);

        } catch (err) {

            setParticipants([]);

            setActionError(
                getApiErrorMessage(
                    err,
                    "인계 가능한 운영 참여자를 불러오지 못했습니다.",
                ),
            );

        } finally {

            setParticipantsLoading(false);
        }

    }, [
        projectOperation,
        isAdmin,
        plannedHandoverDate,
    ]);


    useEffect(() => {

        if (isAdmin && projectOperation) {
            void fetchParticipants();
        }

    }, [isAdmin, projectOperation, fetchParticipants]);


    /**
     * 인계 유형 변경
     */
    const handleCreateModeChange = (
        mode: HandoverCreateMode,
    ) => {

        setCreateMode(mode);
        setFromParticipantId("");
        setToParticipantId("");
        setActionError(null);
    };


    /**
     * 인계 등록
     */
    const handleCreate = async () => {

        if (!projectOperation || !isAdmin || submitting) {
            return;
        }

        if (!toParticipantId) {
            setActionError("인계 대상 참여자를 선택해 주세요.");
            return;
        }

        const commonRequest = {
            projectOperationId: projectOperation.id,
            plannedHandoverDate: plannedHandoverDate || null,
            notes: notes.trim() || null,
        };

        try {

            setSubmitting(true);
            setActionError(null);

            if (createMode === "INITIAL") {

                await createInitialHandover({
                    ...commonRequest,
                    toParticipantId: Number(toParticipantId),
                });

            } else {

                if (!fromParticipantId) {
                    setActionError("인계하는 참여자를 선택해 주세요.");
                    return;
                }

                if (fromParticipantId === toParticipantId) {
                    setActionError(
                        "인계하는 참여자와 인계받는 참여자는 달라야 합니다.",
                    );
                    return;
                }

                const request = {
                    ...commonRequest,
                    fromParticipantId: Number(fromParticipantId),
                    toParticipantId: Number(toParticipantId),
                };

                if (createMode === "OPERATOR_CHANGE") {
                    await createOperatorChangeHandover(request);
                } else {
                    await createReHandover(request);
                }
            }

            setNotes("");
            setPlannedHandoverDate("");
            setFromParticipantId("");
            setToParticipantId("");

            await fetchPageData();

        } catch (err) {

            setActionError(
                getApiErrorMessage(
                    err,
                    "인수인계 등록 중 오류가 발생했습니다.",
                ),
            );

        } finally {

            setSubmitting(false);
        }
    };


    /**
     * 인수인계 상태 변경
     */
    const handleWorkflowAction = async (
        handover: ProjectOperationHandoverResponse,
        action: string,
    ) => {

        if (!isAdmin || submitting) {
            return;
        }

        const actionLabels: Record<string, string> = {
            request: "인계 요청",
            start: "인계 진행 시작",
            review: "검토 요청",
            accept: "인계 승인",
            complete: "인계 완료",
            reject: "인계 거절",
            cancel: "인계 취소",
        };

        const label = actionLabels[action];

        if (!label) {
            return;
        }

        if (!window.confirm(
            `${handover.sequenceNo}차 인수인계의 "${label}" 작업을 진행하시겠습니까?`,
        )) {
            return;
        }

        try {

            setSubmitting(true);
            setActionError(null);

            switch (action) {

                case "request":
                    await requestProjectOperationHandover(handover.id);
                    break;

                case "start":
                    await startProjectOperationHandover(handover.id);
                    break;

                case "review":
                    await submitProjectOperationHandoverForReview(
                        handover.id,
                    );
                    break;

                case "accept":
                    await acceptProjectOperationHandover(handover.id);
                    break;

                case "complete":
                    await completeProjectOperationHandover(handover.id);
                    break;

                case "reject":
                    await rejectProjectOperationHandover(handover.id);
                    break;

                case "cancel":
                    await cancelProjectOperationHandover(handover.id);
                    break;

                default:
                    return;
            }

            await fetchPageData();

        } catch (err) {

            setActionError(
                getApiErrorMessage(
                    err,
                    "인수인계 상태 변경 중 오류가 발생했습니다.",
                ),
            );

        } finally {

            setSubmitting(false);
        }
    };


    /**
     * 인계 참여자 선택 옵션
     */
    const participantOptions = participants.map((participant) => (
        <option
            key={participant.id}
            value={participant.id}
        >
            {getParticipantLabel(participant)}
        </option>
    ));


    if (loading) {

        return (
            <div className="project-operation-handover-page">
                <div className="project-operation-handover-state">
                    프로젝트 운영 서비스 및 인수인계 이력을 불러오는 중입니다.
                </div>
            </div>
        );
    }


    if (error || !projectOperation) {

        return (
            <div className="project-operation-handover-page">

                <div className="project-operation-handover-error">

                    <h2>
                        인수인계 정보를 불러올 수 없습니다.
                    </h2>

                    <p>
                        {error || "프로젝트 운영 관계 정보가 없습니다."}
                    </p>

                    <button
                        type="button"
                        className="button button--secondary"
                        onClick={() =>
                            navigate(`/project/${projectId}/detail`)
                        }
                    >
                        프로젝트로 돌아가기
                    </button>

                </div>

            </div>
        );
    }


    return (
        <div className="project-operation-handover-page">

            {/* 페이지 헤더 */}

            <div className="project-operation-handover-heading">

                <div>

                    <h1>
                        운영 인수인계 이력
                    </h1>

                    <p>
                        프로젝트와 연결된 운영 서비스의 인수인계 내역을 조회하고 관리합니다.
                    </p>

                </div>

                <button
                    type="button"
                    className="button button--secondary"
                    onClick={() =>
                        navigate(`/project/${projectId}/detail`)
                    }
                >
                    프로젝트 상세
                </button>

            </div>


            {/* 프로젝트 운영 관계 정보 */}

            <section className="project-operation-handover-summary">

                <h2>
                    연결 운영 서비스
                </h2>

                <dl>

                    <div>
                        <dt>프로젝트</dt>
                        <dd>
                            {projectOperation.projectName}
                            {" "}
                            ({projectOperation.projectCode})
                        </dd>
                    </div>

                    <div>
                        <dt>운영 서비스</dt>
                        <dd>
                            {projectOperation.operationName}
                            {" "}
                            ({projectOperation.operationCode})
                        </dd>
                    </div>

                    <div>
                        <dt>관계 상태</dt>
                        <dd>{projectOperation.status}</dd>
                    </div>

                </dl>

            </section>


            {/* 인수인계 등록 */}

            {isAdmin && (

                <section className="project-operation-handover-create">

                    <div className="project-operation-handover-section-heading">

                        <div>
                            <h2>인수인계 등록</h2>
                            <p>
                                운영 참여자를 선택하여 인수인계를 등록합니다.
                            </p>
                        </div>

                    </div>


                    <div className="project-operation-handover-form">

                        <div className="form__group">

                            <label htmlFor="handoverType">
                                인계 유형
                            </label>

                            <select
                                id="handoverType"
                                className="form__select"
                                value={createMode}
                                onChange={(event) =>
                                    handleCreateModeChange(
                                        event.target.value as HandoverCreateMode,
                                    )
                                }
                                disabled={submitting}
                            >

                                {!latestHandover && (
                                    <option value="INITIAL">
                                        최초 인계
                                    </option>
                                )}

                                {latestHandover?.status === "COMPLETED" && (
                                    <>
                                        <option value="OPERATOR_CHANGE">
                                            운영자 변경
                                        </option>

                                        <option value="RE_HANDOVER">
                                            재인계
                                        </option>
                                    </>
                                )}

                            </select>

                        </div>


                        <div className="form__group">

                            <label htmlFor="plannedHandoverDate">
                                예정 인계일
                            </label>

                            <input
                                id="plannedHandoverDate"
                                type="date"
                                className="form__input"
                                value={plannedHandoverDate}
                                onChange={(event) => {
                                    setPlannedHandoverDate(
                                        event.target.value,
                                    );
                                    setFromParticipantId("");
                                    setToParticipantId("");
                                }}
                                disabled={submitting}
                            />

                        </div>


                        {createMode !== "INITIAL" && (

                            <div className="form__group">

                                <label htmlFor="fromParticipantId">
                                    인계하는 참여자
                                </label>

                                <select
                                    id="fromParticipantId"
                                    className="form__select"
                                    value={fromParticipantId}
                                    onChange={(event) =>
                                        setFromParticipantId(event.target.value)
                                    }
                                    disabled={submitting || participantsLoading}
                                >

                                    <option value="">
                                        참여자를 선택하세요
                                    </option>

                                    {participantOptions}

                                </select>

                            </div>
                        )}


                        <div className="form__group">

                            <label htmlFor="toParticipantId">
                                인계받는 참여자
                            </label>

                            <select
                                id="toParticipantId"
                                className="form__select"
                                value={toParticipantId}
                                onChange={(event) =>
                                    setToParticipantId(event.target.value)
                                }
                                disabled={submitting || participantsLoading}
                            >

                                <option value="">
                                    참여자를 선택하세요
                                </option>

                                {participantOptions}

                            </select>

                        </div>


                        <div className="form__group form__group--full">

                            <label htmlFor="handoverNotes">
                                비고
                            </label>

                            <textarea
                                id="handoverNotes"
                                className="form__textarea"
                                value={notes}
                                onChange={(event) =>
                                    setNotes(event.target.value)
                                }
                                rows={3}
                                placeholder="인수인계 관련 참고사항을 입력하세요."
                                disabled={submitting}
                            />

                        </div>

                    </div>


                    {participantsLoading && (
                        <p className="project-operation-handover-help">
                            인계 가능한 운영 참여자를 조회하고 있습니다.
                        </p>
                    )}

                    {!participantsLoading && participants.length === 0 && (
                        <p className="project-operation-handover-help">
                            선택한 예정일에 인계 가능한 운영 참여자가 없습니다.
                        </p>
                    )}

                    {latestHandover &&
                        latestHandover.status !== "COMPLETED" && (
                            <p className="project-operation-handover-help">
                                이전 인수인계가 완료된 후 다음 인수인계를 등록할 수 있습니다.
                            </p>
                        )}

                    {actionError && (
                        <div
                            className="project-operation-handover-action-error"
                            role="alert"
                        >
                            {actionError}
                        </div>
                    )}

                    <div className="project-operation-handover-form-actions">

                        <button
                            type="button"
                            className="button button--primary"
                            onClick={handleCreate}
                            disabled={
                                !canCreateHandover ||
                                participantsLoading ||
                                participants.length === 0
                            }
                        >
                            {submitting ? "처리 중..." : "인수인계 등록"}
                        </button>

                    </div>

                </section>
            )}


            {/* 인수인계 이력 */}

            <section className="project-operation-handover-history">

                <div className="project-operation-handover-section-heading">

                    <div>
                        <h2>인수인계 이력</h2>
                        <p>
                            총 {history.length}건
                        </p>
                    </div>

                </div>


                {historyLoading ? (

                    <div className="project-operation-handover-state">
                        인수인계 이력을 조회하고 있습니다.
                    </div>

                ) : history.length === 0 ? (

                    <div className="project-operation-handover-empty">
                        등록된 인수인계 이력이 없습니다.
                    </div>

                ) : (

                    <div className="project-operation-handover-list">

                        {history
                            .slice()
                            .sort((a, b) => b.sequenceNo - a.sequenceNo)
                            .map((handover) => {

                                const actions = getHandoverActionLabel(
                                    handover.status,
                                );

                                return (

                                    <article
                                        key={handover.id}
                                        className="project-operation-handover-card"
                                    >

                                        <div className="project-operation-handover-card-heading">

                                            <div>
                                                <span className="project-operation-handover-sequence">
                                                    {handover.sequenceNo}차 인수인계
                                                </span>

                                                <h3>
                                                    {HANDOVER_TYPE_LABELS[
                                                        handover.handoverType
                                                    ]}
                                                </h3>
                                            </div>

                                            <span
                                                className={`project-operation-handover-status project-operation-handover-status--${handover.status.toLowerCase()}`}
                                            >
                                                {HANDOVER_STATUS_LABELS[
                                                    handover.status
                                                ]}
                                            </span>

                                        </div>


                                        <dl className="project-operation-handover-details">

                                            <div>
                                                <dt>인계하는 참여자</dt>
                                                <dd>
                                                    {handover.fromParticipant
                                                        ? getParticipantLabel(
                                                            handover.fromParticipant,
                                                        )
                                                        : "최초 인계"}
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>인계받는 참여자</dt>
                                                <dd>
                                                    {getParticipantLabel(
                                                        handover.toParticipant,
                                                    )}
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>예정 인계일</dt>
                                                <dd>
                                                    {handover.plannedHandoverDate || "-"}
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>인계 요청일</dt>
                                                <dd>
                                                    {formatDateTime(
                                                        handover.requestedAt,
                                                    )}
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>승인일</dt>
                                                <dd>
                                                    {formatDateTime(
                                                        handover.acceptedAt,
                                                    )}
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>완료일</dt>
                                                <dd>
                                                    {formatDateTime(
                                                        handover.completedAt,
                                                    )}
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>인계 담당자</dt>
                                                <dd>
                                                    {handover.handedOverByName || "-"}
                                                </dd>
                                            </div>

                                            <div>
                                                <dt>승인 담당자</dt>
                                                <dd>
                                                    {handover.acceptedByName || "-"}
                                                </dd>
                                            </div>

                                            <div className="project-operation-handover-notes">
                                                <dt>비고</dt>
                                                <dd>
                                                    {handover.notes || "-"}
                                                </dd>
                                            </div>

                                        </dl>


                                        {isAdmin && actions.length > 0 && (

                                            <div className="project-operation-handover-card-actions">

                                                {actions.map((action) => {

                                                    const labels: Record<string, string> = {
                                                        request: "인계 요청",
                                                        start: "진행 시작",
                                                        review: "검토 요청",
                                                        accept: "승인",
                                                        complete: "완료",
                                                        reject: "거절",
                                                        cancel: "취소",
                                                    };

                                                    return (
                                                        <button
                                                            key={action}
                                                            type="button"
                                                            className={
                                                                action === "reject" ||
                                                                action === "cancel"
                                                                    ? "button button--danger"
                                                                    : "button button--primary"
                                                            }
                                                            disabled={submitting}
                                                            onClick={() =>
                                                                void handleWorkflowAction(
                                                                    handover,
                                                                    action,
                                                                )
                                                            }
                                                        >
                                                            {labels[action]}
                                                        </button>
                                                    );
                                                })}

                                            </div>
                                        )}

                                    </article>
                                );
                            })}

                    </div>
                )}

            </section>

        </div>
    );
}


export default ProjectOperationHandoverPage;
