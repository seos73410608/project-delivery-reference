import { useEffect, useState } from "react";

import WbsToolbar from "@/features/wbs/components/WbsToolbar";
import WbsTree from "@/features/wbs/components/WbsTree";
import WbsDetail from "@/features/wbs/components/WbsDetail";

import { getWbsTree } from "@/features/wbs/api/wbsApi";

import type {
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


const PROJECT_ID = 2;


function WbsPage() {
    const [keyword, setKeyword] = useState("");

    const [status, setStatus] =
        useState<WbsStatus | "">("");

    const [wbsTree, setWbsTree] =
        useState<WbsTreeResponse[]>([]);

    const [selectedWbs, setSelectedWbs] =
        useState<WbsTreeResponse | null>(null);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState<string | null>(null);


    useEffect(() => {
        const loadWbsTree = async () => {
            try {
                setLoading(true);
                setError(null);

                const data = await getWbsTree(PROJECT_ID);

                setWbsTree(data);

                if (data.length > 0) {
                    setSelectedWbs(data[0]);
                } else {
                    setSelectedWbs(null);
                }
            } catch (err) {
                console.error("Failed to load WBS tree:", err);

                setError(
                    "WBS 정보를 불러오지 못했습니다.",
                );

                setWbsTree([]);
                setSelectedWbs(null);
            } finally {
                setLoading(false);
            }
        };

        void loadWbsTree();
    }, []);


    const handleCreate = () => {
        window.alert(
            "WBS 생성 기능은 다음 단계에서 Backend API와 연동합니다.",
        );
    };


    const handleEdit = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} ${selectedWbs.wbsName} 수정 기능은 다음 단계에서 Backend API와 연동합니다.`,
        );
    };


    const handleChangeStatus = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} 상태 변경 기능은 다음 단계에서 Backend API와 연동합니다.`,
        );
    };


    const handleDelete = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} ${selectedWbs.wbsName} 삭제 기능은 다음 단계에서 Backend API와 연동합니다.`,
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


            {/* Loading */}
            {loading && (
                <div
                    style={{
                        padding: "40px",
                        textAlign: "center",
                        backgroundColor: "#ffffff",
                        border: "1px solid #dddddd",
                        borderRadius: "6px",
                    }}
                >
                    WBS 정보를 불러오는 중입니다...
                </div>
            )}


            {/* Error */}
            {!loading && error && (
                <div
                    style={{
                        padding: "20px",
                        marginBottom: "16px",
                        backgroundColor: "#fff3f3",
                        border: "1px solid #f0b8b8",
                        borderRadius: "6px",
                        color: "#c62828",
                    }}
                >
                    {error}
                </div>
            )}


            {/* Empty */}
            {!loading &&
                !error &&
                wbsTree.length === 0 && (
                    <div
                        style={{
                            padding: "40px",
                            textAlign: "center",
                            backgroundColor: "#ffffff",
                            border: "1px solid #dddddd",
                            borderRadius: "6px",
                            color: "#666666",
                        }}
                    >
                        등록된 WBS가 없습니다.
                    </div>
                )}


            {/* WBS Tree / Detail */}
            {!loading &&
                !error &&
                wbsTree.length > 0 && (
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
                            wbsList={wbsTree}
                            selectedWbsId={
                                selectedWbs?.id ?? null
                            }
                            onSelect={setSelectedWbs}
                            keyword={keyword}
                            status={status}
                        />


                        {/* WBS Detail */}
                        <WbsDetail
                            wbs={selectedWbs}
                            onEdit={handleEdit}
                            onChangeStatus={
                                handleChangeStatus
                            }
                            onDelete={handleDelete}
                        />
                    </div>
                )}
        </div>
    );
}


export default WbsPage;