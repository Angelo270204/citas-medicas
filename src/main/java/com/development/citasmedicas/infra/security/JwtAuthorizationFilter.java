package com.development.citasmedicas.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        System.out.println("🔐 === FILTRO JWT ===");
        System.out.println("🔐 URI: " + request.getRequestURI());
        System.out.println("🔐 Method: " + request.getMethod());
        System.out.println("🔐 Authorization Header: " + (authHeader != null ? "PRESENTE" : "AUSENTE"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠ No hay token Bearer, continuando sin autenticación");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        System.out.println("🔐 Token extraído (primeros 20 chars): " + jwt.substring(0, Math.min(20, jwt.length())));

        try {
            String username = jwtUtil.getUsername(jwt);
            System.out.println("🔐 Username del token: " + username);

            if (jwtUtil.validateToken(jwt, username)) {
                String role = jwtUtil.getRole(jwt);
                System.out.println("🔐 Rol del token: " + role);

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
                System.out.println("🔐 Autoridades asignadas: " + authorities);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("✅ Autenticación establecida correctamente");
            } else {
                System.out.println("❌ Token inválido o expirado");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR al procesar token: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // No aplicar filtro a estas rutas
        return path.startsWith("/api/auth/") ||
                (path.equals("/api/doctors") && request.getMethod().equals("GET"));
    }
}
