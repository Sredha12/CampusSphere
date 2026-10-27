package com.campussphere.servlet;

import com.campussphere.dao.DataAccessException;
import com.campussphere.dao.StudentDAO;
import com.campussphere.model.Student;
import com.campussphere.service.AuthService;
import com.campussphere.util.CsrfUtil;
import com.campussphere.util.FlashUtil;
import com.campussphere.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for viewing and updating student profile information.
 * Protected by {@link com.campussphere.filter.AuthFilter}.
 */
@WebServlet(urlPatterns = {"/student/profile"})
public class ProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ProfileServlet.class.getName());

    private final StudentDAO studentDAO = new StudentDAO();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer studentId = SessionUtil.getUserId(request);
        if (studentId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            Student student = studentDAO.findById(studentId);
            if (student == null) {
                SessionUtil.logout(request);
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            CsrfUtil.exposeToken(request);
            request.setAttribute("fullName", student.getFullName());
            request.setAttribute("email", student.getEmail());
            request.setAttribute("department", student.getDepartment());
            request.setAttribute("yearOfStudy", student.getYearOfStudy());
            request.setAttribute("phone", student.getPhone());

            request.setAttribute("pageTitle", "My Profile - CampusSphere");
            request.setAttribute("activeNav", "profile");

            request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Error loading student profile for studentId=" + studentId, e);
            throw e;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer studentId = SessionUtil.getUserId(request);
        if (studentId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String fullName = request.getParameter("fullName");
        String department = request.getParameter("department");
        String yearOfStudy = request.getParameter("yearOfStudy");
        String phone = request.getParameter("phone");

        try {
            AuthService.AuthResult<Void> result = authService.updateStudentProfile(
                    studentId, fullName, department, yearOfStudy, phone);

            if (result.isSuccess()) {
                // Update display name in session
                if (request.getSession(false) != null && fullName != null) {
                    request.getSession(false).setAttribute(SessionUtil.ATTR_USER_NAME, fullName.trim());
                }
                FlashUtil.setSuccess(request, "Profile updated.");
                response.sendRedirect(request.getContextPath() + "/student/profile");
            } else {
                Student student = studentDAO.findById(studentId);
                request.setAttribute("errors", result.getFieldErrors());
                request.setAttribute("genericError", result.getGenericError());

                // Sticky values
                request.setAttribute("fullName", fullName);
                request.setAttribute("email", (student != null) ? student.getEmail() : "");
                request.setAttribute("department", department);
                request.setAttribute("yearOfStudy", yearOfStudy);
                request.setAttribute("phone", phone);

                request.setAttribute("pageTitle", "My Profile - CampusSphere");
                request.setAttribute("activeNav", "profile");

                request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp").forward(request, response);
            }
        } catch (DataAccessException e) {
            LOGGER.log(Level.SEVERE, "Error updating profile for studentId=" + studentId, e);
            throw e;
        }
    }
}
