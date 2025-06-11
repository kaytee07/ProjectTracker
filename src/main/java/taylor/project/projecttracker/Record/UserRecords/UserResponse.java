package taylor.project.projecttracker.Record.UserRecords;

import taylor.project.projecttracker.Entity.Role;

public record UserResponse (
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role
){}
