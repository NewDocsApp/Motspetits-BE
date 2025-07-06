package com.example.newdocsapp_backend.security;

import com.example.newdocsapp_backend.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userId = null;
        String email = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                if(jwtUtil.validToken(token) && !jwtUtil.isTokenExpired(token))
                {
                    userId = jwtUtil.getUserIdFromToken(token).toString();
                    email = jwtUtil.getEmailFromToken(token).toString();
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                            email, "", java.util.Collections.emptyList()
                    );
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    request.setAttribute("userId", userId);
                    request.setAttribute("email", email);

                    logger.info("Authenticated user: {} (userId: {}) for request: {}", email, userId, request.getRequestURI());
                }
                else {
                    logger.warn("Invalid or expired token for request: {}", request.getRequestURI());
                }
            }
            catch (Exception e) {
                logger.error("Cannot validate JWT token: {}", e.getMessage());
            }
        }
        else {
            logger.warn("Missing or invalid Authorization header for request: {}", request.getRequestURI());
        }
        chain.doFilter(request, response);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || path.equals("/error");
    }
}
