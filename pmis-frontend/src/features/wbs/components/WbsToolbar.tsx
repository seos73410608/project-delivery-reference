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
                <div className="form__field wbs-toolbar-search">

                    <label
                        htmlFor="wbs-keyword"
                        className="form__label"
                    >
                        Search
                    </label>


                    <input
                        id="wbs-keyword"
                        type="text"
                        value={keyword}
                        onChange={(event) =>
                            onKeywordChange(
                                event.target.value,
                            )
                        }
                        placeholder="Search WBS..."
                        className="form__input"
                    />

                </div>


                {/* Status Filter */}
                <div className="form__field wbs-toolbar-status">

                    <label
                        htmlFor="wbs-status"
                        className="form__label"
                    >
                        Status
                    </label>


                    <select
                        id="wbs-status"
                        value={status}
                        onChange={(event) =>
                            onStatusChange(
                                event.target.value as WbsStatus | "",
                            )
                        }
                        className="form__select"
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

                </div>


                {/* Create Button */}
                <div className="wbs-toolbar-actions">

                    <button
                        type="button"
                        onClick={onCreate}
                        className="button button--primary"
                    >
                        + Create WBS
                    </button>

                </div>

            </div>

        </section>
    );
}


export default WbsToolbar;