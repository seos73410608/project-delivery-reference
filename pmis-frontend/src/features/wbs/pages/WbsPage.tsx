import { useState } from "react";

import WbsToolbar from "@/features/wbs/components/WbsToolbar";
import WbsTree from "@/features/wbs/components/WbsTree";
import WbsDetail from "@/features/wbs/components/WbsDetail";

import type {
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


const mockWbsTree: WbsTreeResponse[] = [
    {
        id: 1,
        projectId: 2,
        parentId: null,
        wbsCode: "1",
        wbsName: "프로젝트 관리",
        level: 1,
        sortOrder: 1,
        status: "IN_PROGRESS",
        description: "프로젝트 전체 관리",
        createdAt: "2026-08-11T09:00:00",
        updatedAt: "2026-08-13T09:00:00",
        children: [
            {
                id: 2,
                projectId: 2,
                parentId: 1,
                wbsCode: "1.1",
                wbsName: "프로젝트 계획",
                level: 2,
                sortOrder: 1,
                status: "COMPLETED",
                description: "프로젝트 계획 수립",
                createdAt: "2026-08-11T09:10:00",
                updatedAt: "2026-08-12T10:00:00",
                children: [
                    {
                        id: 3,
                        projectId: 2,
                        parentId: 2,
                        wbsCode: "1.1.1",
                        wbsName: "상세 일정 계획",
                        level: 3,
                        sortOrder: 1,
                        status: "IN_PROGRESS",
                        description: "프로젝트 상세 일정 수립",
                        createdAt: "2026-08-11T09:20:00",
                        updatedAt: "2026-08-13T10:00:00",
                        children: [],
                    },
                    {
                        id: 4,
                        projectId: 2,
                        parentId: 2,
                        wbsCode: "1.1.2",
                        wbsName: "품질 계획",
                        level: 3,
                        sortOrder: 2,
                        status: "PLANNED",
                        description: "프로젝트 품질 관리 계획",
                        createdAt: "2026-08-11T09:30:00",
                        updatedAt: "2026-08-11T09:30:00",
                        children: [],
                    },
                ],
            },
            {
                id: 5,
                projectId: 2,
                parentId: 1,
                wbsCode: "1.2",
                wbsName: "프로젝트 수행",
                level: 2,
                sortOrder: 2,
                status: "IN_PROGRESS",
                description: "프로젝트 수행 및 관리",
                createdAt: "2026-08-11T10:00:00",
                updatedAt: "2026-08-13T09:30:00",
                children: [],
            },
        ],
    },
    {
        id: 6,
        projectId: 2,
        parentId: null,
        wbsCode: "2",
        wbsName: "구축 관리",
        level: 1,
        sortOrder: 2,
        status: "PLANNED",
        description: "시스템 구축 관리",
        createdAt: "2026-08-11T11:00:00",
        updatedAt: "2026-08-11T11:00:00",
        children: [
            {
                id: 7,
                projectId: 2,
                parentId: 6,
                wbsCode: "2.1",
                wbsName: "서버 구축",
                level: 2,
                sortOrder: 1,
                status: "IN_PROGRESS",
                description: "서버 설치 및 구성",
                createdAt: "2026-08-11T11:10:00",
                updatedAt: "2026-08-13T08:30:00",
                children: [],
            },
        ],
    },
];


function WbsPage() {
    const [keyword, setKeyword] = useState("");

    const [status, setStatus] =
        useState<WbsStatus | "">("");

    const [selectedWbs, setSelectedWbs] =
        useState<WbsTreeResponse | null>(mockWbsTree[0]);


    const handleCreate = () => {
        window.alert(
            "WBS 생성 기능은 현재 Mock 단계입니다.",
        );
    };


    const handleEdit = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} ${selectedWbs.wbsName} 수정 기능은 현재 Mock 단계입니다.`,
        );
    };


    const handleChangeStatus = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} 상태 변경 기능은 현재 Mock 단계입니다.`,
        );
    };


    const handleDelete = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} ${selectedWbs.wbsName} 삭제 기능은 현재 Mock 단계입니다.`,
        );
    };


    return (
        <div>
            {/* Page Header */}
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
                    WBS Management
                </h1>

                <p
                    style={{
                        marginTop: "8px",
                        color: "#666666",
                    }}
                >
                    Work Breakdown Structure management
                </p>
            </div>


            {/* Toolbar */}
            <div
                style={{
                    marginBottom: "16px",
                }}
            >
                <WbsToolbar
                    keyword={keyword}
                    status={status}
                    onKeywordChange={setKeyword}
                    onStatusChange={setStatus}
                    onCreate={handleCreate}
                />
            </div>


            {/* WBS Tree / Detail */}
            <div
                style={{
                    display: "grid",
                    gridTemplateColumns: "1.5fr 1fr",
                    gap: "16px",
                    alignItems: "start",
                }}
            >
                {/* WBS Tree */}
                <WbsTree
                    wbsList={mockWbsTree}
                    selectedWbsId={selectedWbs?.id ?? null}
                    onSelect={setSelectedWbs}
                    keyword={keyword}
                    status={status}
                />


                {/* WBS Detail */}
                <WbsDetail
                    wbs={selectedWbs}
                    onEdit={handleEdit}
                    onChangeStatus={handleChangeStatus}
                    onDelete={handleDelete}
                />
            </div>
        </div>
    );
}


export default WbsPage;