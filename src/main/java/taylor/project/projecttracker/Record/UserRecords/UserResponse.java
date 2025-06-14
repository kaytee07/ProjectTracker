package taylor.project.projecttracker.Record.UserRecords;

import taylor.project.projecttracker.Entity.Role;

public record UserResponse (
        Long id,
        String name,
        String email,
        Role role
){}
