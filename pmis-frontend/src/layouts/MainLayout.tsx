import { Outlet } from "react-router-dom";
import Header from "@/components/layout/Header";
import Sidebar from "@/components/layout/Sidebar";

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
        <Sidebar />

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