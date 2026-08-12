import { BrowserRouter, Routes, Route } from "react-router-dom";

import MainLayout from "../layouts/MainLayout";
import DashboardPage from "../pages/DashboardPage";
import EvidencePage from "../features/evidence/pages/EvidencePage";

function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<MainLayout />}>
                    <Route
                        path="/"
                        element={<DashboardPage />}
                    />

                    <Route
                        path="/evidence"
                        element={<EvidencePage />}
                    />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}

export default AppRouter;