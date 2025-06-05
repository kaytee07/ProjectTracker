package taylor.project.projecttracker.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import taylor.project.projecttracker.Entity.AuditLog;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
