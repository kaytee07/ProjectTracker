package taylor.project.projecttracker.Record.TaskRecords;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDate dueDate,
        String status,
        Long projectId,
        Long developerId
) {}
