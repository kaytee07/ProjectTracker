package taylor.project.projecttracker.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Project;
import taylor.project.projecttracker.Entity.Status;
import taylor.project.projecttracker.Entity.Task;
import taylor.project.projecttracker.Mappers.ProjectMapper;
import taylor.project.projecttracker.Record.ProjectRecords.CreateProjectRequest;
import taylor.project.projecttracker.Record.ProjectRecords.ProjectResponse;
import taylor.project.projecttracker.Record.ProjectRecords.UpdateProjectRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final AuditLogRepository auditLogRepository;

    public ProjectResponse createProject(CreateProjectRequest request, String actorName) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status());
        project.setDeadline(request.deadline());
        Project saved = projectRepository.save(project);
        logAction("CREATE", "Project", saved.getId().toString(), actorName, saved);
        return ProjectMapper.toResponse(saved);
    }

    public Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
    }

    public List<ProjectResponse> findAllProjects() {
        return ProjectMapper.toResponseList(projectRepository.findAll());
    }

    public ProjectResponse updateProject(Long id, UpdateProjectRequest updatedProject, String actorName) {
        Project existingProject = projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Project not found"));
        existingProject.setName(updatedProject.name());
        existingProject.setDescription(updatedProject.description());
        existingProject.setDeadline(updatedProject.deadline());
        existingProject.setStatus(updatedProject.status());
        Project saved = projectRepository.save(existingProject);
        logAction("UPDATE", "Project", saved.getId().toString(), actorName, saved);
        return ProjectMapper.toResponse(saved);
    }

    public void deleteProject(Long id, String actorName) {
        projectRepository.findById(id).ifPresent(project -> {
            projectRepository.delete(project);
            logAction("DELETE", "Project", id.toString(), actorName, project);
        });
    }

    private void logAction(String actionType, String entityType, String entityId, String actorName, Object payload) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setActorName(actorName);
        log.setTimestamp(Instant.now());
        log.setPayload(Map.of("data", payload));  // You might want to customize serialization
        auditLogRepository.save(log);
    }

}
