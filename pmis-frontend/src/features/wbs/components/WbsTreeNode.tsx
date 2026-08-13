import { useState } from "react";

import type { WbsTreeResponse } from "@/features/wbs/types/wbs";
import { WBS_STATUS_LABEL } from "@/features/wbs/types/wbs";


interface WbsTreeNodeProps {
    node: WbsTreeResponse;
    selectedId?: number;
    onSelect?: (node: WbsTreeResponse) => void;
}


function WbsTreeNode({
    node,
    selectedId,
    onSelect,
}: WbsTreeNodeProps) {
    const [expanded, setExpanded] = useState(true);

    const hasChildren = node.children.length > 0;
    const isSelected = node.id === selectedId;

    const handleToggle = () => {
        if (!hasChildren) {
            return;
        }

        setExpanded((previous) => !previous);
    };

    const handleSelect = () => {
        onSelect?.(node);
    };

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
                    paddingLeft: `${12 + (node.level - 1) * 20}px`,
                    borderRadius: "4px",
                    cursor: "pointer",
                    backgroundColor: isSelected
                        ? "#e3f2fd"
                        : "transparent",
                    border: isSelected
                        ? "1px solid #90caf9"
                        : "1px solid transparent",
                    marginBottom: "2px",
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
                        backgroundColor: "transparent",
                        cursor: hasChildren
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
                        color: "#333333",
                    }}
                >
                    {node.wbsCode}
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

                {/* Status */}
                <span
                    style={{
                        padding: "4px 8px",
                        borderRadius: "12px",
                        backgroundColor: "#f5f5f5",
                        fontSize: "12px",
                        color: "#555555",
                        whiteSpace: "nowrap",
                    }}
                >
                    {WBS_STATUS_LABEL[node.status]}
                </span>

                {/* Sort Order */}
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
            </div>

            {/* Children */}
            {hasChildren && expanded && (
                <div>
                    {node.children.map((child) => (
                        <WbsTreeNode
                            key={child.id}
                            node={child}
                            selectedId={selectedId}
                            onSelect={onSelect}
                        />
                    ))}
                </div>
            )}
        </div>
    );
}


export default WbsTreeNode;