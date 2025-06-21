package taylor.project.projecttracker.dto.UserRecords;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import taylor.project.projecttracker.entity.Role;

public record
CreateUserRequest(
        @NotBlank(message = "name cannot be blank")
        String name,
        @Email
        @NotBlank(message = "message cannot be blank")
        String email,
        @NotNull(message = "role cannot be null")
        Role role
) {}
