package com.development.citasmedicas.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String uri = request.getRequestURI();
        String method = request.getMethod();

        log.debug("[JWT] Request: {} {} - Token: {}", method, uri, (authHeader != null ? "presente" : "ausente"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("[JWT] No token Bearer en {}, continuando sin autenticacion", uri);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        try {
            String username = jwtUtil.getUsername(jwt);

            if (jwtUtil.validateToken(jwt, username)) {
                String role = jwtUtil.getRole(jwt);

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("[JWT] Usuario '{}' autenticado con rol '{}' en {} {}", username, role, method, uri);
            } else {
                log.warn("[JWT] Token invalido o expirado para {}", uri);
            }
        } catch (Exception e) {
            log.error("[JWT] Error al procesar token en {}: {} - {}", uri, e.getClass().getSimpleName(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Excluir rutas de autenticacion
        if (path.startsWith("/api/auth/")) {
            return true;
        }
        
        // Excluir rutas comunes de bots y crawlers para evitar llenar logs
        return path.equals("/") ||
               path.equals("/robots.txt") ||
               path.equals("/security.txt") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/.well-known/");
    }
}
