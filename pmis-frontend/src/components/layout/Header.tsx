function Header() {
  return (
    <header className="app-header">
      <div className="app-header__brand">
        <h1>PMIS</h1>
        <span>Project Management Information System</span>
      </div>

      <div className="app-header__user">
        <span>사용자</span>
        <button type="button">로그아웃</button>
      </div>
    </header>
  )
}

export default Header