import WbsTreeNode from "@/features/wbs/components/WbsTreeNode";

import type {
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


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
 * 실제 Backend WBS:
 *
 * WBS
 * ├── 1 프로젝트 관리
 * │   ├── 1.1 프로젝트 계획
 * │   │   └── 1.1.1 상세 일정 계획
 * │   └── 1.2 프로젝트 수행
 * │
 * └── 2 테스트 관리
 *     └── 2.1 테스트 계획
 *
 *
 * Virtual Root:
 *
 * WBS
 *
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
    /**
     * 실제 DB에는 존재하지 않는
     * 화면 전용 ID
     */
    id: 0,

    /**
     * 실제 프로젝트 ID
     *
     * children이 있으면 첫 번째 WBS의
     * projectId를 사용한다.
     *
     * WBS가 하나도 없는 경우에는
     * 0을 사용한다.
     */
    projectId:
        children.length > 0
            ? children[0].projectId
            : 0,

    /**
     * Virtual Root 자체에는
     * Parent가 없다.
     */
    parentId: null,

    /**
     * Virtual Root에는
     * 실제 WBS Code가 없다.
     */
    wbsCode: "",

    /**
     * 화면 표시용 이름
     */
    wbsName: "WBS",

    /**
     * 실제 WBS보다 한 단계 위의
     * 화면 전용 Level
     */
    level: 0,

    /**
     * 화면 전용 Sort Order
     */
    sortOrder: 0,

    /**
     * 화면 전용 기본 Status
     */
    status: "PLANNED",

    /**
     * 설명 없음
     */
    description: null,

    /**
     * Backend 생성일시 없음
     */
    createdAt: "",

    updatedAt: "",

    /**
     * 실제 Backend WBS를
     * Virtual Root의 children으로 연결한다.
     */
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
                    keyword.trim() === "" ||
                    node.wbsCode
                        .toLowerCase()
                        .includes(
                            keyword
                                .toLowerCase(),
                        ) ||
                    node.wbsName
                        .toLowerCase()
                        .includes(
                            keyword
                                .toLowerCase(),
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
     * WBS
     * ├── 1 프로젝트 관리
     * └── 2 테스트 관리
     *
     *
     * 검색 중:
     *
     * Virtual Root를 숨기고
     * 검색 결과만 표시한다.
     */
    const displayTree =
        keyword.trim() === "" &&
        status === ""
            ? [
                  createVirtualRoot(
                      filteredWbsTree,
                  ),
              ]
            : filteredWbsTree;


    /**
     * WBS Node 선택
     *
     * Virtual Root:
     *     id = 0
     *     → onSelect(null)
     *
     * 실제 WBS:
     *     → onSelect(node)
     */
    const handleSelect = (
        node: WbsTreeResponse | null,
    ) => {
        onSelect(node);
    };

    return (
        <section
            style={{
                backgroundColor: "#ffffff",
                border: "1px solid #dddddd",
                borderRadius: "6px",
                overflow: "hidden",
            }}
        >

            {/* Header */}
            <div
                style={{
                    padding: "16px",
                    borderBottom:
                        "1px solid #eeeeee",
                    fontWeight: "bold",
                    fontSize: "18px",
                }}
            >
                WBS Structure
            </div>


            {/* Tree */}
            <div
                style={{
                    padding: "12px",
                }}
            >

                {displayTree.length === 0 ? (

                    <div
                        style={{
                            padding: "24px",
                            textAlign: "center",
                            color: "#777777",
                        }}
                    >
                        No WBS found.
                    </div>

                ) : (

                    displayTree.map(
                        (node) => (
                            <WbsTreeNode
                                key={node.id}
                                node={node}

                                /**
                                 * Virtual Root:
                                 *
                                 * selectedWbsId = 0
                                 *
                                 * 실제 WBS:
                                 *
                                 * selectedWbsId = 실제 ID
                                 */
                                selectedId={
                                    selectedWbsId ??
                                    0
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