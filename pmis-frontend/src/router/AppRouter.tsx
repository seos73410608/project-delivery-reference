import {
  BrowserRouter,
  Routes,
  Route,
} from "react-router-dom";

import MainLayout from "../layouts/MainLayout";

import ProtectedRoute from "../features/auth/components/ProtectedRoute";

import DashboardPage from "../pages/DashboardPage";

import EvidencePage from "../features/evidence/pages/EvidencePage";

import WbsPage from "../features/wbs/pages/WbsPage";

import LoginPage from "../features/auth/pages/LoginPage";

import ProjectPage from "../features/project/pages/ProjectPage";
import ProjectDetailPage from "../features/project/pages/ProjectDetailPage";
import ProjectFormPage from "../features/project/pages/ProjectFormPage";

import OperationPage from "../features/operation/pages/OperationPage";
import OperationFormPage from "../features/operation/pages/OperationFormPage";
import OperationDetailPage from "../features/operation/pages/OperationDetailPage";

import SchedulePage from "../features/schedule/SchedulePage";

import TaskPage from "../features/task/pages/TaskPage";

import IssuePage from "../features/issue/IssuePage";

import RiskPage from "../features/risk/RiskPage";

import ChangePage from "../features/change/ChangePage";

import RuntimeExecutionFilesPage from "../features/runtime/pages/RuntimeExecutionFilesPage";


/**
 * =====================================================
 * App Router
 * =====================================================
 *
 * PMIS 전체 화면 Routing을 관리한다.
 *
 * 인증 정책:
 *
 * - /login                         인증 없이 접근 가능
 * - 그 외 MainLayout 하위 화면      Access Token 필요
 *
 * 주요 Route:
 *
 * - /                              Dashboard
 * - /login                         Login
 * - /evidence                      Evidence (기존 경로)
 * - /project/:projectId/evidence   Project Evidence Management
 * - /wbs                           WBS
 * - /project                       Project List
 * - /project/create                Project Create
 * - /project/:projectId/detail     Project Detail
 * - /project/:projectId/edit       Project Edit
 * - /operation                     Operation List
 * - /operation/create              Operation Create
 * - /operation/:operationId/detail Operation Detail
 * - /operation/:operationId/edit   Operation Edit
 * - /schedule                      Schedule
 * - /task                          Task Management
 * - /issue                         Issue
 * - /risk                          Risk
 * - /project/:projectId/change     Change Management
 * - /runtime/executions/:executionUuid/files Runtime Execution Files
 */
function AppRouter() {

  return (

    <BrowserRouter>

      <Routes>


        {/* =============================================
            Login - Public Route
            ============================================= */}

        <Route
          path="/login"
          element={
            <LoginPage />
          }
        />


        {/* =============================================
            Protected Routes
            ============================================= */}

        <Route
          element={
            <ProtectedRoute />
          }
        >

          {/* ===========================================
              Main Layout
              =========================================== */}

          <Route
            element={
              <MainLayout />
            }
          >


            {/* =========================================
                Dashboard
                ========================================= */}

            <Route
              path="/"
              element={
                <DashboardPage />
              }
            />


            {/* =========================================
                Evidence - Existing Route
                ========================================= */}

            <Route
              path="/evidence"
              element={
                <EvidencePage />
              }
            />


            {/* =========================================
                Project Evidence Management
                ========================================= */}

            <Route
              path="/project/:projectId/evidence"
              element={
                <EvidencePage />
              }
            />


            {/* =========================================
                WBS
                ========================================= */}

            <Route
              path="/wbs"
              element={
                <WbsPage />
              }
            />


            {/* =========================================
                Project List
                ========================================= */}

            <Route
              path="/project"
              element={
                <ProjectPage />
              }
            />


            {/* =========================================
                Project Create
                ========================================= */}

            <Route
              path="/project/create"
              element={
                <ProjectFormPage />
              }
            />


            {/* =========================================
                Project Detail
                ========================================= */}

            <Route
              path="/project/:projectId/detail"
              element={
                <ProjectDetailPage />
              }
            />


            {/* =========================================
                Project Edit
                ========================================= */}

            <Route
              path="/project/:projectId/edit"
              element={
                <ProjectFormPage />
              }
            />


            {/* =========================================
                Operation List
                ========================================= */}

            <Route
              path="/operation"
              element={
                <OperationPage />
              }
            />


            {/* =========================================
                Operation Create
                ========================================= */}

            <Route
              path="/operation/create"
              element={
                <OperationFormPage />
              }
            />


            {/* =========================================
                Operation Detail
                ========================================= */}

            <Route
              path="/operation/:operationId/detail"
              element={
                <OperationDetailPage />
              }
            />


            {/* =========================================
                Operation Edit
                ========================================= */}

            <Route
              path="/operation/:operationId/edit"
              element={
                <OperationFormPage />
              }
            />


            {/* =========================================
                Schedule
                ========================================= */}

            <Route
              path="/schedule"
              element={
                <SchedulePage />
              }
            />


            {/* =========================================
                Task Management
                ========================================= */}

            <Route
              path="/task"
              element={
                <TaskPage />
              }
            />


            {/* =========================================
                Issue
                ========================================= */}

            <Route
              path="/issue"
              element={
                <IssuePage />
              }
            />


            {/* =========================================
                Risk
                ========================================= */}

            <Route
              path="/risk"
              element={
                <RiskPage />
              }
            />


            {/* =========================================
                Change Management
                ========================================= */}

            <Route
              path="/project/:projectId/change"
              element={
                <ProjectChangePage />
              }
            />


            {/* =========================================
                Runtime Execution Files
                ========================================= */}

            <Route
              path="/runtime/executions/:executionUuid/files"
              element={
                <RuntimeExecutionFilesPage />
              }
            />


          </Route>

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