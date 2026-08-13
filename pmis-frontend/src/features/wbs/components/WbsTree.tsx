import WbsTreeNode from "@/features/wbs/components/WbsTreeNode";

import type {
    WbsStatus,
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";


interface WbsTreeProps {
    wbsList: WbsTreeResponse[];
    selectedWbsId: number | null;
    onSelect: (wbs: WbsTreeResponse) => void;
    keyword: string;
    status: WbsStatus | "";
}


function WbsTree({
    wbsList,
    selectedWbsId,
    onSelect,
    keyword,
    status,
}: WbsTreeProps) {

    const filterTree = (
        nodes: WbsTreeResponse[],
    ): WbsTreeResponse[] => {

        return nodes
            .map((node) => {

                const filteredChildren = filterTree(
                    node.children,
                );

                const keywordMatched =
                    keyword.trim() === "" ||
                    node.wbsCode
                        .toLowerCase()
                        .includes(keyword.toLowerCase()) ||
                    node.wbsName
                        .toLowerCase()
                        .includes(keyword.toLowerCase());

                const statusMatched =
                    status === "" ||
                    node.status === status;

                if (
                    (keywordMatched && statusMatched) ||
                    filteredChildren.length > 0
                ) {
                    return {
                        ...node,
                        children: filteredChildren,
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


    const filteredTree = filterTree(wbsList);


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
                    borderBottom: "1px solid #eeeeee",
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
                {filteredTree.length === 0 ? (
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
                    filteredTree.map((node) => (
                        <WbsTreeNode
                            key={node.id}
                            node={node}
                            selectedId={selectedWbsId ?? undefined}
                            onSelect={onSelect}
                        />
                    ))
                )}
            </div>
        </section>
    );
}


export default WbsTree;