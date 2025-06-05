package taylor.project.projecttracker.Record.TaskRecords;

import taylor.project.projecttracker.Entity.Status;

import java.time.LocalDate;
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
