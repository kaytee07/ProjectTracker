package taylor.project.projecttracker.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "Task")
public class Task {

    @Id
    @SequenceGenerator(
            name = "task_sequence",
            sequenceName = "task_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = SEQUENCE, generator = "task_sequence")
    private long id;

    @NotBlank(message = "Title is required")
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @NotBlank(message = "Description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be in the present or future")
    @Column(name = "duedate", nullable = false)
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference // Correctly set on the "child" side
    @ToString.Exclude // Prevents Project -> Task -> Project toString() loop
    @EqualsAndHashCode.Exclude // Prevents Project -> Task -> Project equals/hashCode loop
    private Project project;

    @ManyToOne
    @JoinColumn(name = "developer_id", nullable = true)
    @ToString.Exclude // Prevents Developer -> Task -> Developer toString() loop
    @EqualsAndHashCode.Exclude // Prevents Developer -> Task -> Developer equals/hashCode loop
    private Developer developer;

    public Task() {}

    @Override
    public String toString() {
        return "Task{id=" + id + ", title=" + title + ", description=" + description +
                ", dueDate=" + dueDate + ", status=" + status + "}";
    }
}


