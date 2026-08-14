import { useEffect, useState } from "react";

import WbsToolbar from "@/features/wbs/components/WbsToolbar";
import WbsTree from "@/features/wbs/components/WbsTree";
import WbsDetail from "@/features/wbs/components/WbsDetail";

import {
    getWbsTree,
    searchWbs,
} from "@/features/wbs/api/wbsApi";

import type {
    WbsResponse,
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


const PROJECT_ID = 2;


/**
 * 검색 결과로 반환된 Flat WBS를
 * 기존 WbsTree에서 사용할 수 있는 형태로 변환한다.
 *
 * 검색 API는 children을 반환하지 않기 때문에
 * 검색 결과 자체를 각각 독립된 Tree Node로 표현한다.
 */
const convertSearchResultsToTree = (
    wbsList: WbsResponse[],
): WbsTreeResponse[] => {
    return wbsList.map((wbs) => ({
        ...wbs,
        children: [],
    }));
};


function WbsPage() {
    const [keyword, setKeyword] = useState("");

    const [status, setStatus] =
        useState<WbsStatus | "">("");


    /**
     * 현재 화면에 표시할 WBS Tree
     *
     * 기본 조회:
     * GET /api/projects/{projectId}/wbs/tree
     *
     * 검색 조회:
     * GET /api/wbs
     */
    const [wbsTree, setWbsTree] =
        useState<WbsTreeResponse[]>([]);


    /**
     * 현재 선택된 WBS
     */
    const [selectedWbs, setSelectedWbs] =
        useState<WbsTreeResponse | null>(null);


    /**
     * API Loading 상태
     */
    const [loading, setLoading] =
        useState(true);


    /**
     * API Error 상태
     */
    const [error, setError] =
        useState<string | null>(null);


    /**
     * WBS 조회
     *
     * 검색 조건이 없으면 Tree API를 호출한다.
     *
     * GET /api/projects/{projectId}/wbs/tree
     *
     * 검색 조건이 있으면 Search API를 호출한다.
     *
     * GET /api/wbs
     */
    useEffect(() => {
        const timer = window.setTimeout(() => {
            const loadWbs = async () => {
                try {
                    setLoading(true);
                    setError(null);

                    /**
                     * 검색 조건이 없는 경우
                     *
                     * 기존 WBS Tree API 사용
                     */
                    if (
                        keyword.trim() === "" &&
                        status === ""
                    ) {
                        const data =
                            await getWbsTree(PROJECT_ID);

                        setWbsTree(data);

                        if (data.length > 0) {
                            setSelectedWbs(data[0]);
                        } else {
                            setSelectedWbs(null);
                        }

                        return;
                    }


                    /**
                     * 검색 조건이 있는 경우
                     *
                     * GET /api/wbs
                     */
                    const response = await searchWbs({
                        projectId: PROJECT_ID,
                        keyword:
                            keyword.trim() || undefined,
                        status:
                            status || undefined,
                        page: 0,
                        size: 20,
                    });


                    /**
                     * 검색 API는 Flat List를 반환하므로
                     * 기존 WbsTree에서 사용할 수 있도록
                     * children을 추가한다.
                     */
                    const tree =
                        convertSearchResultsToTree(
                            response.content,
                        );

                    setWbsTree(tree);

                    if (tree.length > 0) {
                        setSelectedWbs(tree[0]);
                    } else {
                        setSelectedWbs(null);
                    }
                } catch (err) {
                    console.error(
                        "Failed to load WBS:",
                        err,
                    );

                    setError(
                        "WBS 정보를 불러오지 못했습니다.",
                    );

                    setWbsTree([]);
                    setSelectedWbs(null);
                } finally {
                    setLoading(false);
                }
            };

            void loadWbs();
        }, 300);


        /**
         * 검색어 입력 시 너무 많은 API 요청이
         * 발생하지 않도록 debounce 처리한다.
         */
        return () => {
            window.clearTimeout(timer);
        };
    }, [keyword, status]);


    /**
     * WBS 생성
     *
     * 아직 Mock 단계
     */
    const handleCreate = () => {
        window.alert(
            "WBS 생성 기능은 다음 단계에서 Backend API와 연동합니다.",
        );
    };


    /**
     * WBS 수정
     *
     * 아직 Mock 단계
     */
    const handleEdit = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} ${selectedWbs.wbsName} 수정 기능은 다음 단계에서 Backend API와 연동합니다.`,
        );
    };


    /**
     * WBS 상태 변경
     *
     * 아직 Mock 단계
     */
    const handleChangeStatus = () => {
        if (!selectedWbs) {
            return;
        }

        window.alert(
            `${selectedWbs.wbsCode} 상태 변경 기능은 다음 단계에서 Backend API와 연동합니다.`,
        );
    };


    /**
     * WBS 삭제
     *
     * 아직 Mock 단계
     */
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
                        검색 조건에 해당하는 WBS가 없습니다.
                    </div>
                )}


            {/* WBS Tree / Detail */}
            {!loading &&
                !error &&
                wbsTree.length > 0 && (
                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns:
                                "1.5fr 1fr",
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