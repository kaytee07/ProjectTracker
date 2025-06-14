package taylor.project.projecttracker.Auth.Handlers;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import taylor.project.projecttracker.Service.AuditLogService;

@Component
public class CustomAccessDeniedHandlers implements AccessDeniedHandler {

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException, java.io.IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";

        auditLogService.recordEvent(email, "ACCESS_DENIED: " + request.getRequestURI());

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden.");
    }
}

