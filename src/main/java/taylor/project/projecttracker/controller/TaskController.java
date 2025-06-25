package taylor.project.projecttracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.entity.Project;
import taylor.project.projecttracker.mappers.TaskMapper;
import taylor.project.projecttracker.dto.TaskRecords.CreateTaskRequest;
import taylor.project.projecttracker.dto.TaskRecords.TaskResponse;
import taylor.project.projecttracker.dto.TaskRecords.UpdateTaskRequest;
import taylor.project.projecttracker.service.ProjectService;
import taylor.project.projecttracker.service.TaskService;
import taylor.project.projecttracker.utilityInterfaces.TaskStatusCount;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request, @RequestParam String actorName) {
        Project project = projectService.findProjectById(request.projectId());
        return ResponseEntity.ok(TaskMapper.toResponse(taskService.createTask(request, actorName, project)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@RequestParam Long id) {
        return ResponseEntity.ok(TaskMapper.toResponse(taskService.findTaskById(id)));
    }
    @PreAuthorize("hasRole('DEVELOPER') and @securityUtil.isOwner(#id, authentication.name)")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest request, @RequestParam String actorName) {
        return ResponseEntity.ok(TaskMapper.toResponse(taskService.updateTask(id, request, actorName)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DEVELOPER') and @securityUtil.isOwner(#id, authentication.name)")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, @RequestParam String actorName) {
        taskService.deleteTask(id, actorName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byproject/{id}")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId));
    }

    @PostMapping("/{taskId}/assign/{developerId}")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long taskId, @PathVariable Long developerId, @RequestParam String actorName) {
        return ResponseEntity.ok(taskService.assignTaskToDeveloper(taskId, developerId, actorName));
    }


    @GetMapping("/tasks/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    @GetMapping("/tasks/status-count")
    public ResponseEntity<List<TaskStatusCount>> getTaskCountByStatus() {
        return ResponseEntity.ok(taskService.getTaskCountByStatus());
    }


    @GetMapping("/sorted")
    public ResponseEntity<List<TaskResponse>> getSortedTasks(
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        List<TaskResponse> sortedTasks = taskService.getSortedTasks(sortBy, direction);
        return ResponseEntity.ok(sortedTasks);
    }
}

