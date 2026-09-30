import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";

import MainLayout from "../layouts/MainLayout";

import DashboardPage from "../pages/DashboardPage";

import EvidencePage from "../features/evidence/pages/EvidencePage";

import WbsPage from "../features/wbs/pages/WbsPage";

import LoginPage from "../features/auth/pages/LoginPage";

import ProjectPage from "../features/project/pages/ProjectPage";
import ProjectDetailPage from "../features/project/pages/ProjectDetailPage";
import ProjectFormPage from "../features/project/pages/ProjectFormPage";

import SchedulePage from "../features/schedule/SchedulePage";

import IssuePage from "../features/issue/IssuePage";

import RiskPage from "../features/risk/RiskPage";

import ChangePage from "../features/change/ChangePage";


/**
 * =====================================================
 * App Router
 * =====================================================
 *
 * PMIS 전체 화면 Routing을 관리한다.
 *
 * 주요 Route:
 *
 * - /                              Dashboard
 * - /login                         Login
 * - /evidence                      Evidence
 * - /wbs                           WBS
 * - /project                       Project List
 * - /project/create                Project Create
 * - /project/:projectId/detail     Project Detail
 * - /project/:projectId/edit       Project Edit
 * - /schedule                      Schedule
 * - /issue                         Issue
 * - /risk                          Risk
 * - /project/:projectId/change     Change Management
 */
function AppRouter() {

  return (

    <BrowserRouter>

      <Routes>


        {/* =============================================
            Login
            ============================================= */}

        <Route
          path="/login"
          element={
            <LoginPage />
          }
        />


        {/* =============================================
            Main Layout
            ============================================= */}

        <Route
          element={
            <MainLayout />
          }
        >


          {/* ===========================================
              Dashboard
              =========================================== */}

          <Route
            path="/"
            element={
              <DashboardPage />
            }
          />


          {/* ===========================================
              Evidence
              =========================================== */}

          <Route
            path="/evidence"
            element={
              <EvidencePage />
            }
          />


          {/* ===========================================
              WBS
              =========================================== */}

          <Route
            path="/wbs"
            element={
              <WbsPage />
            }
          />


          {/* ===========================================
              Project List
              =========================================== */}

          <Route
            path="/project"
            element={
              <ProjectPage />
            }
          />


          {/* ===========================================
              Project Create
              =========================================== */}

          <Route
            path="/project/create"
            element={
              <ProjectFormPage />
            }
          />


          {/* ===========================================
              Project Detail
              =========================================== */}

          <Route
            path="/project/:projectId/detail"
            element={
              <ProjectDetailPage />
            }
          />


          {/* ===========================================
              Project Edit
              =========================================== */}

          <Route
            path="/project/:projectId/edit"
            element={
              <ProjectFormPage />
            }
          />


          {/* ===========================================
              Schedule
              =========================================== */}

          <Route
            path="/schedule"
            element={
              <SchedulePage />
            }
          />


          {/* ===========================================
              Issue
              =========================================== */}

          <Route
            path="/issue"
            element={
              <IssuePage />
            }
          />


          {/* ===========================================
              Risk
              =========================================== */}

          <Route
            path="/risk"
            element={
              <RiskPage />
            }
          />


          {/* ===========================================
              Change Management
              =========================================== */}

          <Route
            path="/project/:projectId/change"
            element={
              <ProjectChangePage />
            }
          />


        </Route>

      </Routes>

    </BrowserRouter>

  );

}


/**
 * =====================================================
 * Project Change Route
 * =====================================================
 *
 * React Router의 projectId parameter를
 * ChangePage의 projectId prop으로 전달한다.
 */
function ProjectChangePage() {

  return (
    <ProjectChangePageContent />
  );

}


function ProjectChangePageContent() {

  const projectId = Number(
    window.location.pathname.split("/")[2]
  );

  return (
    <ChangePage
      projectId={projectId}
    />
  );

}


export default AppRouter;