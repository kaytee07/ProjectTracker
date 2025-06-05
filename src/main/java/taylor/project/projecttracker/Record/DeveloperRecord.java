package taylor.project.projecttracker.Record;

import java.util.Set;

public record DeveloperRecord(Long id, String name, String email, Set<Integer> skillIds) {}
