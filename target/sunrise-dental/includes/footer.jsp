<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%
    String ctx = request.getContextPath();
%>
</main>

<footer class="footer">
    <div style="max-width: 1400px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
        <div>
            <strong>Sunrise Dental Clinic Colombo</strong> • Patient & Appointment Management System
        </div>
        <div>
            Version 1.0.0 • All Rights Reserved &copy; <%= java.time.Year.now().getValue() %>
        </div>
    </div>
</footer>

<script src="<%= ctx %>/js/app.js"></script>
</body>
</html>
