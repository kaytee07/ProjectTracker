package taylor.project.projecttracker.Record.ProjectRecords;

import java.time.LocalDate;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDate deadline,
        String status
) {}

