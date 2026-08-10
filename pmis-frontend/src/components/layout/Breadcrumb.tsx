import { Link, useLocation } from "react-router-dom";

interface BreadcrumbItem {
  label: string;
  path?: string;
}

const routeLabels: Record<string, string> = {
  project: "Project",
  task: "Task",
  schedule: "Schedule",
  issue: "Issue",
  risk: "Risk",
  change: "Change",
  cmdb: "CMDB",
  report: "Report",
  admin: "Admin",
};

function Breadcrumb() {
  const location = useLocation();

  const pathSegments = location.pathname
    .split("/")
    .filter(Boolean);

  const items: BreadcrumbItem[] = [
    {
      label: "Dashboard",
      path: "/",
    },
  ];

  pathSegments.forEach((segment, index) => {
    const path = `/${pathSegments.slice(0, index + 1).join("/")}`;

    items.push({
      label:
        routeLabels[segment] ??
        segment.charAt(0).toUpperCase() + segment.slice(1),
      path,
    });
  });

  return (
    <nav
      aria-label="Breadcrumb"
      style={{
        marginBottom: "20px",
      }}
    >
      <ol
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          listStyle: "none",
          padding: 0,
          margin: 0,
          fontSize: "14px",
        }}
      >
        {items.map((item, index) => {
          const isLast = index === items.length - 1;

          return (
            <li
              key={`${item.path}-${item.label}`}
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
              }}
            >
              {!isLast && item.path ? (
                <Link
                  to={item.path}
                  style={{
                    color: "#1976d2",
                    textDecoration: "none",
                  }}
                >
                  {item.label}
                </Link>
              ) : (
                <span
                  style={{
                    color: "#333333",
                    fontWeight: "bold",
                  }}
                >
                  {item.label}
                </span>
              )}

              {!isLast && (
                <span
                  aria-hidden="true"
                  style={{
                    color: "#999999",
                  }}
                >
                  /
                </span>
              )}
            </li>
          );
        })}
      </ol>
    </nav>
  );
}

export default Breadcrumb;