import { useEffect, useState } from "react";

import WbsToolbar from "@/features/wbs/components/WbsToolbar";
import WbsTree from "@/features/wbs/components/WbsTree";
import WbsDetail from "@/features/wbs/components/WbsDetail";
import WbsForm from "@/features/wbs/components/WbsForm";
import WbsStatusForm from "@/features/wbs/components/WbsStatusForm";

import {
    createWbs,
    deleteWbs,
    getWbsTree,
    searchWbs,
    updateWbs,
} from "@/features/wbs/api/wbsApi";

import type {
    WbsCreateRequest,
    WbsResponse,
    WbsStatus,
    WbsTreeResponse,
    WbsUpdateRequest,
} from "@/features/wbs/types/wbs";


const PROJECT_ID = 2;


/**
 * WBS Form Mode
 *
 * create:
 * 신규 WBS 생성
 *
 * edit:
 * 기존 WBS 수정
 */
type WbsFormMode =
    | "create"
    | "edit";


/**
 * WBS Page Form Mode
 *
 * create:
 * 신규 WBS 생성
 *
 * edit:
 * 기존 WBS 수정
 *
 * status:
 * WBS 상태 변경
 */
type WbsPageFormMode =
    | "create"
    | "edit"
    | "status";


/**
 * 검색 결과로 반환된 Flat WBS를
 * WbsTree에서 사용할 수 있는 형태로 변환한다.
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


        const child =
            findWbsById(
                node.children,
                targetId,
            );


        if (child) {
            return child;
        }
    }


    return null;
};


/**
 * WBS Tree에서 특정 WBS의 Parent를 찾는다.
 *
 * targetId:
 * 현재 찾고자 하는 WBS ID
 *
 * nodes:
 * WBS Tree
 *
 * 반환:
 * Parent WBS가 있으면 해당 Node
 * 최상위 WBS이면 null
 */
