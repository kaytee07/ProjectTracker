package taylor.project.projecttracker.Record.ProjectRecords;

import java.time.LocalDate;

public record UpdateProjectRequest(
        String name,
        String description,
        LocalDate deadline,
        String status
) {}

