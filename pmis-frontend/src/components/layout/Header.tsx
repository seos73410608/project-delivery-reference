
import { useNavigate } from "react-router-dom";

import {
  clearAuthStorage,
  getUser,
} from "@/features/auth/utils/authStorage";

function Header() {
  const navigate = useNavigate();
  const user = getUser();

  const handleLogout = () => {
    clearAuthStorage();
    navigate("/login", { replace: true });
  };

  return (
    <header className="app-header">
      <div className="app-header__brand">
        <h1>PMIS</h1>
        <span>Project Management Information System</span>
      </div>

      <div className="app-header__user">
        <div>
          <strong>{user?.name ?? "사용자"}</strong>
          <span>
            {user
              ? `${user.username} / ${user.role.replace(/^ROLE_/, "")}`
              : ""}
          </span>
        </div>

        <button type="button" onClick={handleLogout}>
          로그아웃
        </button>
      </div>
    </header>
  );
}

export default Header;
