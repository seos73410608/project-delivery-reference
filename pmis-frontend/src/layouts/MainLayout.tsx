import { Outlet } from "react-router-dom";
import Header from "@/components/layout/Header";
import Sidebar from "@/components/layout/Sidebar";
import Breadcrumb from "@/components/layout/Breadcrumb";

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
          <Breadcrumb />

          {/* Page */}
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default MainLayout;