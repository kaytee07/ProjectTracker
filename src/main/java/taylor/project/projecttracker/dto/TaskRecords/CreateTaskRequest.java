package taylor.project.projecttracker.dto.TaskRecords;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import taylor.project.projecttracker.entity.Status;

import java.time.LocalDateTime;

public record CreateTaskRequest(
        @NotBlank(message = "title is required")
        @Size(max = 200)
        String title,
        @NotBlank(message = "description is required")
        @Size(min = 10, max = 1000, message = "Description must be 10-1000 characters")
        String description,
        @NotNull(message = "due date is required")
        @Future(message = "due date must be a future date")
        LocalDateTime dueDate,
        @NotNull(message = "status cannot be null")
        Status status,
        Long projectId,
        Long developerId
) {}