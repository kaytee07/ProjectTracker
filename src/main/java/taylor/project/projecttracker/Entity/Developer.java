package taylor.project.projecttracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "Developer")
@AllArgsConstructor
public class Developer {

    @Id
    @SequenceGenerator(name = "dev_sequence", sequenceName = "dev_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "dev_sequence")
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill")
    private Skill Skill;

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    public Developer(){}

    public void addTask(Task task) {
        tasks.add(task);
    }
    public void removeTask(Task task) {
        tasks.remove(task);
    }

}


