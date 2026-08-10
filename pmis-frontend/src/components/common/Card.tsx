import type { ReactNode } from "react";

interface CardProps {
  children: ReactNode;
  title?: string;
  description?: string;
  padding?: "small" | "medium" | "large";
}

function Card({
  children,
  title,
  description,
  padding = "medium",
}: CardProps) {
  const paddingStyles = {
    small: "12px",
    medium: "20px",
    large: "24px",
  };

  return (
    <section
      style={{
        backgroundColor: "#ffffff",
        border: "1px solid #e0e0e0",
        borderRadius: "6px",
        boxShadow: "0 1px 3px rgba(0, 0, 0, 0.08)",
        padding: paddingStyles[padding],
      }}
    >
      {(title || description) && (
        <header
          style={{
            marginBottom: "16px",
          }}
        >
          {title && (
            <h2
              style={{
                margin: 0,
                fontSize: "18px",
                fontWeight: "600",
                color: "#333333",
              }}
            >
              {title}
            </h2>
          )}

          {description && (
            <p
              style={{
                margin: "6px 0 0",
                fontSize: "14px",
                color: "#666666",
              }}
            >
              {description}
            </p>
          )}
        </header>
      )}

      <div>{children}</div>
    </section>
  );
}

export default Card;