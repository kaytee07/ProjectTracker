package taylor.project.projecttracker.dto.ProjectRecords;

import taylor.project.projecttracker.entity.Status;

import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDateTime deadline,
        Status status
) {}

