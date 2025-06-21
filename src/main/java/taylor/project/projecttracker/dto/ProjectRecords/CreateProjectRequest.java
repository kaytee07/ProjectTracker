package taylor.project.projecttracker.dto.ProjectRecords;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import taylor.project.projecttracker.entity.Status;

import java.time.LocalDateTime;

public record CreateProjectRequest(
         @NotBlank @Size(max = 100) String name,
         @NotBlank @Size(max = 1000) String description,
         @Future LocalDateTime deadline,
         @NotNull Status status
) {}
