
import { useCallback, useEffect, useMemo, useState } from "react";
import type { ChangeEvent, FormEvent } from "react";

import { getProjects } from "@/features/project/api/projectApi";
import type { Project } from "@/features/project/types/project";

import { getWbsTree } from "@/features/wbs/api/wbsApi";
import type { WbsTreeResponse } from "@/features/wbs/types/wbs";

import { getProjectSchedules } from "@/features/schedule/api/scheduleApi";
import type { ScheduleResponse } from "@/features/schedule/types/schedule";

import {
    changeTaskStatus,
    createTask,
    getTask,
    searchTasks,
    updateTask,
} from "../api/taskApi";

import {
    TASK_PRIORITY_LABELS,
    TASK_STATUS_LABELS,
} from "../types/task";

import type {
    Task,
    TaskCreateRequest,
    TaskPageResponse,
    TaskPriority,
    TaskSearchParams,
    TaskStatus,
    TaskUpdateRequest,
} from "../types/task";

import "./TaskPage.css";

const PAGE_SIZE = 10;
const TASK_NAME_MAX_LENGTH = 200;
const TASK_DESCRIPTION_MAX_LENGTH = 2000;

const TASK_STATUSES: TaskStatus[] = [
    "PLANNED",
    "IN_PROGRESS",
    "ON_HOLD",
    "COMPLETED",
    "CANCELLED",
];

const TASK_PRIORITIES: TaskPriority[] = [
    "LOW",
    "MEDIUM",
    "HIGH",
    "CRITICAL",
];

interface WbsOption {
    id: number;
    wbsCode: string;
    wbsName: string;
    level: number;
    wbsPath: string;
}

interface TaskSearchConditions {
    keyword: string;
    status: TaskStatus | "";
    priority: TaskPriority | "";
    wbsId: number | "";
    scheduleId: number | "";
}

interface TaskCreateForm {
    wbsId: number | "";
    scheduleId: number | "";
    taskName: string;
    description: string;
    priority: TaskPriority;
    startDate: string;
    endDate: string;
}

type TaskEditForm = TaskCreateForm;

const flattenWbsTree = (nodes: WbsTreeResponse[]): WbsOption[] => {
    const result: WbsOption[] = [];

    const traverse = (
        items: WbsTreeResponse[],
        parentPath: string[] = [],
    ) => {
        items.forEach((item) => {
            const currentLabel = `${item.wbsCode} ${item.wbsName}`;
            const currentPath = [...parentPath, currentLabel];

            result.push({
                id: item.id,
                wbsCode: item.wbsCode,
                wbsName: item.wbsName,
                level: item.level,
                wbsPath: currentPath.join(" > "),
            });

            if (item.children?.length) {
                traverse(item.children, currentPath);
            }
        });
    };

    traverse(nodes);
    return result;
};

const INITIAL_CREATE_FORM: TaskCreateForm = {
    wbsId: "",
    scheduleId: "",
    taskName: "",
    description: "",
    priority: "MEDIUM",
    startDate: "",
    endDate: "",
};

const formatDate = (date: string | null): string => date || "-";

const formatDateTime = (date: string | null): string => {
    if (!date) return "-";

    const parsed = new Date(date);

    if (Number.isNaN(parsed.getTime())) {
        return date;
    }

    return parsed.toLocaleString("ko-KR");
};

const getStatusClassName = (status: TaskStatus): string =>
    `task-status task-status--${status.toLowerCase()}`;

const getPriorityClassName = (priority: TaskPriority): string =>
    `task-priority task-priority--${priority.toLowerCase()}`;

