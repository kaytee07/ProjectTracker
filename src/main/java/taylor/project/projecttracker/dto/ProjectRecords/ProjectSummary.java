package taylor.project.projecttracker.dto.ProjectRecords;

import java.time.LocalDateTime;

public record ProjectSummary(Long id,
                             String name,
                             LocalDateTime deadline) {
    public void id(Long id) {
    }
}

