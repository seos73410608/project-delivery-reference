import type { WbsStatus } from "@/features/wbs/types/wbs";
import { WBS_STATUS_LABEL } from "@/features/wbs/types/wbs";

import "@/features/wbs/styles/wbs.css";


interface WbsToolbarProps {
    keyword: string;
    status: WbsStatus | "";
    onKeywordChange: (keyword: string) => void;
    onStatusChange: (status: WbsStatus | "") => void;
    onCreate: () => void;
}


const statusOptions: WbsStatus[] = [
    "PLANNED",
    "IN_PROGRESS",
    "COMPLETED",
    "ON_HOLD",
    "CANCELLED",
];


function WbsToolbar({
    keyword,
    status,
    onKeywordChange,
    onStatusChange,
    onCreate,
}: WbsToolbarProps) {
    return (
        <section className="wbs-toolbar">

            <div className="wbs-toolbar-row">

                {/* Search */}
                <input
                    type="text"
                    value={keyword}
                    onChange={(event) =>
                        onKeywordChange(
                            event.target.value,
                        )
                    }
                    placeholder="Search WBS..."
                    className="wbs-toolbar-search"
                />


                {/* Status Filter */}
                <select
                    value={status}
                    onChange={(event) =>
                        onStatusChange(
                            event.target.value as WbsStatus | "",
                        )
                    }
                    className="wbs-toolbar-status"
                >
                    <option value="">
                        All Status
                    </option>

                    {statusOptions.map(
                        (option) => (
                            <option
                                key={option}
                                value={option}
                            >
                                {
                                    WBS_STATUS_LABEL[
                                        option
                                    ]
                                }
                            </option>
                        ),
                    )}
                </select>


                {/* Create Button */}
                <button
                    type="button"
                    onClick={onCreate}
                    className="wbs-create-button"
                >
                    + Create WBS
                </button>

            </div>

        </section>
    );
}


export default WbsToolbar;