package taylor.project.projecttracker.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Project;
import taylor.project.projecttracker.Entity.Task;
import taylor.project.projecttracker.Exception.TaskNotFoundException;
import taylor.project.projecttracker.Mappers.TaskMapper;
import taylor.project.projecttracker.Record.TaskRecords.CreateTaskRequest;
import taylor.project.projecttracker.Record.TaskRecords.TaskResponse;
import taylor.project.projecttracker.Record.TaskRecords.UpdateTaskRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;
import taylor.project.projecttracker.Repository.TaskRepository;
import taylor.project.projecttracker.UtilityInterfaces.TaskStatusCount;

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

    public Task createTask(CreateTaskRequest task, String actorName, Project project) {
        Task saved = taskRepository.save(TaskMapper.toEntity(task, project));
        logAction("CREATE", "Task", String.valueOf(saved.getId()), actorName, TaskMapper.toResponse(saved));
        return saved;
    }

    public Task findTaskById(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow(()-> new TaskNotFoundException("Task not found"));
    }

    @CacheEvict(value = {"tasks", "taskStatusCounts", "overdueTasks", "sortedTasks"}, key = "#id")
    public Task updateTask(Long id, UpdateTaskRequest updatedTask, String actorName) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        existing.setTitle(updatedTask.title());
        existing.setDescription(updatedTask.description());
        existing.setStatus(updatedTask.status());
        existing.setDueDate(updatedTask.dueDate());

        Task saved = taskRepository.save(existing);
        logAction("UPDATE", "Task", String.valueOf(saved.getId()), actorName, TaskMapper.toResponse(saved));
        return saved;
    }

    @CacheEvict(value = {"tasks", "taskStatusCounts", "overdueTasks", "sortedTasks"}, key = "#id")
    public void deleteTask(Long id, String actorName) {
        taskRepository.findById(id).ifPresent(task -> {
            taskRepository.delete(task);
            logAction("DELETE", "Task", String.valueOf(id), actorName, TaskMapper.toResponse(task));
        });
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    public List<TaskResponse> getTasksByProject(Long projectId) {
        return TaskMapper.toResponseList(taskRepository.findByProjectId(projectId));
    }

    public List<TaskResponse> getTasksByDeveloper(Long developerId) {
        return TaskMapper.toResponseList(taskRepository.findByDeveloperId(developerId));
    }

    public TaskResponse assignTaskToDeveloper(Long taskId, Long developerId, String actorName) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new TaskNotFoundException("Developer not found"));
        task.setDeveloper(developer);
        Task saved = taskRepository.save(task);
        logAction("UPDATE", "Task", String.valueOf(saved.getId()), actorName, TaskMapper.toResponse(saved));
        return TaskMapper.toResponse(saved);
    }

    @Cacheable(value = "sortedTasks", key = "#sortBy + '-' + #direction")
    public List<TaskResponse> getSortedTasks(String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return taskRepository.findAll(sort).stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Cacheable(value = "overdueTasks")
    public List<TaskResponse> getOverdueTasks() {
        return taskRepository.findOverdueTasks()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    @Cacheable(value = "taskStatusCounts")
    public List<TaskStatusCount> getTaskCountByStatus() {
        return taskRepository.countTasksByStatus();
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

