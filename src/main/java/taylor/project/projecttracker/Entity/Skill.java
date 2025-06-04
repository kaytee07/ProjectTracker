package taylor.project.projecttracker.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "Skill")
public class Skill {
    @Id
    @SequenceGenerator(name = "skill_sequence", sequenceName = "skill_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "skill_sequence")
    @Column( name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "skills")
    private Set<Developer> developers = new HashSet<>();


    private void addDeveloper(Developer developer) {
        developers.add(developer);
    }

    private void removeDeveloper(Developer developer) {
        developers.remove(developer);
    }
}