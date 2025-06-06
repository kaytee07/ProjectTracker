package taylor.project.projecttracker.Record.ProjectRecords;

import taylor.project.projecttracker.Entity.Status;
import taylor.project.projecttracker.Record.TaskRecords.TaskResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDateTime deadline,
        Status status
) {}

