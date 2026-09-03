<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%
    String currentUri = request.getRequestURI();
    String ctx = request.getContextPath();
    String username = (session != null) ? (String) session.getAttribute("username") : null;
    String fullName = (session != null) ? (String) session.getAttribute("fullName") : null;
    String role = (session != null) ? (String) session.getAttribute("role") : null;
    if (fullName == null && username != null) fullName = username;
    if (fullName == null) fullName = "Staff Member";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sunrise Dental Clinic Colombo</title>
    <!-- Modern Clean CSS -->
    <link rel="stylesheet" href="<%= ctx %>/css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="nav-container">
        <a href="<%= ctx %>/index.jsp" class="brand-logo">
            <div class="brand-icon">✨</div>
            <div class="brand-text">Sunrise <span>Dental</span></div>
        </a>

        <ul class="nav-links">
            <li>
                <a href="<%= ctx %>/index.jsp" class="nav-link <%= currentUri.endsWith("index.jsp") || currentUri.endsWith("/sunrise-dental/") ? "active" : "" %>">
                    📊 Dashboard
                </a>
            </li>
            <li>
                <a href="<%= ctx %>/appointments" class="nav-link <%= currentUri.contains("/appointments") && !currentUri.contains("view=search") ? "active" : "" %>">
                    📅 Appointments
                </a>
            </li>
            <li>
                <a href="<%= ctx %>/search.jsp" class="nav-link <%= currentUri.contains("search.jsp") ? "active" : "" %>">
                    🔍 Search Details
                </a>
            </li>
            <li>
                <a href="<%= ctx %>/patients" class="nav-link <%= currentUri.contains("/patients") ? "active" : "" %>">
                    👥 Patients
                </a>
            </li>
            <li>
                <a href="<%= ctx %>/billing" class="nav-link <%= currentUri.contains("/billing") || currentUri.contains("receipt") ? "active" : "" %>">
                    💳 Billing & Invoice
                </a>
            </li>
            <li>
                <a href="<%= ctx %>/reports" class="nav-link <%= currentUri.contains("/reports") ? "active" : "" %>">
                    📈 Analytics
                </a>
            </li>
            <li>
                <a href="<%= ctx %>/help.jsp" class="nav-link <%= currentUri.contains("help.jsp") ? "active" : "" %>">
                    ❓ Help Guide
                </a>
            </li>
        </ul>

        <div style="display: flex; align-items: center; gap: 1rem;">
            <div class="user-profile-badge">
                <div class="user-avatar"><%= (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "U" %></div>
                <div class="user-info">
                    <div class="user-name"><%= fullName %></div>
                    <div class="user-role"><%= role != null ? role : "STAFF" %></div>
                </div>
            </div>
            <a href="<%= ctx %>/auth?action=logout" class="btn btn-secondary btn-sm" title="Safely Logout">🚪 Exit</a>
        </div>
    </div>
</nav>

<main class="main-content">
