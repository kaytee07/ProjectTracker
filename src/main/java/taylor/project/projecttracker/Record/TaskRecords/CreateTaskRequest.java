package taylor.project.projecttracker.Record.TaskRecords;

import taylor.project.projecttracker.Entity.Status;

import java.time.LocalDateTime;

public record CreateTaskRequest(
        String title,
        String description,
        LocalDateTime dueDate,
        Status status,
        Long projectId,
        Long developerId
) {}