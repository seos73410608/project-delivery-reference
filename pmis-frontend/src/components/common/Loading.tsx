interface LoadingProps {
  message?: string;
  size?: "small" | "medium" | "large";
}

function Loading({
  message = "Loading...",
  size = "medium",
}: LoadingProps) {
  const sizeStyles = {
    small: {
      spinner: "20px",
      border: "2px",
      fontSize: "12px",
    },
    medium: {
      spinner: "32px",
      border: "3px",
      fontSize: "14px",
    },
    large: {
      spinner: "48px",
      border: "4px",
      fontSize: "16px",
    },
  };

  const currentSize = sizeStyles[size];

  return (
    <div
      role="status"
      aria-live="polite"
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        gap: "12px",
        padding: "24px",
      }}
    >
      <div
        style={{
          width: currentSize.spinner,
          height: currentSize.spinner,
          border: `${currentSize.border} solid #e0e0e0`,
          borderTop: `${currentSize.border} solid #1976d2`,
          borderRadius: "50%",
          animation: "pmis-loading-spin 0.8s linear infinite",
        }}
      />

      <span
        style={{
          fontSize: currentSize.fontSize,
          color: "#666666",
        }}
      >
        {message}
      </span>

      <style>
        {`
          @keyframes pmis-loading-spin {
            from {
              transform: rotate(0deg);
            }

            to {
              transform: rotate(360deg);
            }
          }
        `}
      </style>
    </div>
  );
}

export default Loading;