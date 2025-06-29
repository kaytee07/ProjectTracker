package taylor.project.projecttracker.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import taylor.project.projecttracker.entity.AuditLog;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByActorName(String actorName);
    List<AuditLog> findByEntityTypeAndActorName(String entityType, String actorName);
}

