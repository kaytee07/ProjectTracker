package taylor.project.projecttracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.SEQUENCE;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity(name = "Project")
public class Project {

    @Id
    @SequenceGenerator(
            name = "project_sequence",
            sequenceName = "project_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = SEQUENCE, generator = "project_sequence")
    private Long id;

    @NotBlank(message = "Project name is required")
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @NotBlank(message = "Project description is required")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Project deadline is required")
    @Future(message = "Deadline must be in the future")
    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @NotNull(message = "Project status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    private List<Task> tasks = new ArrayList<>();

    public Project() {}

    public void add(Task task) {
        tasks.add(task);
    }

    public void remove(Task task) {
        tasks.remove(task);
    }


    @Override
    public String toString() {
        return "Project{id=" + id + ", name=" + name + ", description=" + description +
                ", deadline=" + deadline + ", status=" + status + "}";
    }
}