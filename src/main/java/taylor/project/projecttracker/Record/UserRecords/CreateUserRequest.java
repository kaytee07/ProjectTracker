package taylor.project.projecttracker.Record.UserRecords;

import taylor.project.projecttracker.Entity.Role;

public record CreateUserRequest(
        String name,
        String email,
        Role role
) {}
