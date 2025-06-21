package taylor.project.projecttracker.dto.SkillRecords;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSkillRequest(
       @NotNull @Size(max = 100) String name
) {}

