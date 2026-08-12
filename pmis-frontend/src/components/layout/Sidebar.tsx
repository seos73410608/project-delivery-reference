import { NavLink } from "react-router-dom";


interface MenuItem {
  label: string;
  path: string;
}


const menuItems: MenuItem[] = [
  {
    label: "Dashboard",
    path: "/",
  },
  {
    label: "Project",
    path: "/project",
  },
  {
    label: "Task",
    path: "/task",
  },
  {
    label: "Schedule",
    path: "/schedule",
  },
  {
    label: "Issue",
    path: "/issue",
  },
  {
    label: "Risk",
    path: "/risk",
  },
  {
    label: "Change",
    path: "/change",
  },
  {
    label: "Evidence",
    path: "/evidence",
  },
  {
    label: "CMDB",
    path: "/cmdb",
  },
  {
    label: "Report",
    path: "/report",
  },
  {
    label: "Admin",
    path: "/admin",
  },
];


function Sidebar() {
  return (
    <aside
      style={{
        width: "240px",
        backgroundColor: "#eeeeee",
        padding: "20px",
        boxSizing: "border-box",
        flexShrink: 0,
      }}
    >
      <nav>
        <ul
          style={{
            listStyle: "none",
            padding: 0,
            margin: 0,
          }}
        >
          {menuItems.map((menu) => (
            <li
              key={menu.path}
              style={{
                marginBottom: "8px",
              }}
            >
              <NavLink
                to={menu.path}
                style={({ isActive }) => ({
                  display: "block",
                  padding: "10px 12px",
                  borderRadius: "4px",
                  textDecoration: "none",
                  color: isActive ? "#ffffff" : "#333333",
                  backgroundColor: isActive ? "#1976d2" : "transparent",
                  fontWeight: isActive ? "bold" : "normal",
                })}
              >
                {menu.label}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
}


export default Sidebar;