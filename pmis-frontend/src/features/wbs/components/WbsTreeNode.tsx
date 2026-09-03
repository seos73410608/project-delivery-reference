import { useState } from "react";

import type {
    WbsTreeResponse,
} from "@/features/wbs/types/wbs";

import {
    WBS_STATUS_LABEL,
} from "@/features/wbs/types/wbs";

import "@/features/wbs/styles/wbs.css";


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
     * 실제 padding은
     * wbs.css에서 CSS 변수로 처리한다.
     */
    const paddingLeft =
        isVirtualRoot
            ? 12
            : 12 + node.level * 20;


    /**
     * WBS Status에 대응하는
     * 공통 Badge modifier
     *
     * PLANNED
     *     → badge--planned
     *
     * IN_PROGRESS
     *     → badge--in-progress
     *
     * COMPLETED
     *     → badge--completed
     *
     * ON_HOLD
     *     → badge--on-hold
     *
     * CANCELLED
     *     → badge--cancelled
     */
    const statusClass =
        `badge--${node.status
            .toLowerCase()
            .replace("_", "-")}`;


    return (
        <div
            className="wbs-tree-node-wrapper"
            style={{
                "--wbs-tree-padding-left":
                    `${paddingLeft}px`,
            } as React.CSSProperties}
        >

            {/* WBS Node */}
            <div
                onClick={handleSelect}
                className={[
                    "wbs-tree-node-row",
                    isSelected
                        ? "selected"
                        : "",
                    isVirtualRoot
                        ? "virtual-root"
                        : "",
                ]
                    .filter(Boolean)
                    .join(" ")}
            >

                {/* Expand / Collapse */}
                <button
                    type="button"
                    onClick={(event) => {
                        event.stopPropagation();
                        handleToggle();
                    }}
                    disabled={!hasChildren}
                    className={[
                        "wbs-tree-toggle",
                        hasChildren
                            ? "has-children"
                            : "",
                    ]
                        .filter(Boolean)
                        .join(" ")}
                >
                    {hasChildren
                        ? expanded
                            ? "▼"
                            : "▶"
                        : "•"}
                </button>


                {/* WBS Code */}
                <span
                    className={[
                        "wbs-tree-code",
                        isVirtualRoot
                            ? "virtual-root"
                            : "",
                    ]
                        .filter(Boolean)
                        .join(" ")}
                >
                    {isVirtualRoot
                        ? "ROOT"
                        : node.wbsCode}
                </span>


                {/* WBS Name */}
                <span className="wbs-tree-name">
                    {node.wbsName}
                </span>


                {/* Virtual Root */}
                {isVirtualRoot && (
                    <span className="badge wbs-tree-root-badge">
                        최상위 WBS
                    </span>
                )}


                {/* Status */}
                {!isVirtualRoot && (
                    <span
                        className={[
                            "badge",
                            statusClass,
                            "wbs-tree-status",
                        ].join(" ")}
                    >
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