import { useState } from "react";

import type { WbsTreeResponse } from "@/features/wbs/types/wbs";
import { WBS_STATUS_LABEL } from "@/features/wbs/types/wbs";


interface WbsTreeNodeProps {
    node: WbsTreeResponse;
    selectedId?: number;

    /**
     * 실제 WBS 선택:
     *     onSelect(node)
     *
     * Virtual Root 선택:
     *     onSelect(null)
     */
    onSelect?: (
        node: WbsTreeResponse | null,
    ) => void;
}


/**
 * WBS Tree Node
 *
 * 일반 WBS Node와
 * 화면 전용 Virtual Root Node를
 * 동일한 컴포넌트에서 처리한다.
 *
 * Virtual Root:
 *
 * - id = 0
 * - level = 0
 * - wbsCode = ""
 * - wbsName = "WBS"
 * - Backend에는 저장되지 않는다.
 *
 * Virtual Root 선택 시:
 *
 * onSelect(null)
 *
 * → WbsPage selectedWbs = null
 * → WbsForm parentWbs = null
 * → create request parentId = null
 */
function WbsTreeNode({
    node,
    selectedId,
    onSelect,
}: WbsTreeNodeProps) {

    const [expanded, setExpanded] =
        useState(true);


    /**
     * Virtual Root 여부
     */
    const isVirtualRoot =
        node.id === 0 &&
        node.level === 0;


    /**
     * Child 존재 여부
     */
    const hasChildren =
        node.children.length > 0;


    /**
     * 현재 Node 선택 여부
     *
     * Virtual Root:
     *     node.id = 0
     *     selectedId = 0
     *
     * 실제 WBS:
     *     node.id = 실제 WBS ID
     */
    const isSelected =
        node.id === selectedId;


    /**
     * Node 펼침 / 접힘
     */
    const handleToggle = () => {
        if (!hasChildren) {
            return;
        }

        setExpanded(
            (previous) => !previous,
        );
    };


    /**
     * Node 선택
     *
     * Virtual Root:
     *     onSelect(null)
     *
     * 실제 WBS:
     *     onSelect(node)
     */
    const handleSelect = () => {
        if (isVirtualRoot) {
            onSelect?.(null);
            return;
        }

        onSelect?.(node);
    };


    /**
     * Tree depth에 따른 들여쓰기
     *
     * Virtual Root:
     *     12px
     *
     * Level 1:
     *     32px
     *
     * Level 2:
     *     52px
     *
     * Level 3:
     *     72px
     */
    const paddingLeft =
        isVirtualRoot
            ? 12
            : 12 + node.level * 20;


    return (
        <div className="wbs-tree-node-wrapper">

            {/* WBS Node */}
            <div
                onClick={handleSelect}
                className={[
                    "wbs-tree-node",
                    isSelected
                        ? "wbs-tree-node-selected"
                        : "",
                    isVirtualRoot
                        ? "wbs-tree-node-root"
                        : "",
                ]
                    .filter(Boolean)
                    .join(" ")}
                style={{
                    paddingLeft:
                        `${paddingLeft}px`,
                }}
            >

                {/* Expand / Collapse */}
                <button
                    type="button"
                    onClick={(event) => {
                        event.stopPropagation();
                        handleToggle();
                    }}
                    disabled={!hasChildren}
                    className={`wbs-tree-toggle ${
                        hasChildren
                            ? "wbs-tree-toggle-enabled"
                            : "wbs-tree-toggle-disabled"
                    }`}
                >
                    {hasChildren
                        ? expanded
                            ? "▼"
                            : "▶"
                        : "•"}
                </button>


                {/* WBS Code */}
                <span
                    className={`wbs-tree-code ${
                        isVirtualRoot
                            ? "wbs-tree-code-root"
                            : ""
                    }`}
                >
                    {isVirtualRoot
                        ? "ROOT"
                        : node.wbsCode}
                </span>


                {/* WBS Name */}
                <span className="wbs-tree-name">
                    {node.wbsName}
                </span>


                {/* Virtual Root 설명 */}
                {isVirtualRoot && (
                    <span className="wbs-tree-root-badge">
                        최상위 WBS
                    </span>
                )}


                {/* Status */}
                {!isVirtualRoot && (
                    <span className="wbs-tree-status">
                        {
                            WBS_STATUS_LABEL[
                                node.status
                            ]
                        }
                    </span>
                )}


                {/* Sort Order */}
                {!isVirtualRoot && (
                    <span className="wbs-tree-sort-order">
                        #{node.sortOrder}
                    </span>
                )}
            </div>


            {/* Children */}
            {hasChildren &&
                expanded && (
                    <div className="wbs-tree-children">
                        {node.children.map(
                            (child) => (
                                <WbsTreeNode
                                    key={child.id}
                                    node={child}
                                    selectedId={
                                        selectedId
                                    }
                                    onSelect={
                                        onSelect
                                    }
                                />
                            ),
                        )}
                    </div>
                )}
        </div>
    );
}


export default WbsTreeNode;