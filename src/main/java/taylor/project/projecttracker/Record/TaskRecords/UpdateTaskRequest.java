package taylor.project.projecttracker.Record.TaskRecords;

import java.time.LocalDate;

public record UpdateTaskRequest(
        String title,
        String description,
        LocalDate dueDate,
        String status
) {}

