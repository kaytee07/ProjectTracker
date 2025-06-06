package taylor.project.projecttracker.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Task> tasks;

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setDeveloper(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setDeveloper(null);
    }

    @Override
    public String toString() {
        return "Developer{id=" + id + ", name=" + name + ", email=" + email + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Developer developer = (Developer) o;
        return Objects.equals(id, developer.id) &&
                Objects.equals(name, developer.name) &&
                Objects.equals(email, developer.email);
    }
}



