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

function AppRouter() {


return (

    <BrowserRouter>

        <Routes>


            {/* Login */}

            <Route
                path="/login"
                element={
                    <LoginPage />
                }
            />


            {/* Main Layout */}

            <Route
                element={
                    <MainLayout />
                }
            >


                {/* Dashboard */}

                <Route
                    path="/"
                    element={
                        <DashboardPage />
                    }
                />


                {/* Evidence */}

                <Route
                    path="/evidence"
                    element={
                        <EvidencePage />
                    }
                />


                {/* WBS */}

                <Route
                    path="/wbs"
                    element={
                        <WbsPage />
                    }
                />


                {/* Project List */}

                <Route
                    path="/project"
                    element={
                        <ProjectPage />
                    }
                />


                {/* Project Create */}

                <Route
                    path="/project/create"
                    element={
                        <ProjectFormPage />
                    }
                />


                {/* Project Detail */}

                <Route
                    path="/project/:projectId/detail"
                    element={
                        <ProjectDetailPage />
                    }
                />


                {/* Project Edit */}

                <Route
                    path="/project/:projectId/edit"
                    element={
                        <ProjectFormPage />
                    }
                />


                {/* Schedule */}

                <Route
                    path="/schedule"
                    element={
                        <SchedulePage />
                    }
                />


            </Route>

        </Routes>

    </BrowserRouter>

);


}

export default AppRouter;
