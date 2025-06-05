package taylor.project.projecttracker.Record.TaskRecords;

import java.time.LocalDate;

public record CreateTaskRequest(
        String title,
        String description,
        LocalDate dueDate,
        String status,
        Long projectId
) {}