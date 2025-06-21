package taylor.project.projecttracker.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import taylor.project.projecttracker.dto.ErrorResponse;
import taylor.project.projecttracker.service.AuditLogService;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @Autowired
    private AuditLogService auditLogService;
    @ExceptionHandler(UserNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "user not found",
                        ex.getMessage()));
    }

    @ExceptionHandler(DeveloperNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleTaskNotFoundException(TaskNotFoundException ex, HttpServletRequest request) {
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "task not found",
                        ex.getMessage()));
    }

    @ExceptionHandler(DeveloperNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleProjectNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "project not found",
                        ex.getMessage()));
    }

    @ExceptionHandler(DeveloperNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleSkillNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "skill not found",
                        ex.getMessage()));
    }

    @ExceptionHandler(DeveloperNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleDeveloperNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        HttpStatus.NOT_FOUND.value(),
                        "developer not found",
                        ex.getMessage()));
    }

    @ExceptionHandler(DeveloperNotFoundException.class)
    public  ResponseEntity<ErrorResponse> handleInvalidTokenException(UserNotFoundException ex, HttpServletRequest request) {
        return  ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "invalid token",
                        ex.getMessage()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        auditLogService.recordEvent(email, "EXCEPTION: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An internal error occurred.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        e -> e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid"
                ));

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(400, "VALIDATION_FAILED", errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation() {
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.of(400, "DATABASE_ERROR", "Invalid data persisted"));
    }

}