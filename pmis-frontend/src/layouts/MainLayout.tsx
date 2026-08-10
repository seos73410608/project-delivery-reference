import { Outlet } from "react-router-dom";
import Header from "@/components/layout/Header";

function MainLayout() {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        height: "100vh",
      }}
    >
      {/* Header */}
      <Header />

      {/* Body */}
      <div
        style={{
          display: "flex",
          flex: 1,
        }}
      >
        {/* Sidebar */}
        <aside
          style={{
            width: "240px",
            backgroundColor: "#eeeeee",
            padding: "20px",
          }}
        >
          Sidebar
        </aside>

        {/* Content */}
        <main
          style={{
            flex: 1,
            padding: "20px",
            backgroundColor: "#f5f5f5",
          }}
        >
          {/* Breadcrumb */}
          <div
            style={{
              marginBottom: "20px",
              fontWeight: "bold",
            }}
          >
            Dashboard
          </div>

          {/* Page */}
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default MainLayout;