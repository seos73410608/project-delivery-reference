interface KpiCardProps {
  title: string;
  value: string;
}

function KpiCard({ title, value }: KpiCardProps) {
  return (
    <div
      style={{
        padding: "20px",
        backgroundColor: "#ffffff",
        border: "1px solid #dddddd",
        borderRadius: "6px",
      }}
    >
      <div
        style={{
          marginBottom: "8px",
          color: "#666666",
          fontSize: "14px",
        }}
      >
        {title}
      </div>

      <strong
        style={{
          fontSize: "24px",
        }}
      >
        {value}
      </strong>
    </div>
  );
}

export default KpiCard;