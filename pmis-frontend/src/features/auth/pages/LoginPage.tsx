import LoginForm from "@/features/auth/components/LoginForm";


function LoginPage() {
    return (
        <div
            style={{
                minHeight: "100vh",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                backgroundColor: "#f5f6f8",
                padding: "24px",
                boxSizing: "border-box",
            }}
        >
            <div
                style={{
                    width: "100%",
                    maxWidth: "420px",
                    backgroundColor: "#ffffff",
                    border: "1px solid #dddddd",
                    borderRadius: "8px",
                    padding: "32px",
                    boxSizing: "border-box",
                }}
            >
                {/* Page Header */}
                <div
                    style={{
                        marginBottom: "24px",
                        textAlign: "center",
                    }}
                >
                    <h1
                        style={{
                            margin: 0,
                            fontSize: "28px",
                        }}
                    >
                        PMIS
                    </h1>

                    <p
                        style={{
                            marginTop: "8px",
                            marginBottom: 0,
                            color: "#666666",
                        }}
                    >
                        Project Management Information System
                    </p>
                </div>

                {/* Login Form */}
                <LoginForm />
            </div>
        </div>
    );
}


export default LoginPage;