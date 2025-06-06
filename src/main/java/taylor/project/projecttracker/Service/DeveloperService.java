package taylor.project.projecttracker.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Skill;
import taylor.project.projecttracker.Entity.Task;
import taylor.project.projecttracker.Exception.DeveloperNotFoundException;
import taylor.project.projecttracker.Mappers.DeveloperMapper;
import taylor.project.projecttracker.Record.DeveloperRecords.DeveloperResponse;
import taylor.project.projecttracker.Record.DeveloperRecords.UpdateDeveloperRequest;
import taylor.project.projecttracker.Record.DeveloperRecords.UpdateDeveloperSkillRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;
import taylor.project.projecttracker.Repository.SkillRepository;
import taylor.project.projecttracker.Repository.TaskRepository;
import taylor.project.projecttracker.UtilityInterfaces.DeveloperTaskCount;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final AuditLogRepository auditLogRepository;
    private final SkillRepository skillRepository;
    private final TaskRepository taskRepository;

    public Developer createDeveloper(Developer developer, String actorName) {
        Developer saved = developerRepository.save(developer);
        logAction("CREATE", "Developer", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    @CacheEvict(value = "developers", key = "#developerId")
    public Developer updateDeveloper(Long id, UpdateDeveloperRequest updatedDeveloper, String actorName) {
        Developer existing = developerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Developer not found"));
        existing.setName(updatedDeveloper.name());
        existing.setEmail(updatedDeveloper.email());
        Developer saved = developerRepository.save(existing);
        logAction("UPDATE", "Developer", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    @CacheEvict(value = "developers", key = "#developerId")
    @Transactional
    public void deleteDeveloper(Long id, String actorName) {
        developerRepository.findById(id).ifPresent(dev -> {
            List<Task> tasks = taskRepository.findByDeveloperId(id);
            for (Task task : tasks) {
                task.setDeveloper(null);
            }
            taskRepository.saveAll(tasks);
            developerRepository.delete(dev);
            logAction("DELETE", "Developer", String.valueOf(id), actorName, dev);
        });
    }


    @Cacheable(value = "developers", key = "#developerId")
    public Developer getDeveloper(Long id) {
        return developerRepository.findById(id).orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));
    }

    @Cacheable(value = "developers", key = "#developerId")
    public List<Developer> findAllDevelopers() {
        return developerRepository.findAll();
    }
    @CacheEvict(value = "developers", key = "#developerId")
    public Developer updateDeveloperSkills(Long developerId, UpdateDeveloperSkillRequest request) {
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer with id " + developerId + " not found"));

        Set<Skill> newSkills = new HashSet<>(skillRepository.findAllById(request.skills()));

        developer.setSkills(newSkills);

        return developerRepository.save(developer);
    }

    @Cacheable(value = "developers", key = "#developerId")
    public Page getPaginatedDevelopers(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(
                page, size,
                direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );
        return (Page) developerRepository.findAll(pageable)
                .map(DeveloperMapper::toResponse);
    }

    @Cacheable(value = "developers", key = "#developerId")
    public List<DeveloperTaskCount> getTop5DevelopersByTaskCount() {
        return developerRepository.findTop5DevelopersByTaskCount(PageRequest.of(0, 5));
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

