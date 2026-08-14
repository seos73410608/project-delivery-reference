import { useEffect, useState } from "react";

import WbsToolbar from "@/features/wbs/components/WbsToolbar";
import WbsTree from "@/features/wbs/components/WbsTree";
import WbsDetail from "@/features/wbs/components/WbsDetail";
import WbsForm from "@/features/wbs/components/WbsForm";

import {
    createWbs,
    getWbsTree,
    searchWbs,
} from "@/features/wbs/api/wbsApi";

import type {
    WbsCreateRequest,
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
 * 검색 결과 각각을 독립된 Tree Node로 표현한다.
 */
const convertSearchResultsToTree = (
    wbsList: WbsResponse[],
): WbsTreeResponse[] => {
    return wbsList.map((wbs) => ({
        ...wbs,
        children: [],
    }));
};


/**
 * WBS Tree에서 특정 ID의 WBS를 찾는다.
 */
const findWbsById = (
    nodes: WbsTreeResponse[],
    targetId: number,
): WbsTreeResponse | null => {
    for (const node of nodes) {
        if (node.id === targetId) {
            return node;
        }

        const child = findWbsById(
            node.children,
            targetId,
        );

        if (child) {
            return child;
        }
    }

    return null;
};


function WbsPage() {
    /**
     * WBS 검색 Keyword
     */
    const [keyword, setKeyword] =
        useState("");


    /**
     * WBS Status 검색 조건
     */
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
     * 현재 선택된 실제 WBS
     *
     * null인 경우:
     *
     * 화면 전용 Virtual Root가 선택된 상태이다.
     *
     * 따라서 WBS 생성 시:
     *
     * parentId = null
     *
     * 이 된다.
     */
    const [selectedWbs, setSelectedWbs] =
        useState<WbsTreeResponse | null>(
            null,
        );


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
     * WBS 생성 Form 표시 여부
     */
    const [showCreateForm, setShowCreateForm] =
        useState(false);


    /**
     * WBS 조회
     *
     * 검색 조건이 없으면 전체 Tree를 조회한다.
     *
     * GET /api/projects/{projectId}/wbs/tree
     *
     * 검색 조건이 있으면 Flat Search API를 호출한다.
     *
     * GET /api/wbs
     */
    useEffect(() => {
        const timer =
            window.setTimeout(() => {
                const loadWbs = async () => {
                    try {
                        setLoading(true);
                        setError(null);


                        /**
                         * 검색 조건이 없는 경우
                         *
                         * 전체 WBS Tree 조회
                         */
                        if (
                            keyword.trim() === "" &&
                            status === ""
                        ) {
                            const data =
                                await getWbsTree(
                                    PROJECT_ID,
                                );

                            setWbsTree(data);


                            /**
                             * 최초 진입 시에는
                             * Virtual Root를 선택한다.
                             *
                             * 실제 WBS가 선택되지 않았으므로
                             * parentId = null 상태이다.
                             */
                            setSelectedWbs(null);

                            return;
                        }


                        /**
                         * 검색 조건이 있는 경우
                         *
                         * GET /api/wbs
                         */
                        const response =
                            await searchWbs({
                                projectId:
                                    PROJECT_ID,
                                keyword:
                                    keyword.trim() ||
                                    undefined,
                                status:
                                    status ||
                                    undefined,
                                page: 0,
                                size: 20,
                            });


                        /**
                         * Search API는 Flat List를 반환한다.
                         *
                         * Tree Node 형태로 변환한다.
                         */
                        const tree =
                            convertSearchResultsToTree(
                                response.content,
                            );

                        setWbsTree(tree);


                        /**
                         * 검색 상태에서는
                         * 실제 WBS를 선택하지 않는다.
                         *
                         * selectedWbs = null
                         */
                        setSelectedWbs(null);
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
         * 검색어 입력 debounce
         */
        return () => {
            window.clearTimeout(timer);
        };
    }, [keyword, status]);


    /**
     * WBS 생성 버튼
     *
     * 현재 선택된 WBS가 있으면
     * 해당 WBS를 Parent로 사용한다.
     *
     * selectedWbs === null이면
     * Virtual Root 상태이므로
     * parentId = null인 최상위 WBS를 생성한다.
     */
    const handleCreate = () => {
        setShowCreateForm(true);
    };


    /**
     * Tree Node 선택
     *
     * Virtual Root:
     *     null
     *
     * 실제 WBS:
     *     WbsTreeResponse
     */
    const handleSelectWbs = (
        wbs: WbsTreeResponse | null,
    ) => {
        setSelectedWbs(wbs);
    };


    /**
     * WBS 생성 Submit
     *
     * POST /api/projects/{projectId}/wbs
     */
    const handleCreateSubmit = async (
        request: WbsCreateRequest,
    ): Promise<void> => {
        try {
            setLoading(true);
            setError(null);


            /**
             * Backend WBS 생성
             */
            const createdWbs =
                await createWbs(
                    PROJECT_ID,
                    request,
                );


            console.log(
                "Created WBS:",
                createdWbs,
            );


            /**
             * 생성 Form 닫기
             */
            setShowCreateForm(false);


            /**
             * 최신 Tree 다시 조회
             */
            const data =
                await getWbsTree(
                    PROJECT_ID,
                );

            setWbsTree(data);


            /**
             * 방금 생성한 WBS를 찾아
             * 선택 상태로 만든다.
             */
            const createdNode =
                findWbsById(
                    data,
                    createdWbs.id,
                );


            if (createdNode) {
                setSelectedWbs(
                    createdNode,
                );
            } else {
                /**
                 * 생성된 WBS를 찾지 못한 경우
                 * Virtual Root 상태로 돌아간다.
                 */
                setSelectedWbs(null);
            }
        } catch (err) {
            console.error(
                "Failed to create WBS:",
                err,
            );

            setError(
                "WBS 생성에 실패했습니다.",
            );

            /**
             * WbsForm에서도 오류 처리가
             * 가능하도록 다시 전달한다.
             */
            throw err;
        } finally {
            setLoading(false);
        }
    };


    /**
     * WBS 생성 취소
     */
    const handleCreateCancel = () => {
        setShowCreateForm(false);
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


            {/* WBS Create Form */}
            {showCreateForm && (
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <WbsForm
                        projectId={PROJECT_ID}
                        parentWbs={selectedWbs}
                        onSubmit={
                            handleCreateSubmit
                        }
                        onCancel={
                            handleCreateCancel
                        }
                    />
                </div>
            )}


            {/* Toolbar */}
            {!showCreateForm && (
                <div
                    style={{
                        marginBottom: "16px",
                    }}
                >
                    <WbsToolbar
                        keyword={keyword}
                        status={status}
                        onKeywordChange={
                            setKeyword
                        }
                        onStatusChange={
                            setStatus
                        }
                        onCreate={
                            handleCreate
                        }
                    />
                </div>
            )}


            {/* Loading */}
            {loading && (
                <div
                    style={{
                        padding: "40px",
                        textAlign: "center",
                        backgroundColor:
                            "#ffffff",
                        border:
                            "1px solid #dddddd",
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
                        backgroundColor:
                            "#fff3f3",
                        border:
                            "1px solid #f0b8b8",
                        borderRadius: "6px",
                        color: "#c62828",
                    }}
                >
                    {error}
                </div>
            )}


            {/* Empty Search Result */}
            {!loading &&
                !error &&
                wbsTree.length === 0 &&
                (
                    keyword.trim() !== "" ||
                    status !== ""
                ) && (
                    <div
                        style={{
                            padding: "40px",
                            marginBottom: "16px",
                            textAlign: "center",
                            backgroundColor:
                                "#ffffff",
                            border:
                                "1px solid #dddddd",
                            borderRadius: "6px",
                            color: "#666666",
                        }}
                    >
                        검색 조건에 해당하는 WBS가 없습니다.
                    </div>
                )}


            {/* WBS Tree / Detail */}
            {!loading &&
                !error && (
                    <div
                        style={{
                            display: "grid",
                            gridTemplateColumns:
                                "1.5fr 1fr",
                            gap: "16px",
                            alignItems:
                                "start",
                        }}
                    >
                        {/* WBS Tree */}
                        <WbsTree
                            wbsList={wbsTree}
                            selectedWbsId={
                                selectedWbs?.id ??
                                0
                            }
                            onSelect={
                                handleSelectWbs
                            }
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
                            onDelete={
                                handleDelete
                            }
                        />
                    </div>
                )}
        </div>
    );
}


export default WbsPage;