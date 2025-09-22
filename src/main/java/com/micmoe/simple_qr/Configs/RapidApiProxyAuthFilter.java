package com.micmoe.simple_qr.Configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
//TODO disabled for now, try to enable it
public class RapidApiProxyAuthFilter extends OncePerRequestFilter {
    @Value("${RAPIDAPI_PROXY_SECRET}")
    private String rapidApiProxySecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        String secretHeader = request.getHeader("X-RapidAPI-Proxy-Secret");
        if (secretHeader == null || !secretHeader.equals(rapidApiProxySecret)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized - Invalid RapidAPI Proxy Secret");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

