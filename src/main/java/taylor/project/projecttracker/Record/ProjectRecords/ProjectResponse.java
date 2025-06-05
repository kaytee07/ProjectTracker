package taylor.project.projecttracker.Record.ProjectRecords;

import taylor.project.projecttracker.Entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDateTime deadline,
        Status status
) {}

