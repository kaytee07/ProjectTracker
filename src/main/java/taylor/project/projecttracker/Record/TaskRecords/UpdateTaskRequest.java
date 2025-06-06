package taylor.project.projecttracker.Record.TaskRecords;

import taylor.project.projecttracker.Entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateTaskRequest(
        String title,
        String description,
        LocalDateTime dueDate,
        Status status,
        long developerId
) {}

