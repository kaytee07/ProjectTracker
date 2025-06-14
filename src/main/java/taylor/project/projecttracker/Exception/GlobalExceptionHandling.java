package taylor.project.projecttracker.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import taylor.project.projecttracker.Service.AuditLogService;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @Autowired
    private AuditLogService auditLogService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex, HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";

        auditLogService.recordEvent(email, "EXCEPTION: " + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An internal error occurred.");
    }
}