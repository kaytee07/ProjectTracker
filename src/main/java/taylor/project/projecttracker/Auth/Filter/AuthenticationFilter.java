package taylor.project.projecttracker.Auth.Filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import taylor.project.projecttracker.Service.AuditLogService;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final ProviderManager authenticationManager;
    private final AuditLogService auditLogService;

    @Value("${app.jwt.secret}")
    private String signingKey;



    public AuthenticationFilter(ProviderManager authenticationManager, @Value("${app.jwt.secret}") String signingKey, AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.signingKey = signingKey;
        this.auditLogService = auditLogService;
    }


    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || password == null) {


            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String errorMessage = "{\"message\": \"Missing email or password\"}";
            response.getWriter().write(errorMessage);

            return;
        }

        try {
            Authentication a = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authenticatedResult = authenticationManager.authenticate(a);
            List<String> authorities = authenticatedResult.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            if (authenticatedResult.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authenticatedResult);
            } else {
                auditLogService.recordEvent(username, "UNSUCCESSFUL LOGIN");
                throw new BadCredentialsException("Invalid credentials.");
            }
            SecurityContextHolder.getContext().setAuthentication(authenticatedResult);
            SecretKey key = Keys.hmacShaKeyFor(
                    signingKey.getBytes(
                            StandardCharsets.UTF_8));
            String jwt = Jwts.builder()
                    .setClaims(Map.of("username", username, "authorities", authorities))
                    .signWith(key)
                    .setExpiration(new Date(System.currentTimeMillis() + 7200000))
                    .compact();

            String jwtString = "Bearer " + jwt;

            response.setHeader("Authorization", jwtString);
            System.out.println("Successful sign-in for: " + authenticatedResult.getName());
            auditLogService.recordEvent(username, "SUCCESSFUL LOGIN");

        }catch (AuthenticationException e) {
            System.out.println("Authentication failed for: " + username + " - " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authentication Failed: " + e.getMessage());
            auditLogService.recordEvent(username, "UNSUCCESSFUL LOGIN");
        }

    }

    @Override
    public  boolean shouldNotFilter(HttpServletRequest request){
        return !request.getServletPath().equals("/auth/login");
    }

}
