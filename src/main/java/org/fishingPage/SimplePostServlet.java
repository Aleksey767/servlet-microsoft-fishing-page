package org.fishingPage;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.security.Key;

@WebServlet("/api/user/signup")
public class SimplePostServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"Test successful\"}");
    }

    private static final Key key = JWTUtil.getKey();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/json;charset=UTF-8");

        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            reader.lines().forEach(jsonBuilder::append);
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonBuilder.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid JSON format\"}");
            return;
        }

        String login = jsonObject.optString("login");
        String password = jsonObject.optString("password");

        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Login and password are required\"}");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Credentials credentials = Credentials.builder()
                .login(login)
                .password(hashedPassword)
                .build();


        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(credentials);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Could not save user\"}");
            return;
        }

        String jwtToken = Jwts.builder()
                .setSubject(credentials.getLogin())
                .signWith(key)
                .compact();

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"token\":\"" + jwtToken + "\"}");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
