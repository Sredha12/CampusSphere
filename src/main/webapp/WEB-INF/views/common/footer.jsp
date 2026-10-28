<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
    </main>

    <footer class="app-footer">
        <div class="container footer-container">
            <div class="footer-top">
                <div class="footer-brand">
                    <span class="logo"><span class="logo-accent">Campus</span>Sphere</span>
                    <p class="footer-tagline">Every Event. One Campus. One Platform.</p>
                    <p class="footer-desc">A centralized College Event Management System simplifying campus activities, registrations, and club management.</p>
                </div>
                <div class="footer-nav">
                    <div class="footer-col">
                        <h4>Explore</h4>
                        <ul>
                            <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                            <li><a href="${pageContext.request.contextPath}/events">All Events</a></li>
                            <li><a href="${pageContext.request.contextPath}/clubs">Clubs &amp; Societies</a></li>
                        </ul>
                    </div>
                    <div class="footer-col">
                        <h4>Account</h4>
                        <ul>
                            <li><a href="${pageContext.request.contextPath}/login">Student Login</a></li>
                            <li><a href="${pageContext.request.contextPath}/register">Student Register</a></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; <%= java.time.LocalDate.now().getYear() %> CampusSphere. Built with pure Java 21, Jakarta Servlets &amp; Tomcat 10.1.</p>
            </div>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
