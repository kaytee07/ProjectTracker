package taylor.project.projecttracker.Service;

import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Repository.AuditLogRepository;

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
}
