import type { ButtonHTMLAttributes, ReactNode } from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: "primary" | "secondary" | "danger" | "text";
  size?: "small" | "medium" | "large";
}

function Button({
  children,
  variant = "primary",
  size = "medium",
  type = "button",
  disabled = false,
  ...props
}: ButtonProps) {
  const variantStyles = {
    primary: {
      backgroundColor: "#1976d2",
      color: "#ffffff",
      border: "1px solid #1976d2",
    },
    secondary: {
      backgroundColor: "#ffffff",
      color: "#333333",
      border: "1px solid #cccccc",
    },
    danger: {
      backgroundColor: "#d32f2f",
      color: "#ffffff",
      border: "1px solid #d32f2f",
    },
    text: {
      backgroundColor: "transparent",
      color: "#1976d2",
      border: "1px solid transparent",
    },
  };

  const sizeStyles = {
    small: {
      padding: "6px 12px",
      fontSize: "12px",
    },
    medium: {
      padding: "8px 16px",
      fontSize: "14px",
    },
    large: {
      padding: "10px 20px",
      fontSize: "16px",
    },
  };

  return (
    <button
      type={type}
      disabled={disabled}
      style={{
        ...variantStyles[variant],
        ...sizeStyles[size],
        borderRadius: "4px",
        fontWeight: "500",
        cursor: disabled ? "not-allowed" : "pointer",
        opacity: disabled ? 0.6 : 1,
        transition: "opacity 0.2s ease",
      }}
      {...props}
    >
      {children}
    </button>
  );
}

export default Button;