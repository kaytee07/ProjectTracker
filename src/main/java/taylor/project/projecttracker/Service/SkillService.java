package taylor.project.projecttracker.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Skill;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;
import taylor.project.projecttracker.Repository.SkillRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final DeveloperRepository developerRepository;
    private final AuditLogRepository auditLogRepository;

    public Skill createSkill(Skill skill, String actorName) {
        if (skillRepository.findByName(skill.getName()).isPresent()) {
            throw new IllegalArgumentException("Skill with this name already exists");
        }
        Skill saved = skillRepository.save(skill);
        logAction("CREATE", "Skill", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    public Skill updateSkill(long id, Skill updatedSkill, String actorName) {
        Skill existing = skillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found"));
        existing.setName(updatedSkill.getName());
        Skill saved = skillRepository.save(existing);
        logAction("UPDATE", "Skill", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    public void deleteSkill(long id, String actorName) {
        skillRepository.findById(id).ifPresent(skill -> {
            skillRepository.delete(skill);
            logAction("DELETE", "Skill", String.valueOf(id), actorName, skill);
        });
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill addSkillToDeveloper(long skillId, Long developerId, String actorName) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found"));
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new EntityNotFoundException("Developer not found"));

        skill.addDeveloper(developer);
        Skill saved = skillRepository.save(skill);
        logAction("UPDATE", "Skill", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    public Skill removeSkillFromDeveloper(long skillId, Long developerId, String actorName) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found"));
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new EntityNotFoundException("Developer not found"));

        skill.removeDeveloper(developer);
        Skill saved = skillRepository.save(skill);
        logAction("UPDATE", "Skill", String.valueOf(saved.getId()), actorName, saved);
        return saved;
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