const TaskPage = () => {
    const [projects, setProjects] = useState<Project[]>([]);
    const [selectedProjectId, setSelectedProjectId] = useState<number | "">("");

    const [wbsTree, setWbsTree] = useState<WbsTreeResponse[]>([]);
    const [schedules, setSchedules] = useState<ScheduleResponse[]>([]);
    const [taskPage, setTaskPage] = useState<TaskPageResponse | null>(null);

    // 검색 조건
    const [keyword, setKeyword] = useState("");
    const [status, setStatus] = useState<TaskStatus | "">("");
    const [priority, setPriority] = useState<TaskPriority | "">("");
    const [selectedWbsId, setSelectedWbsId] = useState<number | "">("");
    const [selectedScheduleId, setSelectedScheduleId] = useState<number | "">("");

    // Task 등록 모달
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [createForm, setCreateForm] = useState<TaskCreateForm>(
        INITIAL_CREATE_FORM,
    );
    const [createLoading, setCreateLoading] = useState(false);

    // Task 수정 모달
    const [showEditModal, setShowEditModal] = useState(false);
    const [editTaskId, setEditTaskId] = useState<number | null>(null);
    const [editForm, setEditForm] = useState<TaskEditForm>(INITIAL_CREATE_FORM);
    const [editLoading, setEditLoading] = useState(false);
    const [editErrorMessage, setEditErrorMessage] = useState("");

    // Task 상세 조회 모달
    const [showDetailModal, setShowDetailModal] = useState(false);
    const [selectedTask, setSelectedTask] = useState<Task | null>(null);
    const [detailLoading, setDetailLoading] = useState(false);
    const [detailErrorMessage, setDetailErrorMessage] = useState("");
    const [detailStatus, setDetailStatus] = useState<TaskStatus>("PLANNED");
    const [detailProgress, setDetailProgress] = useState("0");
    const [detailActionLoading, setDetailActionLoading] = useState(false);
    const [detailActionErrorMessage, setDetailActionErrorMessage] = useState("");
    const [detailActionSuccessMessage, setDetailActionSuccessMessage] = useState("");

    const [page, setPage] = useState(0);

    const [projectLoading, setProjectLoading] = useState(false);
    const [relationLoading, setRelationLoading] = useState(false);
    const [taskLoading, setTaskLoading] = useState(false);

    const [errorMessage, setErrorMessage] = useState("");
    const [createErrorMessage, setCreateErrorMessage] = useState("");
    const [successMessage, setSuccessMessage] = useState("");

    useEffect(() => {
        if (!selectedTask) return;

        setDetailStatus(selectedTask.status);
        setDetailProgress(String(selectedTask.progressRate));
    }, [selectedTask]);

    const wbsOptions = useMemo(
        () => flattenWbsTree(wbsTree),
        [wbsTree],
    );

    const wbsMap = useMemo(
        () => new Map(wbsOptions.map((wbs) => [wbs.id, wbs])),
        [wbsOptions],
    );

    const filteredSchedules = useMemo(() => {
        if (!selectedWbsId) return schedules;

        return schedules.filter(
            (schedule) => schedule.wbsId === selectedWbsId,
        );
    }, [schedules, selectedWbsId]);

    const createFilteredSchedules = useMemo(() => {
        if (!createForm.wbsId) return [];

        return schedules.filter(
            (schedule) => schedule.wbsId === createForm.wbsId,
        );
    }, [schedules, createForm.wbsId]);

    // 프로젝트 목록 조회
    useEffect(() => {
        let cancelled = false;

        const fetchProjects = async () => {
            setProjectLoading(true);
            setErrorMessage("");

            try {
                const response = await getProjects({
                    page: 0,
                    size: 100,
                });

                if (cancelled) return;

                setProjects(response.content ?? []);

                if (response.content?.length) {
                    setSelectedProjectId(response.content[0].id);
                }
            } catch {
                if (!cancelled) {
                    setErrorMessage("프로젝트 목록을 불러오지 못했습니다.");
                }
            } finally {
                if (!cancelled) {
                    setProjectLoading(false);
                }
            }
        };

        void fetchProjects();

        return () => {
            cancelled = true;
        };
    }, []);

    // 프로젝트별 WBS 및 Schedule 조회
    useEffect(() => {
        if (!selectedProjectId) {
            setWbsTree([]);
            setSchedules([]);
            setTaskPage(null);
            setRelationLoading(false);
            return;
        }

        let cancelled = false;

        const fetchProjectRelations = async () => {
            setRelationLoading(true);
            setErrorMessage("");

            setWbsTree([]);
            setSchedules([]);
            setSelectedWbsId("");
            setSelectedScheduleId("");

            try {
                const [wbsResult, scheduleResult] = await Promise.all([
                    getWbsTree(selectedProjectId),
                    getProjectSchedules(selectedProjectId),
                ]);

                if (cancelled) return;

                setWbsTree(wbsResult);
                setSchedules(scheduleResult);
            } catch {
                if (!cancelled) {
                    setErrorMessage(
                        "프로젝트의 WBS 또는 Schedule 정보를 불러오지 못했습니다.",
                    );
                }
            } finally {
                if (!cancelled) {
                    setRelationLoading(false);
                }
            }
        };

        void fetchProjectRelations();

        return () => {
            cancelled = true;
        };
    }, [selectedProjectId]);

    // Task 목록 검색
    const fetchTasks = useCallback(async (
        nextPage: number,
        conditions: TaskSearchConditions,
    ) => {
        if (!selectedProjectId) {
            setTaskPage(null);
            return;
        }

        setTaskLoading(true);
        setErrorMessage("");

        const params: TaskSearchParams = {
            projectId: selectedProjectId,
            page: nextPage,
            size: PAGE_SIZE,
            sortBy: "createdAt",
            direction: "DESC",
        };

        const trimmedKeyword = conditions.keyword.trim();

        if (trimmedKeyword) params.keyword = trimmedKeyword;
        if (conditions.status) params.status = conditions.status;
        if (conditions.priority) params.priority = conditions.priority;
        if (conditions.wbsId) params.wbsId = conditions.wbsId;
        if (conditions.scheduleId) params.scheduleId = conditions.scheduleId;

        try {
            const response = await searchTasks(params);
            setTaskPage(response);
            setPage(nextPage);
        } catch {
            setErrorMessage("Task 목록을 불러오지 못했습니다.");
        } finally {
            setTaskLoading(false);
        }
    }, [selectedProjectId]);

    // 최초 Task 조회
    useEffect(() => {
        if (!selectedProjectId || relationLoading) return;

        void fetchTasks(0, {
            keyword: "",
            status: "",
            priority: "",
            wbsId: "",
            scheduleId: "",
        });
    }, [selectedProjectId, relationLoading, fetchTasks]);

    const handleSearch = () => {
        void fetchTasks(0, {
            keyword,
            status,
            priority,
            wbsId: selectedWbsId,
            scheduleId: selectedScheduleId,
        });
    };

    const handleReset = () => {
        setKeyword("");
        setStatus("");
        setPriority("");
        setSelectedWbsId("");
        setSelectedScheduleId("");

        void fetchTasks(0, {
            keyword: "",
            status: "",
            priority: "",
            wbsId: "",
            scheduleId: "",
        });
    };

    const handleProjectChange = (
        event: ChangeEvent<HTMLSelectElement>,
    ) => {
        const value = event.target.value;

        setTaskPage(null);
        setPage(0);
        setKeyword("");
        setStatus("");
        setPriority("");
        setSelectedWbsId("");
        setSelectedScheduleId("");

        setShowCreateModal(false);
        setCreateForm(INITIAL_CREATE_FORM);
        setShowEditModal(false);
        setEditTaskId(null);
        setEditForm(INITIAL_CREATE_FORM);
        setEditErrorMessage("");
        setCreateErrorMessage("");
        setSuccessMessage("");

        setShowDetailModal(false);
        setSelectedTask(null);
        setDetailErrorMessage("");

        setSelectedProjectId(value ? Number(value) : "");
    };

    const handleWbsChange = (
        event: ChangeEvent<HTMLSelectElement>,
    ) => {
        const value = event.target.value;

        setSelectedWbsId(value ? Number(value) : "");
        setSelectedScheduleId("");
    };

    const handleCreateFormChange = (
        event: ChangeEvent<
            HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
        >,
    ) => {
        const { name, value } = event.target;

        setCreateForm((previous) => ({
            ...previous,
            [name]: value,
        }));

        setCreateErrorMessage("");
    };

    const handleCreateScheduleChange = (
        event: ChangeEvent<HTMLSelectElement>,
    ) => {
        const value = event.target.value;

        setCreateForm((previous) => ({
            ...previous,
            scheduleId: value ? Number(value) : "",
        }));

        setCreateErrorMessage("");
    };

    const handleCreateWbsChange = (
        event: ChangeEvent<HTMLSelectElement>,
    ) => {
        const value = event.target.value;

        setCreateForm((previous) => ({
            ...previous,
            wbsId: value ? Number(value) : "",
            scheduleId: "",
        }));

        setCreateErrorMessage("");
    };

    // Task 등록 모달
    const handleOpenCreateModal = () => {
        setCreateForm(INITIAL_CREATE_FORM);
        setCreateErrorMessage("");
        setSuccessMessage("");
        setShowCreateModal(true);
    };

    const handleCloseCreateModal = () => {
        if (createLoading) return;

        setShowCreateModal(false);
        setCreateForm(INITIAL_CREATE_FORM);
        setShowEditModal(false);
        setEditTaskId(null);
        setEditForm(INITIAL_CREATE_FORM);
        setEditErrorMessage("");
        setCreateErrorMessage("");
    };

    // Task 상세 조회
    const handleOpenDetailModal = async (taskId: number) => {
        setShowDetailModal(true);
        setSelectedTask(null);
        setDetailErrorMessage("");
        setDetailActionErrorMessage("");
        setDetailActionSuccessMessage("");
        setDetailLoading(true);

        try {
            const response = await getTask(taskId);
            setSelectedTask(response);
        } catch {
            setDetailErrorMessage(
                "Task 상세 정보를 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.",
            );
        } finally {
            setDetailLoading(false);
        }
    };

    const handleCloseDetailModal = () => {
        if (detailActionLoading) return;

        setShowDetailModal(false);
        setSelectedTask(null);
        setDetailErrorMessage("");
        setDetailActionErrorMessage("");
        setDetailActionSuccessMessage("");
    };

    // 상세 모달에서 Task 상태 변경
    const handleChangeDetailStatus = async () => {
        if (!selectedTask || detailActionLoading) return;

        if (detailStatus === selectedTask.status) {
            setDetailActionErrorMessage("변경할 상태를 선택해 주세요.");
            return;
        }

        setDetailActionLoading(true);
        setDetailActionErrorMessage("");
        setDetailActionSuccessMessage("");

        try {
            const updatedTask = await changeTaskStatus(selectedTask.id, {
                status: detailStatus,
            });

            setSelectedTask(updatedTask);
            setDetailActionSuccessMessage("Task 상태가 변경되었습니다.");

            await fetchTasks(page, {
                keyword,
                status,
                priority,
                wbsId: selectedWbsId,
                scheduleId: selectedScheduleId,
            });
        } catch {
            setDetailActionErrorMessage(
                "Task 상태 변경에 실패했습니다. 입력값과 서버 응답을 확인해 주세요.",
            );
        } finally {
            setDetailActionLoading(false);
        }
    };

    // 상세 모달에서 Task 진척률 변경
    const handleUpdateDetailProgress = async () => {
        if (!selectedTask || detailActionLoading) return;

        const progressRate = Number(detailProgress);

        if (
            detailProgress.trim() === "" ||
            !Number.isInteger(progressRate) ||
            progressRate < 0 ||
            progressRate > 100
        ) {
            setDetailActionErrorMessage("진척률은 0부터 100 사이의 정수로 입력해 주세요.");
            return;
        }

        if (progressRate === selectedTask.progressRate) {
            setDetailActionErrorMessage("변경할 진척률을 입력해 주세요.");
            return;
        }

        setDetailActionLoading(true);
        setDetailActionErrorMessage("");
        setDetailActionSuccessMessage("");

        try {
            // PUT 전체 수정 계약에 따라 수정하지 않는 필드는 최신 서버 값으로 보존합니다.
            const currentTask = await getTask(selectedTask.id);
            const request: TaskUpdateRequest = {
                wbsId: currentTask.wbsId,
                scheduleId: currentTask.scheduleId,
                assigneeId: currentTask.assigneeId,
                taskName: currentTask.taskName,
                description: currentTask.description,
                priority: currentTask.priority,
                progressRate,
                startDate: currentTask.startDate,
                endDate: currentTask.endDate,
                sortOrder: currentTask.sortOrder,
            };

            const updatedTask = await updateTask(currentTask.id, request);
            setSelectedTask(updatedTask);
            setDetailActionSuccessMessage("Task 진척률이 변경되었습니다.");

            await fetchTasks(page, {
                keyword,
                status,
                priority,
                wbsId: selectedWbsId,
                scheduleId: selectedScheduleId,
            });
        } catch {
            setDetailActionErrorMessage(
                "Task 진척률 변경에 실패했습니다. 입력값과 서버 응답을 확인해 주세요.",
            );
        } finally {
            setDetailActionLoading(false);
        }
    };

    // Task 수정 모달 열기: 서버의 최신 상세 정보를 기준으로 폼 구성
    const handleOpenEditModal = async (taskId: number) => {
        setEditTaskId(taskId);
        setEditForm(INITIAL_CREATE_FORM);
        setEditErrorMessage("");
        setShowEditModal(true);
        setEditLoading(true);

        try {
            const task = await getTask(taskId);
            setEditForm({
                wbsId: task.wbsId,
                scheduleId: task.scheduleId ?? "",
                taskName: task.taskName,
                description: task.description ?? "",
                priority: task.priority,
                startDate: task.startDate ?? "",
                endDate: task.endDate ?? "",
            });
        } catch {
            setEditErrorMessage("Task 정보를 불러오지 못했습니다. 다시 시도해 주세요.");
        } finally {
            setEditLoading(false);
        }
    };

    const handleCloseEditModal = () => {
        if (editLoading) return;
        setShowEditModal(false);
        setEditTaskId(null);
        setEditForm(INITIAL_CREATE_FORM);
        setEditErrorMessage("");
    };

    const handleEditFormChange = (
        event: ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>,
    ) => {
        const { name, value } = event.target;
        setEditForm((previous) => ({ ...previous, [name]: value }));
        setEditErrorMessage("");
    };

    const handleEditWbsChange = (event: ChangeEvent<HTMLSelectElement>) => {
        const value = event.target.value;
        setEditForm((previous) => ({
            ...previous,
            wbsId: value ? Number(value) : "",
            scheduleId: "",
        }));
        setEditErrorMessage("");
    };

    const handleEditScheduleChange = (event: ChangeEvent<HTMLSelectElement>) => {
        const value = event.target.value;
        setEditForm((previous) => ({
            ...previous,
            scheduleId: value ? Number(value) : "",
        }));
        setEditErrorMessage("");
    };

    const editFilteredSchedules = useMemo(() => {
        if (!editForm.wbsId) return [];
        return schedules.filter((schedule) => schedule.wbsId === editForm.wbsId);
    }, [schedules, editForm.wbsId]);

    const handleUpdateTask = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (editLoading || editTaskId === null) return;

        if (!editForm.wbsId || !wbsOptions.some((wbs) => wbs.id === editForm.wbsId)) {
            setEditErrorMessage("현재 프로젝트에 속한 유효한 WBS를 선택해 주세요.");
            return;
        }
        const taskName = editForm.taskName.trim();
        if (!taskName) {
            setEditErrorMessage("Task 이름을 입력해 주세요.");
            return;
        }
        if (taskName.length > TASK_NAME_MAX_LENGTH) {
            setEditErrorMessage(`Task 이름은 ${TASK_NAME_MAX_LENGTH}자 이하로 입력해 주세요.`);
            return;
        }
        if (editForm.description.length > TASK_DESCRIPTION_MAX_LENGTH) {
            setEditErrorMessage(`설명은 ${TASK_DESCRIPTION_MAX_LENGTH}자 이하로 입력해 주세요.`);
            return;
        }
        if (editForm.startDate && editForm.endDate && editForm.startDate > editForm.endDate) {
            setEditErrorMessage("종료일은 시작일보다 빠를 수 없습니다.");
            return;
        }
        if (editForm.scheduleId && !editFilteredSchedules.some((item) => item.id === editForm.scheduleId)) {
            setEditErrorMessage("선택한 WBS에 연결된 Schedule을 확인해 주세요.");
            return;
        }

        setEditLoading(true);
        setEditErrorMessage("");
        setSuccessMessage("");
        try {
            // PUT 전체 수정 계약: 화면에서 수정하지 않는 담당자/진척률/정렬 순서는 기존 값 보존
            const currentTask = await getTask(editTaskId);
            const request: TaskUpdateRequest = {
                wbsId: editForm.wbsId,
                scheduleId: editForm.scheduleId || null,
                assigneeId: currentTask.assigneeId,
                taskName,
                description: editForm.description.trim() || null,
                priority: editForm.priority,
                progressRate: currentTask.progressRate,
                startDate: editForm.startDate || null,
                endDate: editForm.endDate || null,
                sortOrder: currentTask.sortOrder,
            };
            const updatedTask = await updateTask(editTaskId, request);
            setSelectedTask((previous) => previous?.id === updatedTask.id ? updatedTask : previous);
            setShowEditModal(false);
            setEditTaskId(null);
            setSuccessMessage("Task 정보가 수정되었습니다.");
            await fetchTasks(page, {
                keyword,
                status,
                priority,
                wbsId: selectedWbsId,
                scheduleId: selectedScheduleId,
            });
            if (showDetailModal) {
                setSelectedTask(await getTask(updatedTask.id));
            }
        } catch {
            setEditErrorMessage("Task 수정에 실패했습니다. 입력값과 서버 응답을 확인해 주세요.");
        } finally {
            setEditLoading(false);
        }
    };

    // Task 등록 입력값 검증
    const validateCreateForm = (): string | null => {
        if (!selectedProjectId) {
            return "프로젝트를 먼저 선택해 주세요.";
        }

        if (!createForm.wbsId) {
            return "WBS를 선택해 주세요.";
        }

        const selectedWbs = wbsOptions.find(
            (wbs) => wbs.id === createForm.wbsId,
        );

        if (!selectedWbs) {
            return "현재 프로젝트에 속한 유효한 WBS를 선택해 주세요.";
        }

        const trimmedTaskName = createForm.taskName.trim();

        if (!trimmedTaskName) {
            return "Task 이름을 입력해 주세요.";
        }

        if (trimmedTaskName.length > TASK_NAME_MAX_LENGTH) {
            return `Task 이름은 ${TASK_NAME_MAX_LENGTH}자 이하로 입력해 주세요.`;
        }

        if (createForm.description.length > TASK_DESCRIPTION_MAX_LENGTH) {
            return `설명은 ${TASK_DESCRIPTION_MAX_LENGTH}자 이하로 입력해 주세요.`;
        }

        if (
            createForm.startDate &&
            createForm.endDate &&
            createForm.startDate > createForm.endDate
        ) {
            return "종료일은 시작일보다 빠를 수 없습니다.";
        }

        if (createForm.scheduleId) {
            const selectedSchedule = createFilteredSchedules.find(
                (schedule) => schedule.id === createForm.scheduleId,
            );

            if (!selectedSchedule) {
                return "선택한 WBS에 연결된 Schedule을 확인해 주세요.";
            }
        }

        return null;
    };

    // Task 등록
    const handleCreateTask = async (
        event: FormEvent<HTMLFormElement>,
    ) => {
        event.preventDefault();

        if (createLoading) return;

        const validationMessage = validateCreateForm();

        if (validationMessage) {
            setCreateErrorMessage(validationMessage);
            return;
        }

        if (!selectedProjectId || !createForm.wbsId) return;

        const request: TaskCreateRequest = {
            wbsId: createForm.wbsId,
            scheduleId: createForm.scheduleId || null,
            assigneeId: null,
            taskName: createForm.taskName.trim(),
            description: createForm.description.trim() || null,
            status: "PLANNED",
            progressRate: 0,
            priority: createForm.priority,
            startDate: createForm.startDate || null,
            endDate: createForm.endDate || null,
            sortOrder: 0,
        };

        setCreateLoading(true);
        setCreateErrorMessage("");
        setSuccessMessage("");

        try {
            await createTask(selectedProjectId, request);

            setShowCreateModal(false);
            setCreateForm(INITIAL_CREATE_FORM);
            setSuccessMessage("Task가 등록되었습니다.");

            await fetchTasks(0, {
                keyword,
                status,
                priority,
                wbsId: selectedWbsId,
                scheduleId: selectedScheduleId,
            });
        } catch {
            setCreateErrorMessage(
                "Task 등록에 실패했습니다. 입력값과 서버 응답을 확인해 주세요.",
            );
        } finally {
            setCreateLoading(false);
        }
    };

    const handlePreviousPage = () => {
        void fetchTasks(page - 1, {
            keyword,
            status,
            priority,
            wbsId: selectedWbsId,
            scheduleId: selectedScheduleId,
        });
    };

    const handleNextPage = () => {
        void fetchTasks(page + 1, {
            keyword,
            status,
            priority,
            wbsId: selectedWbsId,
            scheduleId: selectedScheduleId,
        });
    };

    const tasks: Task[] = taskPage?.content ?? [];

    const currentPageInProgress = tasks.filter(
        (task) => task.status === "IN_PROGRESS",
    ).length;

    const currentPageCompleted = tasks.filter(
        (task) => task.status === "COMPLETED",
    ).length;

    const detailWbs = selectedTask
        ? wbsMap.get(selectedTask.wbsId)
        : undefined;

    const detailSchedule = selectedTask?.scheduleId
        ? schedules.find((item) => item.id === selectedTask.scheduleId)
        : undefined;

    return (
        <div className="page task-page">
            <div className="page__header">
                <div>
                    <div className="page__eyebrow">PROJECT MANAGEMENT</div>
                    <h1 className="page__title">Task Management</h1>
                    <p className="page__description">
                        프로젝트의 작업을 조회하고 진행 상태와 진척률을 관리합니다.
                    </p>
                </div>
            </div>

            {/* 프로젝트 선택 */}
            <section className="task-project-section">
                <div className="task-section-heading">
                    <div>
                        <h2>프로젝트 선택</h2>
                        <p>Task를 조회할 프로젝트를 선택하세요.</p>
                    </div>
                </div>

                <select
                    className="task-project-select"
                    value={selectedProjectId}
                    onChange={handleProjectChange}
                    disabled={projectLoading}
                >
                    <option value="">
                        {projectLoading
                            ? "프로젝트 조회 중..."
                            : "프로젝트를 선택하세요"}
                    </option>

                    {projects.map((project) => (
                        <option key={project.id} value={project.id}>
                            {project.projectCode} - {project.projectName}
                        </option>
                    ))}
                </select>
            </section>

            {!projectLoading && projects.length === 0 && !errorMessage && (
                <div className="task-state-message">
                    등록된 프로젝트가 없습니다.
                </div>
            )}

            {selectedProjectId !== "" && (
                <>
                    {/* Task 요약 */}
                    <section className="task-summary-grid">
                        <div className="task-summary-card">
                            <span className="task-summary-label">전체 Task</span>
                            <strong className="task-summary-value">
                                {taskPage?.totalElements ?? 0}
                            </strong>
                            <span className="task-summary-caption">
                                검색 결과 기준
                            </span>
                        </div>

                        <div className="task-summary-card">
                            <span className="task-summary-label">진행 중</span>
                            <strong className="task-summary-value">
                                {currentPageInProgress}
                            </strong>
                            <span className="task-summary-caption">
                                현재 페이지 기준
                            </span>
                        </div>

                        <div className="task-summary-card">
                            <span className="task-summary-label">완료</span>
                            <strong className="task-summary-value">
                                {currentPageCompleted}
                            </strong>
                            <span className="task-summary-caption">
                                현재 페이지 기준
                            </span>
                        </div>
                    </section>

                    {/* 검색 조건 */}
                    <section className="task-search-section">
                        <div className="task-search-header">
                            <div className="task-search-title">
                                <div className="task-search-icon">
                                    <svg
                                        width="20"
                                        height="20"
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        strokeWidth="2"
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        aria-hidden="true"
                                    >
                                        <circle cx="11" cy="11" r="8" />
                                        <path d="m21 21-4.35-4.35" />
                                    </svg>
                                </div>

                                <div>
                                    <span className="task-search-eyebrow">
                                        SEARCH FILTER
                                    </span>
                                    <h2>검색 조건</h2>
                                    <p>
                                        원하는 조건을 선택하여 Task를 검색할 수 있습니다.
                                    </p>
                                </div>
                            </div>

                            <span className="task-search-description">
                                최대 5개 검색 조건
                            </span>
                        </div>

                        <div className="task-search-grid">
                            <div className="task-search-field task-search-field--keyword">
                                <label htmlFor="task-keyword">Task 이름</label>
                                <input
                                    id="task-keyword"
                                    type="text"
                                    value={keyword}
                                    placeholder="Task 이름 또는 설명 입력"
                                    onChange={(event) =>
                                        setKeyword(event.target.value)
                                    }
                                    onKeyDown={(event) => {
                                        if (event.key === "Enter") handleSearch();
                                    }}
                                />
                            </div>

                            <div className="task-search-field">
                                <label htmlFor="task-wbs">WBS</label>
                                <select
                                    id="task-wbs"
                                    value={selectedWbsId}
                                    onChange={handleWbsChange}
                                    disabled={relationLoading}
                                >
                                    <option value="">전체 WBS</option>
                                    {wbsOptions.map((wbs) => (
                                        <option key={wbs.id} value={wbs.id}>
                                            {"　".repeat(Math.max(0, wbs.level - 1))}
                                            {wbs.wbsCode} {wbs.wbsName}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="task-search-field">
                                <label htmlFor="task-schedule">Schedule</label>
                                <select
                                    id="task-schedule"
                                    value={selectedScheduleId}
                                    onChange={(event) =>
                                        setSelectedScheduleId(
                                            event.target.value
                                                ? Number(event.target.value)
                                                : "",
                                        )
                                    }
                                    disabled={relationLoading}
                                >
                                    <option value="">전체 Schedule</option>
                                    {filteredSchedules.map((schedule) => (
                                        <option key={schedule.id} value={schedule.id}>
                                            {schedule.scheduleName}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="task-search-field">
                                <label htmlFor="task-status">상태</label>
                                <select
                                    id="task-status"
                                    value={status}
                                    onChange={(event) =>
                                        setStatus(
                                            event.target.value as TaskStatus | "",
                                        )
                                    }
                                >
                                    <option value="">전체 상태</option>
                                    {TASK_STATUSES.map((item) => (
                                        <option key={item} value={item}>
                                            {TASK_STATUS_LABELS[item]}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="task-search-field">
                                <label htmlFor="task-priority">우선순위</label>
                                <select
                                    id="task-priority"
                                    value={priority}
                                    onChange={(event) =>
                                        setPriority(
                                            event.target.value as TaskPriority | "",
                                        )
                                    }
                                >
                                    <option value="">전체 우선순위</option>
                                    {TASK_PRIORITIES.map((item) => (
                                        <option key={item} value={item}>
                                            {TASK_PRIORITY_LABELS[item]}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className="task-search-actions">
                            <div className="task-search-hint">
                                검색 조건을 입력한 후 검색 버튼을 눌러주세요.
                            </div>

                            <div className="task-search-buttons">
                                <button
                                    type="button"
                                    className="button button--secondary"
                                    onClick={handleReset}
                                    disabled={taskLoading}
                                >
                                    <span aria-hidden="true">↺</span>
                                    초기화
                                </button>

                                <button
                                    type="button"
                                    className="button button--primary"
                                    onClick={handleSearch}
                                    disabled={taskLoading || relationLoading}
                                >
                                    <span aria-hidden="true">⌕</span>
                                    검색
                                </button>
                            </div>
                        </div>
                    </section>

                    {/* Task 목록 */}
                    <section className="task-list-section">
                        <div className="task-section-heading task-list-heading">
                            <div>
                                <div className="task-list-title">
                                    <h2>Task 목록</h2>
                                    <span className="task-list-count">
                                        {taskPage?.totalElements ?? 0}
                                    </span>
                                </div>
                                <p>선택한 프로젝트의 작업 현황입니다.</p>
                            </div>

                            <button
                                type="button"
                                className="button button--primary"
                                onClick={handleOpenCreateModal}
                                disabled={relationLoading || wbsOptions.length === 0}
                                title={
                                    wbsOptions.length === 0
                                        ? "등록 가능한 WBS가 없습니다."
                                        : "새로운 Task 등록"
                                }
                            >
                                + Task 등록
                            </button>
                        </div>

                        {successMessage && (
                            <div className="task-create-success" role="status">
                                {successMessage}
                            </div>
                        )}

                        {taskLoading || relationLoading ? (
                            <div className="task-state-message">
                                Task 정보를 불러오는 중입니다.
                            </div>
                        ) : errorMessage ? (
                            <div className="task-state-message task-state-message--error">
                                {errorMessage}
                            </div>
                        ) : tasks.length === 0 ? (
                            <div className="task-state-message">
                                조회된 Task가 없습니다.
                            </div>
                        ) : (
                            <div className="task-table-wrapper">
                                <table className="task-table">
                                    <thead>
                                        <tr>
                                            <th>Task 이름</th>
                                            <th>WBS</th>
                                            <th>Schedule</th>
                                            <th>담당자</th>
                                            <th>우선순위</th>
                                            <th>상태</th>
                                            <th>진척률</th>
                                            <th>시작일</th>
                                            <th>종료일</th>
                                            <th>관리</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {tasks.map((task) => {
                                            const wbs = wbsMap.get(task.wbsId);
                                            const schedule = schedules.find(
                                                (item) => item.id === task.scheduleId,
                                            );

                                            return (
                                                <tr key={task.id}>
                                                    <td className="task-name-cell">
                                                        <button
                                                            type="button"
                                                            className="task-name-link"
                                                            onClick={() =>
                                                                void handleOpenDetailModal(task.id)
                                                            }
                                                            title="Task 상세 정보 조회"
                                                        >
                                                            {task.taskName}
                                                        </button>

                                                        {task.description && (
                                                            <span>{task.description}</span>
                                                        )}
                                                    </td>

                                                    <td className="task-wbs-cell">
                                                        {wbs ? (
                                                            <div>
                                                                <strong>
                                                                    {wbs.wbsCode} {wbs.wbsName}
                                                                </strong>
                                                                <span title={wbs.wbsPath}>
                                                                    {wbs.wbsPath}
                                                                </span>
                                                            </div>
                                                        ) : (
                                                            <span>WBS #{task.wbsId}</span>
                                                        )}
                                                    </td>

                                                    <td>
                                                        {task.scheduleId
                                                            ? schedule?.scheduleName ??
                                                              `Schedule #${task.scheduleId}`
                                                            : "-"}
                                                    </td>

                                                    <td>{task.assigneeName ?? "-"}</td>

                                                    <td>
                                                        <span
                                                            className={getPriorityClassName(
                                                                task.priority,
                                                            )}
                                                        >
                                                            {TASK_PRIORITY_LABELS[task.priority]}
                                                        </span>
                                                    </td>

                                                    <td>
                                                        <span
                                                            className={getStatusClassName(
                                                                task.status,
                                                            )}
                                                        >
                                                            {TASK_STATUS_LABELS[task.status]}
                                                        </span>
                                                    </td>

                                                    <td>
                                                        <div className="task-progress">
                                                            <div className="task-progress__track">
                                                                <div
                                                                    className="task-progress__bar"
                                                                    style={{
                                                                        width: `${task.progressRate}%`,
                                                                    }}
                                                                />
                                                            </div>
                                                            <span>{task.progressRate}%</span>
                                                        </div>
                                                    </td>

                                                    <td>{formatDate(task.startDate)}</td>
                                                    <td>{formatDate(task.endDate)}</td>
                                                    <td>
                                                        <button
                                                            type="button"
                                                            className="button button--secondary task-row-edit-button"
                                                            onClick={() => void handleOpenEditModal(task.id)}
                                                            disabled={editLoading}
                                                        >
                                                            수정
                                                        </button>
                                                    </td>
                                                </tr>
                                            );
                                        })}
                                    </tbody>
                                </table>
                            </div>
                        )}

                        {taskPage && taskPage.totalPages > 1 && (
                            <div className="task-pagination">
                                <button
                                    type="button"
                                    className="button button--secondary"
                                    disabled={taskPage.first || taskLoading}
                                    onClick={handlePreviousPage}
                                >
                                    이전
                                </button>

                                <span>
                                    {taskPage.number + 1} / {taskPage.totalPages}
                                </span>

                                <button
                                    type="button"
                                    className="button button--secondary"
                                    disabled={taskPage.last || taskLoading}
                                    onClick={handleNextPage}
                                >
                                    다음
                                </button>
                            </div>
                        )}
                    </section>
                </>
            )}

            {/* Task 등록 모달 */}
            {showCreateModal && (
                <div className="task-modal-overlay">
                    <section
                        className="task-create-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="task-create-modal-title"
                    >
                        <div className="task-create-modal__header">
                            <div>
                                <span className="task-search-eyebrow">NEW TASK</span>
                                <h2 id="task-create-modal-title">Task 등록</h2>
                                <p>새로운 작업 정보를 입력해 주세요.</p>
                            </div>

                            <button
                                type="button"
                                className="task-modal-close"
                                onClick={handleCloseCreateModal}
                                disabled={createLoading}
                                aria-label="등록 모달 닫기"
                            >
                                ×
                            </button>
                        </div>

                        <form
                            className="task-create-form"
                            onSubmit={handleCreateTask}
                            noValidate
                        >
                            <div className="task-create-grid">
                                <div className="task-search-field">
                                    <label htmlFor="create-task-wbs">
                                        WBS <span className="required">*</span>
                                    </label>
                                    <select
                                        id="create-task-wbs"
                                        name="wbsId"
                                        value={createForm.wbsId}
                                        onChange={handleCreateWbsChange}
                                        required
                                        disabled={relationLoading || createLoading}
                                    >
                                        <option value="">WBS를 선택하세요</option>
                                        {wbsOptions.map((wbs) => (
                                            <option key={wbs.id} value={wbs.id}>
                                                {wbs.wbsPath}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="task-search-field">
                                    <label htmlFor="create-task-schedule">
                                        Schedule
                                        <span className="optional-label">선택 사항</span>
                                    </label>
                                    <select
                                        id="create-task-schedule"
                                        name="scheduleId"
                                        value={createForm.scheduleId}
                                        onChange={handleCreateScheduleChange}
                                        disabled={
                                            !createForm.wbsId ||
                                            relationLoading ||
                                            createLoading ||
                                            createFilteredSchedules.length === 0
                                        }
                                    >
                                        <option value="">
                                            {!createForm.wbsId
                                                ? "먼저 WBS를 선택하세요"
                                                : createFilteredSchedules.length === 0
                                                  ? "연결 가능한 Schedule이 없습니다."
                                                  : "Schedule을 선택하지 않음"}
                                        </option>
                                        {createFilteredSchedules.map((schedule) => (
                                            <option
                                                key={schedule.id}
                                                value={schedule.id}
                                            >
                                                {schedule.scheduleName}
                                            </option>
                                        ))}
                                    </select>
                                    <span className="task-create-field-hint">
                                        선택하지 않으면 Schedule과 연결하지 않습니다.
                                    </span>
                                </div>

                                <div className="task-search-field task-create-field--full">
                                    <label htmlFor="create-task-name">
                                        Task 이름 <span className="required">*</span>
                                    </label>
                                    <input
                                        id="create-task-name"
                                        name="taskName"
                                        type="text"
                                        value={createForm.taskName}
                                        onChange={handleCreateFormChange}
                                        placeholder="Task 이름을 입력하세요."
                                        maxLength={TASK_NAME_MAX_LENGTH}
                                        required
                                        disabled={createLoading}
                                    />
                                    <span className="task-create-field-hint">
                                        {createForm.taskName.length} / {TASK_NAME_MAX_LENGTH}자
                                    </span>
                                </div>

                                <div className="task-search-field">
                                    <label htmlFor="create-task-priority">우선순위</label>
                                    <select
                                        id="create-task-priority"
                                        name="priority"
                                        value={createForm.priority}
                                        onChange={handleCreateFormChange}
                                        disabled={createLoading}
                                    >
                                        {TASK_PRIORITIES.map((item) => (
                                            <option key={item} value={item}>
                                                {TASK_PRIORITY_LABELS[item]}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="task-search-field">
                                    <label htmlFor="create-task-assignee">
                                        담당자
                                        <span className="optional-label">선택 사항</span>
                                    </label>
                                    <input
                                        id="create-task-assignee"
                                        type="text"
                                        value="미배정"
                                        disabled
                                        readOnly
                                    />
                                    <span className="task-create-field-hint">
                                        담당자 배정 기능은 추후 API 연동 시 적용합니다.
                                    </span>
                                </div>

                                <div className="task-search-field">
                                    <label htmlFor="create-task-start-date">시작일</label>
                                    <input
                                        id="create-task-start-date"
                                        name="startDate"
                                        type="date"
                                        value={createForm.startDate}
                                        max={createForm.endDate || undefined}
                                        onChange={handleCreateFormChange}
                                        disabled={createLoading}
                                    />
                                </div>

                                <div className="task-search-field">
                                    <label htmlFor="create-task-end-date">종료일</label>
                                    <input
                                        id="create-task-end-date"
                                        name="endDate"
                                        type="date"
                                        value={createForm.endDate}
                                        min={createForm.startDate || undefined}
                                        onChange={handleCreateFormChange}
                                        disabled={createLoading}
                                    />
                                </div>

                                <div className="task-search-field task-create-field--full">
                                    <label htmlFor="create-task-description">설명</label>
                                    <textarea
                                        id="create-task-description"
                                        name="description"
                                        value={createForm.description}
                                        onChange={handleCreateFormChange}
                                        placeholder="Task에 대한 상세 설명을 입력하세요."
                                        rows={4}
                                        maxLength={TASK_DESCRIPTION_MAX_LENGTH}
                                        disabled={createLoading}
                                    />
                                    <span className="task-create-field-hint">
                                        {createForm.description.length} / {TASK_DESCRIPTION_MAX_LENGTH}자
                                    </span>
                                </div>
                            </div>

                            {createErrorMessage && (
                                <div
                                    className="task-state-message task-state-message--error"
                                    role="alert"
                                >
                                    {createErrorMessage}
                                </div>
                            )}

                            <div className="task-create-modal__footer">
                                <button
                                    type="button"
                                    className="button button--secondary"
                                    onClick={handleCloseCreateModal}
                                    disabled={createLoading}
                                >
                                    취소
                                </button>

                                <button
                                    type="submit"
                                    className="button button--primary"
                                    disabled={createLoading || relationLoading}
                                >
                                    {createLoading ? "등록 중..." : "Task 등록"}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}

            {/* Task 수정 모달 */}
            {showEditModal && (
                <div className="task-modal-overlay">
                    <section
                        className="task-create-modal task-edit-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="task-edit-modal-title"
                    >
                        <div className="task-create-modal__header">
                            <div>
                                <span className="task-search-eyebrow">EDIT TASK</span>
                                <h2 id="task-edit-modal-title">Task 수정</h2>
                                <p>변경할 작업 정보를 입력해 주세요.</p>
                            </div>
                            <button
                                type="button"
                                className="task-modal-close"
                                onClick={handleCloseEditModal}
                                disabled={editLoading}
                                aria-label="수정 모달 닫기"
                            >×</button>
                        </div>

                        {editLoading && !editForm.wbsId ? (
                            <div className="task-detail-modal__body">
                                <div className="task-state-message">Task 정보를 불러오는 중입니다.</div>
                            </div>
                        ) : (
                            <form className="task-create-form" onSubmit={handleUpdateTask} noValidate>
                                <div className="task-create-grid">
                                    <div className="task-search-field">
                                        <label htmlFor="edit-task-wbs">WBS <span className="required">*</span></label>
                                        <select
                                            id="edit-task-wbs"
                                            name="wbsId"
                                            value={editForm.wbsId}
                                            onChange={handleEditWbsChange}
                                            disabled={editLoading || relationLoading}
                                            required
                                        >
                                            <option value="">WBS를 선택하세요</option>
                                            {wbsOptions.map((wbs) => (
                                                <option key={wbs.id} value={wbs.id}>{wbs.wbsPath}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="task-search-field">
                                        <label htmlFor="edit-task-schedule">Schedule <span className="optional-label">선택 사항</span></label>
                                        <select
                                            id="edit-task-schedule"
                                            name="scheduleId"
                                            value={editForm.scheduleId}
                                            onChange={handleEditScheduleChange}
                                            disabled={editLoading || !editForm.wbsId || editFilteredSchedules.length === 0}
                                        >
                                            <option value="">Schedule을 선택하지 않음</option>
                                            {editFilteredSchedules.map((schedule) => (
                                                <option key={schedule.id} value={schedule.id}>{schedule.scheduleName}</option>
                                            ))}
                                        </select>
                                        <span className="task-create-field-hint">선택하지 않으면 Schedule 연결을 해제합니다.</span>
                                    </div>
                                    <div className="task-search-field task-create-field--full">
                                        <label htmlFor="edit-task-name">Task 이름 <span className="required">*</span></label>
                                        <input
                                            id="edit-task-name"
                                            name="taskName"
                                            type="text"
                                            value={editForm.taskName}
                                            onChange={handleEditFormChange}
                                            maxLength={TASK_NAME_MAX_LENGTH}
                                            disabled={editLoading}
                                            required
                                        />
                                        <span className="task-create-field-hint">{editForm.taskName.length} / {TASK_NAME_MAX_LENGTH}자</span>
                                    </div>
                                    <div className="task-search-field">
                                        <label htmlFor="edit-task-priority">우선순위</label>
                                        <select
                                            id="edit-task-priority"
                                            name="priority"
                                            value={editForm.priority}
                                            onChange={handleEditFormChange}
                                            disabled={editLoading}
                                        >
                                            {TASK_PRIORITIES.map((item) => (
                                                <option key={item} value={item}>{TASK_PRIORITY_LABELS[item]}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <div className="task-search-field">
                                        <label>담당자</label>
                                        <input type="text" value="기존 담당자 유지" disabled readOnly />
                                    </div>
                                    <div className="task-search-field">
                                        <label htmlFor="edit-task-start-date">시작일</label>
                                        <input
                                            id="edit-task-start-date"
                                            name="startDate"
                                            type="date"
                                            value={editForm.startDate}
                                            max={editForm.endDate || undefined}
                                            onChange={handleEditFormChange}
                                            disabled={editLoading}
                                        />
                                    </div>
                                    <div className="task-search-field">
                                        <label htmlFor="edit-task-end-date">종료일</label>
                                        <input
                                            id="edit-task-end-date"
                                            name="endDate"
                                            type="date"
                                            value={editForm.endDate}
                                            min={editForm.startDate || undefined}
                                            onChange={handleEditFormChange}
                                            disabled={editLoading}
                                        />
                                    </div>
                                    <div className="task-search-field task-create-field--full">
                                        <label htmlFor="edit-task-description">설명</label>
                                        <textarea
                                            id="edit-task-description"
                                            name="description"
                                            value={editForm.description}
                                            onChange={handleEditFormChange}
                                            rows={4}
                                            maxLength={TASK_DESCRIPTION_MAX_LENGTH}
                                            disabled={editLoading}
                                        />
                                        <span className="task-create-field-hint">{editForm.description.length} / {TASK_DESCRIPTION_MAX_LENGTH}자</span>
                                    </div>
                                </div>
                                <div className="task-edit-preserved-note">
                                    상태, 진척률, 담당자 및 정렬 순서는 기존 값을 유지합니다.
                                </div>
                                {editErrorMessage && (
                                    <div className="task-state-message task-state-message--error" role="alert">{editErrorMessage}</div>
                                )}
                                <div className="task-create-modal__footer">
                                    <button type="button" className="button button--secondary" onClick={handleCloseEditModal} disabled={editLoading}>취소</button>
                                    <button type="submit" className="button button--primary" disabled={editLoading || relationLoading || !editForm.wbsId}>
                                        {editLoading ? "저장 중..." : "변경 사항 저장"}
                                    </button>
                                </div>
                            </form>
                        )}
                    </section>
                </div>
            )}

            {/* Task 상세 조회 모달 */}
            {showDetailModal && (
                <div
                    className="task-modal-overlay"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) {
                            handleCloseDetailModal();
                        }
                    }}
                >
                    <section
                        className="task-detail-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="task-detail-modal-title"
                    >
                        <div className="task-create-modal__header">
                            <div>
                                <span className="task-search-eyebrow">
                                    TASK DETAIL
                                </span>
                                <h2 id="task-detail-modal-title">
                                    Task 상세 정보
                                </h2>
                                <p>
                                    등록된 작업의 상세 정보를 확인합니다.
                                </p>
                            </div>

                            <button
                                type="button"
                                className="task-modal-close"
                                onClick={handleCloseDetailModal}
                                disabled={detailActionLoading}
                                aria-label="상세 모달 닫기"
                            >
                                ×
                            </button>
                        </div>

                        <div className="task-detail-modal__body">
                            {detailLoading ? (
                                <div className="task-state-message">
                                    Task 상세 정보를 불러오는 중입니다.
                                </div>
                            ) : detailErrorMessage ? (
                                <div
                                    className="task-state-message task-state-message--error"
                                    role="alert"
                                >
                                    {detailErrorMessage}
                                </div>
                            ) : selectedTask ? (
                                <>
                                    <div className="task-detail-title">
                                        <div>
                                            <span className="task-detail-label">
                                                TASK NAME
                                            </span>
                                            <h3>{selectedTask.taskName}</h3>
                                        </div>

                                        <span
                                            className={getStatusClassName(
                                                selectedTask.status,
                                            )}
                                        >
                                            {TASK_STATUS_LABELS[selectedTask.status]}
                                        </span>
                                    </div>

                                    <div className="task-detail-progress">
                                        <div className="task-detail-progress__heading">
                                            <strong>진척률</strong>
                                            <span>{selectedTask.progressRate}%</span>
                                        </div>

                                        <div className="task-progress__track">
                                            <div
                                                className="task-progress__bar"
                                                style={{
                                                    width: `${selectedTask.progressRate}%`,
                                                }}
                                            />
                                        </div>
                                    </div>

                                    <div className="task-detail-grid">
                                        <div className="task-detail-item">
                                            <label htmlFor="detail-task-status">상태 변경</label>
                                            <select
                                                id="detail-task-status"
                                                value={detailStatus}
                                                onChange={(event) =>
                                                    setDetailStatus(event.target.value as TaskStatus)
                                                }
                                                disabled={detailActionLoading}
                                            >
                                                {TASK_STATUSES.map((item) => (
                                                    <option key={item} value={item}>
                                                        {TASK_STATUS_LABELS[item]}
                                                    </option>
                                                ))}
                                            </select>
                                            <button
                                                type="button"
                                                className="button button--secondary"
                                                onClick={() => void handleChangeDetailStatus()}
                                                disabled={detailActionLoading || detailStatus === selectedTask.status}
                                            >
                                                상태 저장
                                            </button>
                                        </div>

                                        <div className="task-detail-item">
                                            <label htmlFor="detail-task-progress">진척률 변경 (%)</label>
                                            <input
                                                id="detail-task-progress"
                                                type="number"
                                                min="0"
                                                max="100"
                                                step="1"
                                                value={detailProgress}
                                                onChange={(event) => setDetailProgress(event.target.value)}
                                                disabled={detailActionLoading}
                                            />
                                            <button
                                                type="button"
                                                className="button button--secondary"
                                                onClick={() => void handleUpdateDetailProgress()}
                                                disabled={
                                                    detailActionLoading ||
                                                    detailProgress === String(selectedTask.progressRate)
                                                }
                                            >
                                                진척률 저장
                                            </button>
                                        </div>
                                    </div>

                                    {detailActionErrorMessage && (
                                        <div
                                            className="task-state-message task-state-message--error"
                                            role="alert"
                                        >
                                            {detailActionErrorMessage}
                                        </div>
                                    )}

                                    {detailActionSuccessMessage && (
                                        <div className="task-create-success" role="status">
                                            {detailActionSuccessMessage}
                                        </div>
                                    )}

                                    <p className="task-create-field-hint">
                                        상태와 진척률은 백엔드 정책에 따라 함께 변경될 수 있습니다.
                                    </p>

                                    <div className="task-detail-grid">
                                        <div className="task-detail-item">
                                            <span>Task ID</span>
                                            <strong>{selectedTask.id}</strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>프로젝트 ID</span>
                                            <strong>{selectedTask.projectId}</strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>WBS</span>
                                            <strong>
                                                {detailWbs
                                                    ? `${detailWbs.wbsCode} ${detailWbs.wbsName}`
                                                    : `WBS #${selectedTask.wbsId}`}
                                            </strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>Schedule</span>
                                            <strong>
                                                {selectedTask.scheduleId
                                                    ? detailSchedule?.scheduleName ??
                                                      `Schedule #${selectedTask.scheduleId}`
                                                    : "-"}
                                            </strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>담당자</span>
                                            <strong>
                                                {selectedTask.assigneeName ?? "미배정"}
                                            </strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>우선순위</span>
                                            <strong>
                                                <span
                                                    className={getPriorityClassName(
                                                        selectedTask.priority,
                                                    )}
                                                >
                                                    {TASK_PRIORITY_LABELS[selectedTask.priority]}
                                                </span>
                                            </strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>시작일</span>
                                            <strong>
                                                {formatDate(selectedTask.startDate)}
                                            </strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>종료일</span>
                                            <strong>
                                                {formatDate(selectedTask.endDate)}
                                            </strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>정렬 순서</span>
                                            <strong>{selectedTask.sortOrder}</strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>등록일</span>
                                            <strong>
                                                {formatDateTime(selectedTask.createdAt)}
                                            </strong>
                                        </div>

                                        <div className="task-detail-item">
                                            <span>최종 수정일</span>
                                            <strong>
                                                {formatDateTime(selectedTask.updatedAt)}
                                            </strong>
                                        </div>
                                    </div>

                                    <div className="task-detail-description">
                                        <span>상세 설명</span>
                                        <p>
                                            {selectedTask.description?.trim() ||
                                                "등록된 설명이 없습니다."}
                                        </p>
                                    </div>
                                </>
                            ) : null}
                        </div>

                        <div className="task-create-modal__footer">
                            <button
                                type="button"
                                className="button button--secondary"
                                onClick={handleCloseDetailModal}
                                disabled={detailActionLoading}
                            >
                                닫기
                            </button>
                        </div>
                    </section>
                </div>
            )}
        </div>
    );
};

export default TaskPage;
