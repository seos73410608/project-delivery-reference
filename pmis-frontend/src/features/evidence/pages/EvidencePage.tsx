import EvidenceSummary from "@/components/dashboard/EvidenceSummary";
import EvidenceChecklist from "@/features/evidence/components/EvidenceChecklist";
import EvidenceMatrix from "@/features/evidence/components/EvidenceMatrix";
import EvidenceDetail from "@/features/evidence/components/EvidenceDetail";


function EvidencePage() {
    return (
        <div>
            {/* Page Header */}
            <div
                style={{
                    marginBottom: "24px",
                }}
            >
                <h1
                    style={{
                        margin: 0,
                        fontSize: "28px",
                    }}
                >
                    Evidence Management
                </h1>

                <p
                    style={{
                        marginTop: "8px",
                        color: "#666666",
                    }}
                >
                    Project evidence tracking and verification
                </p>
            </div>


            {/* Evidence Summary */}
            <div
                style={{
                    marginBottom: "24px",
                }}
            >
                <EvidenceSummary />
            </div>


            {/* Evidence Checklist / Detail */}
            <div
                style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr",
                    gap: "16px",
                    marginBottom: "24px",
                }}
            >
                <EvidenceChecklist />

                <EvidenceDetail />
            </div>


            {/* Evidence Matrix */}
            <div>
                <EvidenceMatrix />
            </div>
        </div>
    );
}


export default EvidencePage;