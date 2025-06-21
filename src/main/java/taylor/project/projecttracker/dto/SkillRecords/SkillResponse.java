package taylor.project.projecttracker.dto.SkillRecords;

import java.util.Set;

public record SkillResponse(
        long id,
        String name,
        Set<Long> developerId
) {}

