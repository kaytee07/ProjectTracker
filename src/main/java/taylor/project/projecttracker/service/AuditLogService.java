package taylor.project.projecttracker.service;

import org.springframework.stereotype.Service;
import taylor.project.projecttracker.entity.AuditLog;
import taylor.project.projecttracker.repository.AuditLogRepository;

import java.time.Instant;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    public List<AuditLog> getLogs(String entityType, String actorName) {
        if (entityType != null && actorName != null) {
            return auditLogRepository.findByEntityTypeAndActorName(entityType, actorName);
        } else if (entityType != null) {
            return auditLogRepository.findByEntityType(entityType);
        } else if (actorName != null) {
            return auditLogRepository.findByActorName(actorName);
        } else {
            return auditLogRepository.findAll();
        }
    }

    public void recordEvent(String email, String action) {
        AuditLog log = new AuditLog();
        log.setActorName(email);
        log.setActionType(action);
        log.setTimestamp(Instant.now());

        auditLogRepository.save(log);
    }
}
