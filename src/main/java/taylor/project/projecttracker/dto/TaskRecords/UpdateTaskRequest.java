package taylor.project.projecttracker.dto.TaskRecords;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import taylor.project.projecttracker.entity.Status;

import java.time.LocalDateTime;

public record UpdateTaskRequest(
        @NotBlank(message = "title cannot be blank")
        @Size(max = 200)
        String title,
        @NotBlank(message = "description cannot be blank")
        @Size(min = 10, max = 1000, message = "Description must be 10-1000 characters")
        String description,
        @NotNull(message = "due date cannot be null")
        @Future(message = "due date must be a future date")
        LocalDateTime dueDate,
        Status status,
        long developerId
) {}

