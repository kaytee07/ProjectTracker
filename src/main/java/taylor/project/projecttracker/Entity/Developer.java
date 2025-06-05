package taylor.project.projecttracker.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "Developer")
@NoArgsConstructor
@AllArgsConstructor
public class Developer {

    @Id
    @SequenceGenerator(name = "dev_sequence", sequenceName = "dev_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "dev_sequence")
    @Column(name = "id", updatable = false)
    private long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @ManyToMany
    @JoinTable(
            name = "developer_skill",
            joinColumns = @JoinColumn(name = "developer_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    public void addTask(Task task) {
        tasks.add(task);
        task.setDeveloper(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setDeveloper(null);
    }
}



