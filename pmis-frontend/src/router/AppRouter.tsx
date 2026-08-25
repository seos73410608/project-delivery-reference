import { BrowserRouter, Routes, Route } from "react-router-dom";

import MainLayout from "../layouts/MainLayout";
import DashboardPage from "../pages/DashboardPage";

import EvidencePage from "../features/evidence/pages/EvidencePage";
import WbsPage from "../features/wbs/pages/WbsPage";
import LoginPage from "../features/auth/pages/LoginPage";

import ProjectPage from "../features/project/pages/ProjectPage";
import ProjectDetailPage from "../features/project/pages/ProjectDetailPage";


function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>

                {/* Login */}
                <Route
                    path="/login"
                    element={<LoginPage />}
                />

                {/* Main Layout */}
                <Route element={<MainLayout />}>

                    {/* Dashboard */}
                    <Route
                        path="/"
                        element={<DashboardPage />}
                    />

                    {/* Evidence */}
                    <Route
                        path="/evidence"
                        element={<EvidencePage />}
                    />

                    {/* WBS */}
                    <Route
                        path="/wbs"
                        element={<WbsPage />}
                    />

                    {/* Project */}
                    <Route
                        path="/project"
                        element={<ProjectPage />}
                    />

                    {/* Project Detail */}
                    <Route
                        path="/project/:projectId"
                        element={<ProjectDetailPage />}
                    />

                </Route>

            </Routes>
        </BrowserRouter>
    );
}


export default AppRouter;