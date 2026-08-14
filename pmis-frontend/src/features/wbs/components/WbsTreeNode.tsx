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
        <div>
            {/* WBS Node */}
            <div
                onClick={handleSelect}
                style={{
                    display: "flex",
                    alignItems: "center",
                    gap: "8px",

                    padding: "8px 12px",

                    paddingLeft:
                        `${paddingLeft}px`,

                    borderRadius: "4px",

                    cursor: "pointer",

                    backgroundColor:
                        isSelected
                            ? "#e3f2fd"
                            : isVirtualRoot
                              ? "#f5f5f5"
                              : "transparent",

                    border:
                        isSelected
                            ? "1px solid #90caf9"
                            : isVirtualRoot
                              ? "1px solid #dddddd"
                              : "1px solid transparent",

                    marginBottom: "2px",

                    fontWeight:
                        isVirtualRoot
                            ? "bold"
                            : "normal",
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
                    style={{
                        width: "24px",
                        height: "24px",
                        padding: 0,
                        border: "none",
                        backgroundColor:
                            "transparent",
                        cursor:
                            hasChildren
                                ? "pointer"
                                : "default",
                        color: "#555555",
                        fontSize: "14px",
                    }}
                >
                    {hasChildren
                        ? expanded
                            ? "▼"
                            : "▶"
                        : "•"}
                </button>


                {/* WBS Code */}
                <span
                    style={{
                        minWidth: "60px",
                        fontWeight: "bold",
                        color:
                            isVirtualRoot
                                ? "#1976d2"
                                : "#333333",
                    }}
                >
                    {isVirtualRoot
                        ? "ROOT"
                        : node.wbsCode}
                </span>


                {/* WBS Name */}
                <span
                    style={{
                        flex: 1,
                        color: "#333333",
                    }}
                >
                    {node.wbsName}
                </span>


                {/* Virtual Root 설명 */}
                {isVirtualRoot && (
                    <span
                        style={{
                            padding:
                                "4px 8px",
                            borderRadius:
                                "12px",
                            backgroundColor:
                                "#e3f2fd",
                            fontSize: "12px",
                            color: "#1976d2",
                            whiteSpace:
                                "nowrap",
                        }}
                    >
                        최상위 WBS
                    </span>
                )}


                {/* Status */}
                {!isVirtualRoot && (
                    <span
                        style={{
                            padding:
                                "4px 8px",
                            borderRadius:
                                "12px",
                            backgroundColor:
                                "#f5f5f5",
                            fontSize: "12px",
                            color: "#555555",
                            whiteSpace:
                                "nowrap",
                        }}
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
                    <span
                        style={{
                            width: "40px",
                            textAlign: "right",
                            fontSize: "12px",
                            color: "#999999",
                        }}
                    >
                        #{node.sortOrder}
                    </span>
                )}
            </div>


            {/* Children */}
            {hasChildren &&
                expanded && (
                    <div>
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