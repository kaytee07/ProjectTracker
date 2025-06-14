package taylor.project.projecttracker.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.User;
import taylor.project.projecttracker.Entity.Skill;

import taylor.project.projecttracker.Exception.SkillNotFoundException;
import taylor.project.projecttracker.Mappers.SkillMapper;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.SkillRepository;
import taylor.project.projecttracker.Repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public Skill createSkill(Skill skill, String actorName) {
        if (skillRepository.findByName(skill.getName()).isPresent()) {
            throw new IllegalArgumentException("Skill with this name already exists");
        }
        Skill saved = skillRepository.save(skill);
        logAction("CREATE", "Skill", String.valueOf(saved.getId()), actorName, SkillMapper.toResponse(saved));
        return saved;
    }

    public Skill updateSkill(long id, Skill updatedSkill, String actorName) {
        Skill existing = skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found"));
        existing.setName(updatedSkill.getName());
        Skill saved = skillRepository.save(existing);
        logAction("UPDATE", "Skill", String.valueOf(saved.getId()), actorName, SkillMapper.toResponse(saved));
        return saved;
    }

    public Skill getSkillById(long id) {
        return skillRepository.findById(id).orElseThrow(() -> new SkillNotFoundException("Skill Not Found"));
    }

    public void deleteSkill(long id, String actorName) {
        skillRepository.findById(id).ifPresent(skill -> {
            skillRepository.delete(skill);
            logAction("DELETE", "Skill", String.valueOf(id), actorName, SkillMapper.toResponse(skill));
        });
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill addSkillToDeveloper(long skillId, Long developerId, String actorName) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found"));
        User user = userRepository.findById(developerId)
                .orElseThrow(() -> new SkillNotFoundException("Developer not found"));

        skill.addUser(user);
        Skill saved = skillRepository.save(skill);
        logAction("UPDATE", "Skill", String.valueOf(saved.getId()), actorName, SkillMapper.toResponse(saved));
        return saved;
    }

    public Skill removeSkillFromDeveloper(long skillId, Long developerId, String actorName) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found"));
        User user = userRepository.findById(developerId)
                .orElseThrow(() -> new SkillNotFoundException("Developer not found"));

        skill.removeUser(user);
        Skill saved = skillRepository.save(skill);
        logAction("UPDATE", "Skill", String.valueOf(saved.getId()), actorName, SkillMapper.toResponse(saved));
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
