package taylor.project.projecttracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.entity.Project;
import taylor.project.projecttracker.mappers.ProjectMapper;
import taylor.project.projecttracker.dto.ProjectRecords.CreateProjectRequest;
import taylor.project.projecttracker.dto.ProjectRecords.ProjectResponse;
import taylor.project.projecttracker.dto.ProjectRecords.ProjectSummary;
import taylor.project.projecttracker.dto.ProjectRecords.UpdateProjectRequest;
import taylor.project.projecttracker.service.ProjectService;
import taylor.project.projecttracker.service.TaskMetricsService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final TaskMetricsService taskMetricsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectResponse> createProject(@Valid  @RequestBody CreateProjectRequest request, @RequestParam String actorName) {
        log.info("POST /api/projects controller reached with actorName: {}, project: {}", actorName, request);
        return ResponseEntity.ok(projectService.createProject(request, actorName));
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(ProjectMapper.toResponse(projectService.findProjectById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody CreateProjectRequest request, @RequestParam String actorName) {
        return ResponseEntity.ok(projectService.updateProject(id, request, actorName));
    }

    @GetMapping("/without-tasks")
    public ResponseEntity<List<ProjectResponse>> getProjectsWithoutTasks() {
        return ResponseEntity.ok(projectService.getProjectsWithoutTasks());
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteProject(@PathVariable Long id, @RequestParam String actorName) {
        projectService.deleteProject(id, actorName);
        return ResponseEntity.ok("Project deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        taskMetricsService.processTask();
        return ResponseEntity.ok(projectService.findAllProjects());
    }

    @PreAuthorize("hasRole('CONTRACTOR')")
    @GetMapping("{id}/summary")
    public ProjectSummary getProjectSummaries(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Username: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());
        return projectService.findProjectSummaryByProjectId(id);
    }
}

