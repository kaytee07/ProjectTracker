package taylor.project.projecttracker.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final AuditLogRepository auditLogRepository;

    public Developer createDeveloper(Developer developer, String actorName) {
        Developer saved = developerRepository.save(developer);
        logAction("CREATE", "Developer", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    public Developer updateDeveloper(Long id, Developer updatedDeveloper, String actorName) {
        Developer existing = developerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Developer not found"));
        existing.setName(updatedDeveloper.getName());
        existing.setEmail(updatedDeveloper.getEmail());
        existing.setSkills(updatedDeveloper.getSkills());
        Developer saved = developerRepository.save(existing);
        logAction("UPDATE", "Developer", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    public void deleteDeveloper(Long id, String actorName) {
        developerRepository.findById(id).ifPresent(dev -> {
            developerRepository.delete(dev);
            logAction("DELETE", "Developer", String.valueOf(id), actorName, dev);
        });
    }

    private void logAction(String actionType, String entityType, String entityId, String actorName, Object payload) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setActorName(actorName);
        log.setTimestamp(Instant.now());
        log.setPayload(Map.of("data", payload));
        auditLogRepository.save(log);
    }
}

