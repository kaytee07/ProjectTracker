package taylor.project.projecttracker.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Task;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;
import taylor.project.projecttracker.Repository.TaskRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final DeveloperRepository developerRepository;
    private final ProjectRepository projectRepository;
    private final AuditLogRepository auditLogRepository;

    public Task createTask(Task task, String actorName) {
        Task saved = taskRepository.save(task);
        logAction("CREATE", "Task", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    public Task updateTask(Long id, Task updatedTask, String actorName) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
        existing.setDueDate(updatedTask.getDueDate());
        existing.setProject(updatedTask.getProject());
        existing.setDeveloper(updatedTask.getDeveloper());
        Task saved = taskRepository.save(existing);
        logAction("UPDATE", "Task", String.valueOf(saved.getId()), actorName, saved);
        return saved;
    }

    public void deleteTask(Long id, String actorName) {
        taskRepository.findById(id).ifPresent(task -> {
            taskRepository.delete(task);
            logAction("DELETE", "Task", String.valueOf(id), actorName, task);
        });
    }

    public List<Task> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public List<Task> getTasksByDeveloper(Long developerId) {
        return taskRepository.findByDeveloperId(developerId);
    }

    public Task assignTaskToDeveloper(Long taskId, Long developerId, String actorName) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new EntityNotFoundException("Developer not found"));
        task.setDeveloper(developer);
        Task saved = taskRepository.save(task);
        logAction("UPDATE", "Task", String.valueOf(saved.getId()), actorName, saved);
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