const findParentWbs = (
    nodes: WbsTreeResponse[],
    targetId: number,
): WbsTreeResponse | null => {

    for (const node of nodes) {

        /**
         * 현재 node의 직접적인 child인지 확인
         */
        const childExists =
            node.children.some(
                (child) =>
                    child.id === targetId,
            );


        if (childExists) {
            return node;
        }


        /**
         * 하위 Tree 탐색
         */
        const parent =
            findParentWbs(
                node.children,
                targetId,
            );


        if (parent) {
            return parent;
        }
    }


    /**
     * Parent가 없으면
     * 최상위 WBS이다.
     */
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
     */
    const [wbsTree, setWbsTree] =
        useState<WbsTreeResponse[]>([]);


    /**
     * 현재 선택된 WBS
     *
     * null:
     * Virtual Root 상태
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
     * WBS Form 표시 여부
     */
    const [showForm, setShowForm] =
        useState(false);


    /**
     * WBS Create / Edit Form Mode
     */
    const [formMode, setFormMode] =
        useState<WbsFormMode>("create");


    /**
     * 현재 Page에서 표시할 Form 종류
     *
     * create:
     * WbsForm - 생성
     *
     * edit:
     * WbsForm - 수정
     *
     * status:
     * WbsStatusForm - 상태 변경
     */
    const [pageFormMode, setPageFormMode] =
        useState<WbsPageFormMode>("create");


    /**
     * 현재 Form에서 사용하는 Parent WBS
     *
     * Create:
     * 현재 선택된 WBS
     *
     * Edit:
     * 수정 대상 WBS의 실제 Parent
     */
    const [formParentWbs, setFormParentWbs] =
        useState<WbsTreeResponse | null>(
            null,
        );


    /**
     * WBS 조회
     *
     * 검색 조건이 없으면 전체 Tree 조회
     *
     * GET /api/projects/{projectId}/wbs/tree
     *
     * 검색 조건이 있으면 Flat Search API
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
                             * 전체 Tree 조회 시
                             * 선택 상태 초기화
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
                         * Flat Search Result
                         *
                         * →
                         *
                         * Tree Node
                         */
                        const tree =
                            convertSearchResultsToTree(
                                response.content,
                            );


                        setWbsTree(tree);


                        /**
                         * 검색 결과에서는
                         * 선택 상태를 초기화한다.
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
         * Search Debounce
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
     * 선택된 WBS가 없으면
     * 최상위 WBS를 생성한다.
     */
    const handleCreate = () => {

        setFormMode("create");

        setPageFormMode("create");


        /**
         * Create에서는
         * 현재 선택된 WBS가 Parent
         */
        setFormParentWbs(
            selectedWbs,
        );


        setShowForm(true);
    };


    /**
     * Tree Node 선택
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
             * 최신 Tree 조회
             */
            const data =
                await getWbsTree(
                    PROJECT_ID,
                );


            setWbsTree(data);


            /**
             * 생성된 WBS를 Tree에서 찾는다.
             */
            const createdNode =
                findWbsById(
                    data,
                    createdWbs.id,
                );


            /**
             * 생성된 WBS 선택
             */
            if (createdNode) {

                setSelectedWbs(
                    createdNode,
                );

            } else {

                setSelectedWbs(null);
            }


            /**
             * Form 닫기
             */
            setShowForm(false);


            /**
             * Form Parent 초기화
             */
            setFormParentWbs(null);

        } catch (err) {

            console.error(
                "Failed to create WBS:",
                err,
            );


            setError(
                "WBS 생성에 실패했습니다.",
            );


            /**
             * WbsForm에서도
             * Error를 표시할 수 있도록
             * 다시 throw
             */
            throw err;

        } finally {

            setLoading(false);
        }
    };


    /**
     * WBS 수정 버튼
     *
     * WbsDetail의 Edit 버튼에서 호출된다.
     */
    const handleEdit = (
        wbs: WbsTreeResponse,
    ) => {

        /**
         * 수정 대상 WBS 선택
         */
        setSelectedWbs(wbs);


        /**
         * 실제 Parent WBS 조회
         *
         * 중요:
         * 수정 대상 자신을 Parent로 넘기면 안 된다.
         */
        const parent =
            findParentWbs(
                wbsTree,
                wbs.id,
            );


        setFormParentWbs(
            parent,
        );


        /**
         * Edit Mode
         */
        setFormMode("edit");

        setPageFormMode("edit");


        /**
         * Form 표시
         */
        setShowForm(true);
    };


    /**
     * WBS 수정 Submit
     *
     * PUT /api/wbs/{id}
     */
    const handleUpdateSubmit = async (
        id: number,
        request: WbsUpdateRequest,
    ): Promise<void> => {

        try {

            setLoading(true);
            setError(null);


            /**
             * Backend WBS 수정
             */
            const updatedWbs =
                await updateWbs(
                    id,
                    request,
                );


            console.log(
                "Updated WBS:",
                updatedWbs,
            );


            /**
             * 최신 Tree 조회
             */
            const data =
                await getWbsTree(
                    PROJECT_ID,
                );


            setWbsTree(data);


            /**
             * 수정된 WBS를 다시 찾는다.
             */
            const updatedNode =
                findWbsById(
                    data,
                    updatedWbs.id,
                );


            if (updatedNode) {

                setSelectedWbs(
                    updatedNode,
                );

            } else {

                setSelectedWbs(null);
            }


            /**
             * Form 닫기
             */
            setShowForm(false);


            /**
             * Form Parent 초기화
             */
            setFormParentWbs(null);

        } catch (err) {

            console.error(
                "Failed to update WBS:",
                err,
            );


            setError(
                "WBS 수정에 실패했습니다.",
            );


            /**
             * WbsForm에서
             * Error 처리 가능하도록
             * 다시 throw
             */
            throw err;

        } finally {

            setLoading(false);
        }
    };


    /**
     * WBS 상태 변경 버튼
     *
     * WbsDetail의 Change Status 버튼에서 호출된다.
     *
     * 별도의 Status API를 사용하지 않고
     * 기존 WBS Update API를 사용한다.
     */
    const handleChangeStatus = () => {

        if (!selectedWbs) {
            return;
        }


        /**
         * Status Form Mode
         */
        setPageFormMode("status");


        /**
         * Status Form 표시
         */
        setShowForm(true);
    };


    /**
     * WBS 상태 변경 Submit
     *
     * PUT /api/wbs/{id}
     *
     * 기존 WBS의 모든 값을 유지하고
     * status만 변경한다.
     */
    const handleStatusSubmit = async (
        id: number,
        newStatus: WbsStatus,
    ): Promise<void> => {

        if (!selectedWbs) {
            return;
        }


        try {

            setLoading(true);
            setError(null);


            /**
             * 기존 WBS 데이터를 유지하면서
             * status만 변경한다.
             *
             * Backend Update API가
             * 전체 WBS 데이터를 요구하기 때문에
             * 기존 값을 그대로 전달한다.
             */
            const request: WbsUpdateRequest = {

                parentId:
                    selectedWbs.parentId,

                wbsCode:
                    selectedWbs.wbsCode,

                wbsName:
                    selectedWbs.wbsName,

                description:
                    selectedWbs.description ??
                    null,

                status:
                    newStatus,

                sortOrder:
                    selectedWbs.sortOrder,
            };


            /**
             * 기존 Update API 호출
             */
            const updatedWbs =
                await updateWbs(
                    id,
                    request,
                );


            console.log(
                "Changed WBS status:",
                updatedWbs,
            );


            /**
             * 최신 Tree 조회
             */
            const data =
                await getWbsTree(
                    PROJECT_ID,
                );


            setWbsTree(data);


            /**
             * 상태가 변경된 WBS를
             * 다시 선택한다.
             */
            const updatedNode =
                findWbsById(
                    data,
                    updatedWbs.id,
                );


            if (updatedNode) {

                setSelectedWbs(
                    updatedNode,
                );

            } else {

                setSelectedWbs(null);
            }


            /**
             * Status Form 닫기
             */
            setShowForm(false);

        } catch (err) {

            console.error(
                "Failed to change WBS status:",
                err,
            );


            setError(
                "WBS 상태 변경에 실패했습니다.",
            );


            /**
             * WbsStatusForm에서
             * 자체 Error 처리 가능하도록
             * 다시 throw
             */
            throw err;

        } finally {

            setLoading(false);
        }
    };


    /**
     * WBS 삭제
     *
     * Delete 정책:
     *
     * 1. 선택된 WBS가 없으면 종료
     *
     * 2. 하위 WBS가 존재하면
     *    Front에서 삭제를 차단한다.
     *
     * 3. 하위 WBS가 없으면
     *    사용자 확인 후 DELETE API를 호출한다.
     *
     * 4. 삭제 성공 후
     *    WBS Tree를 다시 조회한다.
     *
     * 5. 삭제된 WBS 선택을 해제한다.
     */
    const handleDelete = async () => {

        if (!selectedWbs) {
            return;
        }


        /**
         * 하위 WBS 존재 여부 확인
         */
        if (
            selectedWbs.children.length > 0
        ) {

            window.alert(
                "하위 WBS가 존재하는 WBS는 삭제할 수 없습니다.\n하위 WBS를 먼저 삭제한 후 다시 시도해주세요.",
            );

            return;
        }


        /**
         * 삭제 대상 확인
         */
        const confirmed =
            window.confirm(
                `${selectedWbs.wbsCode} ${selectedWbs.wbsName}을(를) 삭제하시겠습니까?\n\n삭제된 WBS는 복구할 수 없습니다.`,
            );


        if (!confirmed) {
            return;
        }


        try {

            setLoading(true);
            setError(null);


            /**
             * DELETE /api/wbs/{id}
             */
            await deleteWbs(
                selectedWbs.id,
            );


            console.log(
                "Deleted WBS:",
                selectedWbs,
            );


            /**
             * 최신 Tree 조회
             */
            const data =
                await getWbsTree(
                    PROJECT_ID,
                );


            setWbsTree(data);


            /**
             * 삭제된 WBS 선택 해제
             */
            setSelectedWbs(null);


            /**
             * Form Parent 초기화
             */
            setFormParentWbs(null);


            /**
             * Form 닫기
             */
            setShowForm(false);

        } catch (err) {

            console.error(
                "Failed to delete WBS:",
                err,
            );


            setError(
                "WBS 삭제에 실패했습니다.",
            );

        } finally {

            setLoading(false);
        }
    };


    /**
     * WBS Form 취소
     */
    const handleFormCancel = () => {

        setShowForm(false);

        setFormParentWbs(null);
    };


    return (
        <div>

            {/* Page Header */}
            <div
                style={{
                    marginBottom:
                        "24px",
                }}
            >

                <h1
                    style={{
                        margin: 0,

                        fontSize:
                            "28px",
                    }}
                >
                    WBS Management
                </h1>


                <p
                    style={{
                        marginTop:
                            "8px",

                        color:
                            "#666666",
                    }}
                >
                    Work Breakdown Structure management
                </p>

            </div>


            {/* WBS Create / Edit Form */}
            {showForm &&
                (
                    pageFormMode === "create" ||
                    pageFormMode === "edit"
                ) && (

                    <div
                        style={{
                            marginBottom:
                                "16px",
                        }}
                    >

                        <WbsForm
                            mode={
                                formMode
                            }

                            projectId={
                                PROJECT_ID
                            }

                            wbs={
                                formMode === "edit"
                                    ? selectedWbs
                                    : null
                            }

                            parentWbs={
                                formParentWbs
                            }

                            onCreate={
                                handleCreateSubmit
                            }

                            onUpdate={
                                handleUpdateSubmit
                            }

                            onCancel={
                                handleFormCancel
                            }
                        />

                    </div>
                )}


            {/* WBS Status Form */}
            {showForm &&
                pageFormMode === "status" &&
                selectedWbs && (

                    <div
                        style={{
                            marginBottom:
                                "16px",
                        }}
                    >

                        <WbsStatusForm
                            wbs={
                                selectedWbs
                            }

                            onSubmit={
                                handleStatusSubmit
                            }

                            onCancel={
                                handleFormCancel
                            }
                        />

                    </div>
                )}


            {/* Toolbar */}
            {!showForm && (
                <div
                    style={{
                        marginBottom:
                            "16px",
                    }}
                >

                    <WbsToolbar
                        keyword={
                            keyword
                        }

                        status={
                            status
                        }

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
                        padding:
                            "40px",

                        textAlign:
                            "center",

                        backgroundColor:
                            "#ffffff",

                        border:
                            "1px solid #dddddd",

                        borderRadius:
                            "6px",
                    }}
                >
                    WBS 정보를 불러오는 중입니다...
                </div>
            )}


            {/* Error */}
            {!loading &&
                error && (
                    <div
                        style={{
                            padding:
                                "20px",

                            marginBottom:
                                "16px",

                            backgroundColor:
                                "#fff3f3",

                            border:
                                "1px solid #f0b8b8",

                            borderRadius:
                                "6px",

                            color:
                                "#c62828",
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
                            padding:
                                "40px",

                            marginBottom:
                                "16px",

                            textAlign:
                                "center",

                            backgroundColor:
                                "#ffffff",

                            border:
                                "1px solid #dddddd",

                            borderRadius:
                                "6px",

                            color:
                                "#666666",
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
                            display:
                                "grid",

                            gridTemplateColumns:
                                "1.5fr 1fr",

                            gap:
                                "16px",

                            alignItems:
                                "start",
                        }}
                    >

                        {/* WBS Tree */}
                        <WbsTree
                            wbsList={
                                wbsTree
                            }

                            selectedWbsId={
                                selectedWbs?.id ??
                                0
                            }

                            onSelect={
                                handleSelectWbs
                            }

                            keyword={
                                keyword
                            }

                            status={
                                status
                            }
                        />


                        {/* WBS Detail */}
                        <WbsDetail
                            wbs={
                                selectedWbs
                            }

                            onEdit={
                                handleEdit
                            }

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