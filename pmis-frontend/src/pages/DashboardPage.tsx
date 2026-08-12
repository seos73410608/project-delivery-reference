import KpiCard from "@/components/dashboard/KpiCard";
import ProjectOverview from "@/components/dashboard/ProjectOverview";
import WbsProgress from "@/components/dashboard/WbsProgress";
import ScheduleSummary from "@/components/dashboard/ScheduleSummary";
import IssueSummary from "@/components/dashboard/IssueSummary";
import RecentActivity from "@/components/dashboard/RecentActivity";
import EvidenceSummary from "@/components/dashboard/EvidenceSummary";

function DashboardPage() {
  return (
    <div>
      {/* Dashboard Header */}
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
          PMIS Dashboard
        </h1>

        <p
          style={{
            marginTop: "8px",
            color: "#666666",
          }}
        >
          Project Management Information System
        </p>

        <ProjectOverview />
      </div>

      {/* KPI */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(4, 1fr)",
          gap: "16px",
          marginBottom: "24px",
        }}
      >
        <KpiCard
          title="Project Status"
          value="ON TRACK"
        />

        <KpiCard
          title="Progress"
          value="72%"
        />

        <KpiCard
          title="Schedule"
          value="68%"
        />

        <KpiCard
          title="Issues"
          value="12"
        />
      </div>

      {/* WBS / Schedule */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr",
          gap: "16px",
          marginBottom: "24px",
        }}
      >
        <WbsProgress />

        <ScheduleSummary />
      </div>

      {/* Issues / Activity */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr",
          gap: "16px",
          marginBottom: "24px",
        }}
      >
        <IssueSummary />

        <RecentActivity />
      </div>

      {/* Evidence */}
      <div
        style={{
          marginBottom: "24px",
        }}
      >
        <EvidenceSummary />
      </div>
    </div>
  );
}

export default DashboardPage;