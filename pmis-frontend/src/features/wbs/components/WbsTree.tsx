import WbsTreeNode from "@/features/wbs/components/WbsTreeNode";

import type {
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";

import "@/features/wbs/styles/wbs.css";


interface WbsTreeProps {
    wbsList: WbsTreeResponse[];

    /**
     * 선택된 WBS ID
     *
     * 0:
     *     Virtual Root 선택
     *
     * 실제 숫자:
     *     실제 WBS 선택
     */
    selectedWbsId: number | null;

    /**
     * WBS 선택
     *
     * null:
     *     Virtual Root 선택
     *
     * 실제 WbsTreeResponse:
     *     실제 WBS 선택
     */
    onSelect: (
        wbs: WbsTreeResponse | null,
    ) => void;

    keyword: string;
    status: WbsStatus | "";
}


/**
 * Virtual WBS Root Node
 *
 * Backend에는 저장되지 않는
 * 화면 전용 Root이다.
 *
 * 사용자가 Virtual Root를 선택하고
 * WBS 생성 버튼을 누르면
 *
 * parentId = null
 *
 * 로 최상위 WBS가 생성된다.
 */
const createVirtualRoot = (
    children: WbsTreeResponse[],
): WbsTreeResponse => ({
    id: 0,

    projectId:
        children.length > 0
            ? children[0].projectId
            : 0,

    parentId: null,

    wbsCode: "",

    wbsName: "WBS",

    level: 0,

    sortOrder: 0,

    status: "PLANNED",

    description: null,

    createdAt: "",
    updatedAt: "",

    children,
});


function WbsTree({
    wbsList,
    selectedWbsId,
    onSelect,
    keyword,
    status,
}: WbsTreeProps) {

    /**
     * 검색 조건을 한 번만 계산한다.
     */
    const normalizedKeyword =
        keyword.trim().toLowerCase();

    const isFiltering =
        normalizedKeyword !== "" ||
        status !== "";


    /**
     * 검색 조건에 따라
     * WBS Tree를 필터링한다.
     *
     * 부모 WBS가 검색되지 않았더라도
     * 하위 WBS가 검색되면
     * 부모를 유지한다.
     */
    const filterTree = (
        nodes: WbsTreeResponse[],
    ): WbsTreeResponse[] => {

        return nodes
            .map((node) => {

                /**
                 * 하위 Node를 먼저 필터링한다.
                 */
                const filteredChildren =
                    filterTree(
                        node.children,
                    );


                /**
                 * WBS Code / Name 검색
                 */
                const keywordMatched =
                    normalizedKeyword === "" ||
                    node.wbsCode
                        .toLowerCase()
                        .includes(
                            normalizedKeyword,
                        ) ||
                    node.wbsName
                        .toLowerCase()
                        .includes(
                            normalizedKeyword,
                        );


                /**
                 * WBS Status 검색
                 */
                const statusMatched =
                    status === "" ||
                    node.status === status;


                /**
                 * 현재 Node가 직접 일치하거나
                 *
                 * 하위 Node 중 검색 결과가 있으면
                 * Node를 유지한다.
                 */
                if (
                    (
                        keywordMatched &&
                        statusMatched
                    ) ||
                    filteredChildren.length > 0
                ) {
                    return {
                        ...node,
                        children:
                            filteredChildren,
                    };
                }


                return null;
            })
            .filter(
                (
                    node,
                ): node is WbsTreeResponse =>
                    node !== null,
            );
    };


    /**
     * Backend WBS Tree를
     * 검색 조건에 따라 필터링한다.
     */
    const filteredWbsTree =
        filterTree(wbsList);


    /**
     * 검색 조건이 없는 경우:
     *
     * Virtual Root를 표시한다.
     *
     * 검색 중:
     *
     * Virtual Root를 숨기고
     * 검색 결과만 표시한다.
     */
    const displayTree =
        isFiltering
            ? filteredWbsTree
            : [
                  createVirtualRoot(
                      filteredWbsTree,
                  ),
              ];


    /**
     * WBS Node 선택
     */
    const handleSelect = (
        node: WbsTreeResponse | null,
    ) => {
        onSelect(node);
    };


    return (
        <section className="wbs-tree">

            {/* Header */}
            <div className="wbs-tree-header">
                WBS Structure
            </div>


            {/* Tree */}
            <div className="wbs-tree-content">

                {displayTree.length === 0 ? (

                    <div className="state state--empty wbs-tree-empty">

                        <div className="state__icon">
                            !
                        </div>

                        <p className="state__title">
                            No WBS found.
                        </p>

                    </div>

                ) : (

                    displayTree.map(
                        (node) => (
                            <WbsTreeNode
                                key={node.id}
                                node={node}
                                selectedId={
                                    selectedWbsId ?? 0
                                }
                                onSelect={
                                    handleSelect
                                }
                            />
                        ),
                    )

                )}

            </div>

        </section>
    );
}


export default WbsTree;
