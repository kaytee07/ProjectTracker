package taylor.project.projecttracker.mappers;

import taylor.project.projecttracker.entity.Project;
import taylor.project.projecttracker.entity.Task;
import taylor.project.projecttracker.dto.TaskRecords.CreateTaskRequest;
import taylor.project.projecttracker.dto.TaskRecords.TaskResponse;
import taylor.project.projecttracker.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class TaskMapper {

    public static Task toEntity(CreateTaskRequest request, Project project) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setDueDate(request.dueDate());
        task.setStatus(request.status());
        task.setProject(project);
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus(),
                task.getProject() != null ? task.getProject().getId() : null,
                task.getUser() != null ? task.getUser().getId() : null
        );
    }

    public static List<TaskResponse> toResponseList(List<Task> projects) {
        return projects.stream()
                .map(TaskMapper::toResponse)
                .collect(Collectors.toList());
    }
}
