package taylor.project.projecttracker.Record.ProjectRecords;

import taylor.project.projecttracker.Entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateProjectRequest(
        String name,
        String description,
        LocalDateTime deadline,
        Status status
) {}

