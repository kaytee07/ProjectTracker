package taylor.project.projecttracker.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Project;
import taylor.project.projecttracker.Exception.ProjectNotFoundExpetion;
import taylor.project.projecttracker.Mappers.ProjectMapper;
import taylor.project.projecttracker.Record.ProjectRecords.CreateProjectRequest;
import taylor.project.projecttracker.Record.ProjectRecords.ProjectResponse;
import taylor.project.projecttracker.Record.ProjectRecords.ProjectSummary;
import taylor.project.projecttracker.Record.ProjectRecords.UpdateProjectRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;
import taylor.project.projecttracker.Repository.TaskRepository;
import org.springframework.cache.annotation.Cacheable;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AuditLogRepository auditLogRepository;
    private final TaskRepository taskRepository;

    @Transactional
    @CacheEvict(value = {"projects", "allProjects", "projectsWithoutTasks"}, allEntries = true)
    public ProjectResponse createProject(CreateProjectRequest request, String actorName) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setStatus(request.status());
        project.setDeadline(request.deadline());
        Project saved = projectRepository.save(project);
        logAction("CREATE", "Project", saved.getId().toString(), actorName, ProjectMapper.toResponse(saved));
        return ProjectMapper.toResponse(saved);
    }

    @Cacheable(value = "projects", key = "#projectId")
    public Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with ID " + projectId + " not found"));
    }

    @Cacheable(value = "allProjects", key = "'all'")
    public List<ProjectResponse> findAllProjects() {
        return ProjectMapper.toResponseList(projectRepository.findAll());
    }

    @Transactional
    @CacheEvict(value = {"projects", "allProjects", "projectsWithoutTasks"}, allEntries = true)
    public ProjectResponse updateProject(Long id, UpdateProjectRequest updatedProject, String actorName) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        existingProject.setName(updatedProject.name());
        existingProject.setDescription(updatedProject.description());
        existingProject.setDeadline(updatedProject.deadline());
        existingProject.setStatus(updatedProject.status());
        Project saved = projectRepository.save(existingProject);
        logAction("UPDATE", "Project", saved.getId().toString(), actorName, ProjectMapper.toResponse(saved));
        return ProjectMapper.toResponse(saved);
    }

    @Cacheable(value = "projectsWithoutTasks", key = "'noTasks'")
    public List<ProjectResponse> getProjectsWithoutTasks() {
        return projectRepository.findProjectsWithoutTasks()
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = {"projects", "allProjects", "projectsWithoutTasks"}, allEntries = true)
    public void deleteProject(Long id, String actorName) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundExpetion("Project with ID " + id + " not found"));
        taskRepository.deleteByProjectId(id);
        projectRepository.delete(project);
        logAction("DELETE", "Project", id.toString(), actorName, ProjectMapper.toResponse(project));
    }

    public ProjectSummary findProjectSummaryByProjectId(Long projectId) {
        List<Project> projects = projectRepository.findAll();
        Optional<Project> project = projectRepository.findById(projectId);
        Project project1 = project.orElseThrow(() -> new ProjectNotFoundExpetion("Project not found"));
        return new ProjectSummary(
                project1.getId(),
                project1.getName(),
                project1.getDeadline()
        );
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

