package taylor.project.projecttracker.Record.SkillRecords;

import java.util.Set;

public record SkillResponse(
        int id,
        String name,
        Set<String> developerNames
) {}

