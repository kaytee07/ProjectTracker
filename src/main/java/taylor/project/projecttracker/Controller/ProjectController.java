package taylor.project.projecttracker.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.Mappers.ProjectMapper;
import taylor.project.projecttracker.Record.ProjectRecords.CreateProjectRequest;
import taylor.project.projecttracker.Record.ProjectRecords.ProjectResponse;
import taylor.project.projecttracker.Record.ProjectRecords.UpdateProjectRequest;
import taylor.project.projecttracker.Service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody CreateProjectRequest request, @RequestParam String actorName) {
        return ResponseEntity.ok(projectService.createProject(request, actorName));
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(ProjectMapper.toResponse(projectService.findProjectById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody UpdateProjectRequest request, @RequestParam String actorName) {
        return ResponseEntity.ok(projectService.updateProject(id, request, actorName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id, @RequestParam String actorName) {
        projectService.deleteProject(id, actorName);
        return ResponseEntity.ok("Project deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.findAllProjects());
    }
}

