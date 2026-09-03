<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sunrise Dental Clinic Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            background: linear-gradient(135deg, #0f172a 0%, #075985 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 1.5rem;
        }
        .login-wrapper {
            background: #ffffff;
            border-radius: var(--radius-lg);
            width: 100%;
            max-width: 440px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.35);
            overflow: hidden;
        }
        .login-hero {
            background: linear-gradient(135deg, #0284c7, #0d9488);
            color: white;
            padding: 2.5rem 2rem 2rem;
            text-align: center;
        }
        .login-body {
            padding: 2.25rem 2rem;
        }
        .demo-roles {
            background: var(--neutral-50);
            border: 1px dashed var(--neutral-300);
            padding: 0.9rem;
            border-radius: var(--radius-md);
            margin-top: 1.5rem;
            font-size: 0.82rem;
            color: var(--neutral-600);
        }
        .role-chip {
            display: inline-block;
            cursor: pointer;
            padding: 0.2rem 0.5rem;
            background: white;
            border: 1px solid var(--neutral-300);
            border-radius: var(--radius-sm);
            margin: 0.2rem 0.1rem;
            font-weight: 600;
            color: var(--primary-dark);
        }
    </style>
</head>
<body>

<div class="login-wrapper">
    <div class="login-hero">
        <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">✨</div>
        <h1 style="font-size: 1.6rem; font-weight: 700;">Sunrise Dental Clinic</h1>
        <p style="opacity: 0.9; font-size: 0.9rem; margin-top: 0.25rem;">Appointment & Patient Management System</p>
    </div>

    <div class="login-body">
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-danger">
                ⚠️ <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <% if ("logged_out".equals(request.getParameter("msg"))) { %>
            <div class="alert alert-success">
                ✅ You have been safely logged out.
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/auth" method="POST">
            <div class="form-group">
                <label class="form-label" for="username">Username</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="Enter staff username" required autofocus>
            </div>

            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Enter password" required>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1.05rem; margin-top: 0.5rem;">
                🔐 Secure Staff Login
            </button>
        </form>

        <div class="demo-roles">
            <div style="font-weight: 700; margin-bottom: 0.4rem; color: var(--neutral-800);">Quick Demo Credentials:</div>
            <div>• <span class="role-chip" onclick="fillCreds('admin', 'admin123')">Admin: admin / admin123</span></div>
            <div>• <span class="role-chip" onclick="fillCreds('receptionist', 'recep123')">Receptionist: receptionist / recep123</span></div>
            <div>• <span class="role-chip" onclick="fillCreds('drperera', 'dentist123')">Dentist: drperera / dentist123</span></div>
        </div>
    </div>
</div>

<script>
    function fillCreds(u, p) {
        document.getElementById('username').value = u;
        document.getElementById('password').value = p;
    }
</script>

</body>
</html>
