package taylor.project.projecttracker.security.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import taylor.project.projecttracker.exception.UserNotFoundException;
import taylor.project.projecttracker.security.util.JwtTokenUtil;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Value("${app.jwt.secret}")
    private String signingKey;

    JwtTokenUtil jwtTokenUtil;


    public JwtFilter(@Value("${app.jwt.secret}") String signingKey,
                     JwtTokenUtil jwtTokenUtil) {
        this.signingKey = signingKey;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String jwt = authorizationHeader.substring(7);
                SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));

                Claims claims = jwtTokenUtil.extractClaims(jwt);
                String username = claims.get("username", String.class);
                List<String> roles = (List<String>) claims.get("authorities");
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                logger.warn("JWT validation failed: " + e.getMessage(), e);
                SecurityContextHolder.clearContext();

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or tampered token.");
                return;
            }
        } else {
            logger.warn("JWT token is missing from Authorization header.");
            SecurityContextHolder.clearContext();
            throw new UserNotFoundException("log in to continue ");

        }
        filterChain.doFilter(request, response);
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        System.out.println(path);
        return path.equals("/auth/login") ||  path.equals("/api/tasks/process")  || path.equals("/api/auth/register") || path.startsWith("/actuator/");
    }
}