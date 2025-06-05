package taylor.project.projecttracker.Record;

import taylor.project.projecttracker.Entity.Status;

import java.time.LocalDateTime;

public record ProjectRecord(Long id, String name, String description, LocalDateTime deadline, Status status) {}
