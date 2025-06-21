package taylor.project.projecttracker.dto.TaskRecords;

import taylor.project.projecttracker.entity.Status;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDateTime dueDate,
        Status status,
        Long projectId,
        Long developerId
) {}
