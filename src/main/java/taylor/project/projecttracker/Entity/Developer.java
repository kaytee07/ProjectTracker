package taylor.project.projecttracker.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity
public class Developer {

    @Id
    @SequenceGenerator(name = "dev_sequence", sequenceName = "dev_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "dev_sequence")
    private long id;
    private String name;
    private String email;

    @Column(name = "skill")
    private String Skill;

    @OneToMany(mappedBy = "developer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;


}


