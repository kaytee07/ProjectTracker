package taylor.project.projecttracker.Record.DeveloperRecords;

import java.util.Set;

public record DeveloperResponse(
        Long id,
        String name,
        String email,
        Set<String> skills
) {}
