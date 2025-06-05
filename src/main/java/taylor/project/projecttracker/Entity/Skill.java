package taylor.project.projecttracker.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@NoArgsConstructor
@Entity(name = "Skill")
public class Skill {

    @Id
    @SequenceGenerator(name = "skill_sequence", sequenceName = "skill_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "skill_sequence")
    @Column(name = "id", nullable = false, updatable = false)
    private int id;

    @NotBlank(message = "Skill name is required")
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "skills")
    private Set<Developer> developers = new HashSet<>();

    public Skill(String name) {
        this.name = name;
    }

    public void addDeveloper(Developer developer) {
        developers.add(developer);
        developer.getSkills().add(this);
    }

    public void removeDeveloper(Developer developer) {
        developers.remove(developer);
        developer.getSkills().remove(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return id == skill.id && Objects.equals(name, skill.name);
    }

}
