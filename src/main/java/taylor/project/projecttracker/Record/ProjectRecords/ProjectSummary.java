package taylor.project.projecttracker.Record.ProjectRecords;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProjectSummary(Long id,
                             String name,
                             LocalDateTime deadline) {
    public void id(Long id) {
    }
}

