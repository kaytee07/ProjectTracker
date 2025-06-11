package taylor.project.projecttracker.Record.UserRecords;

import taylor.project.projecttracker.Entity.Role;

public record CreateUserRequest(
        String firstName,
        String lastName,
        String email,
        Role role
) {}
