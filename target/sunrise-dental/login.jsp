<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sunrise Dental Clinic Management System</title>
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
    <style>
        body {
            background: linear-gradient(135deg, #0f172a 0%, #075985 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            padding: 1.5rem;
            margin: 0;
            font-family: 'Segoe UI', -apple-system, BlinkMacSystemFont, Roboto, sans-serif;
        }
        .login-card {
            background: #ffffff;
            border-radius: 16px;
            width: 100%;
            max-width: 440px;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.4);
            overflow: hidden;
        }
        .login-header {
            background: linear-gradient(135deg, #0284c7, #0d9488);
            color: white;
            padding: 2.25rem 2rem;
            text-align: center;
        }
        .login-body {
            padding: 2.25rem 2rem;
        }
    </style>
</head>
<body>

<div class="login-card">
    <div class="login-header">
        <div style="font-size: 2.4rem; margin-bottom: 0.25rem;">✨</div>
        <h1 style="font-size: 1.6rem; font-weight: 700; margin: 0;">Sunrise Dental Clinic</h1>
        <p style="opacity: 0.9; font-size: 0.9rem; margin-top: 0.35rem;">Patient & Appointment Management</p>
    </div>

    <div class="login-body">
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="alert alert-danger" style="margin-bottom: 1.25rem;">
                ⚠️ <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>

        <% if ("logged_out".equals(request.getParameter("msg"))) { %>
            <div class="alert alert-success" style="margin-bottom: 1.25rem;">
                ✅ You have been safely logged out.
            </div>
        <% } %>

        <form action="<%= ctx %>/auth" method="POST">
            <div class="form-group" style="margin-bottom: 1.25rem;">
                <label class="form-label" for="username">Staff Username</label>
                <input type="text" id="username" name="username" class="form-control" placeholder="Enter username" required autofocus>
            </div>

            <div class="form-group" style="margin-bottom: 1.75rem;">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="Enter password" required>
            </div>

            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem; font-size: 1rem; font-weight: 600;">
                🔐 Secure Staff Login
            </button>
        </form>
    </div>
</div>

</body>
</html>
