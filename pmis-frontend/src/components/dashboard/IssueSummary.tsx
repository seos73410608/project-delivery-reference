function IssueSummary() {
  return (
    <div
      style={{
        padding: "20px",
        backgroundColor: "#ffffff",
        border: "1px solid #dddddd",
        borderRadius: "6px",
      }}
    >
      <h3>Issues / Risks</h3>

      <p>🔴 Critical — 3</p>
      <p>🟠 Major — 4</p>
      <p>🟡 Minor — 5</p>
    </div>
  );
}

export default IssueSummary;