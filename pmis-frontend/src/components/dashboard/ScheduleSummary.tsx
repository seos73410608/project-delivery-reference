function ScheduleSummary() {
  return (
    <div
      style={{
        padding: "20px",
        backgroundColor: "#ffffff",
        border: "1px solid #dddddd",
        borderRadius: "6px",
      }}
    >
      <h3>Schedule</h3>

      <p>Overall Schedule Progress</p>

      <strong>68%</strong>

      <p
        style={{
          color: "#666666",
        }}
      >
        Next Milestone: Server Installation Complete
      </p>
    </div>
  );
}

export default ScheduleSummary;