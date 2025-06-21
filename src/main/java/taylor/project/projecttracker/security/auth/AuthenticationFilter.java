package taylor.project.projecttracker.security.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import taylor.project.projecttracker.service.AuditLogService;
import taylor.project.projecttracker.security.util.JwtTokenUtil;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    private final ProviderManager authenticationManager;
    private final AuditLogService auditLogService;
    private final JwtTokenUtil jwtTokenUtil;





    public AuthenticationFilter(ProviderManager authenticationManager,
                                AuditLogService auditLogService, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.auditLogService = auditLogService;
        this.jwtTokenUtil = jwtTokenUtil;

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

            System.out.println(authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticatedResult);
            String jwt = jwtTokenUtil.generateToken();
            String refreshJwt = jwtTokenUtil.refreshToken();
            String jwtString = "Bearer " + jwt;
            response.setHeader("Authorization", jwtString);
            String responseBody = String.format(
                    "{\"refresh_token\": \"%s\"}",
                     refreshJwt
            );
            response.getWriter().write(responseBody);
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
