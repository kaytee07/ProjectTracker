package taylor.project.projecttracker.Record;

import taylor.project.projecttracker.Entity.Status;

import java.time.LocalDateTime;

public record TaskRecord(Long id, String title, String description, Status status, LocalDateTime dueDate, Long projectId, Long developerId) {}
