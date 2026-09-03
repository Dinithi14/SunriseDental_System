<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String currentUri = request.getRequestURI();
    String username = (String) session.getAttribute("username");
    String fullName = (String) session.getAttribute("fullName");
    String role = (String) session.getAttribute("role");
    
    // If not logged in and not on login page, redirect to login
    if (username == null && !currentUri.endsWith("login.jsp") && !currentUri.endsWith("/auth")) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic - Patient & Appointment Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<% if (username != null) { %>
<nav class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/index.jsp" class="brand-logo">
            <div class="brand-icon">✨</div>
            <div class="brand-text">Sunrise <span>Dental</span></div>
        </a>

        <ul class="nav-links">
            <li><a href="${pageContext.request.contextPath}/index.jsp" class="nav-link <%= currentUri.endsWith("index.jsp") ? "active" : "" %>">📊 Dashboard</a></li>
            <li><a href="${pageContext.request.contextPath}/appointments" class="nav-link <%= currentUri.contains("appointments") ? "active" : "" %>">📅 Appointments</a></li>
            <li><a href="${pageContext.request.contextPath}/search.jsp" class="nav-link <%= currentUri.endsWith("search.jsp") ? "active" : "" %>">🔍 Search Details</a></li>
            <li><a href="${pageContext.request.contextPath}/patients" class="nav-link <%= currentUri.contains("patients") ? "active" : "" %>">👥 Patients</a></li>
            <li><a href="${pageContext.request.contextPath}/billing" class="nav-link <%= currentUri.contains("billing") || currentUri.contains("receipt") ? "active" : "" %>">💳 Billing & Invoice</a></li>
            <li><a href="${pageContext.request.contextPath}/reports" class="nav-link <%= currentUri.contains("reports") ? "active" : "" %>">📈 Analytics</a></li>
            <li><a href="${pageContext.request.contextPath}/help.jsp" class="nav-link <%= currentUri.endsWith("help.jsp") ? "active" : "" %>">❓ Help Guide</a></li>
        </ul>

        <div style="display: flex; align-items: center; gap: 1rem;">
            <div class="user-profile-badge">
                <div class="user-avatar"><%= username != null ? username.substring(0, 1).toUpperCase() : "U" %></div>
                <div class="user-info">
                    <div class="user-name"><%= fullName != null ? fullName : username %></div>
                    <div class="user-role"><%= role != null ? role : "STAFF" %></div>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/auth?action=logout" class="btn btn-secondary btn-sm" title="Safely Close & Logout">🚪 Exit</a>
        </div>
    </div>
</nav>
<% } %>

<main class="main-content">
