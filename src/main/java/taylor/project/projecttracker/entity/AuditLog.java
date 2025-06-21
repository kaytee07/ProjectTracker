package taylor.project.projecttracker.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String actionType;
    private String entityType;
    private String entityId;
    private Instant timestamp;
    private String actorName;
    private Map<String, Object> payload;
}
