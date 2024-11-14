package org.fishingPage;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Key;
import java.util.HashSet;
import java.util.Set;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Key key = JWTUtil.getKey();

    private static final Set<String> excludedEndpoints = new HashSet<>();

    static {
        excludedEndpoints.add("/api/user/signup");
        excludedEndpoints.add("/api/user/login");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        if (excludedEndpoints.contains(path)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
