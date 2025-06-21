package taylor.project.projecttracker.dto.UserRecords;

import taylor.project.projecttracker.entity.Role;

public record UserResponse (
        Long id,
        String name,
        String email,
        Role role
){}
