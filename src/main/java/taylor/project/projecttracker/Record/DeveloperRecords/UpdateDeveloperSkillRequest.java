package taylor.project.projecttracker.Record.DeveloperRecords;

import java.util.Set;

public record UpdateDeveloperSkillRequest(
        Set<Long> skills
) {}
