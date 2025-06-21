package taylor.project.projecttracker.dto.ProjectRecords;

import taylor.project.projecttracker.entity.Project;
import taylor.project.projecttracker.entity.Status;

import java.time.LocalDateTime;

public class ProjectDTO {
    public record ProjectRequest(
            String name,
            String description,
            LocalDateTime deadline,
            Status status
    ) {

    }
}
