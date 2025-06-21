package taylor.project.projecttracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import taylor.project.projecttracker.entity.AuditLog;
import taylor.project.projecttracker.service.AuditLogService;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String actorName) {

        List<AuditLog> logs = auditLogService.getLogs(entityType, actorName);
        return ResponseEntity.ok(logs);
    }
}
