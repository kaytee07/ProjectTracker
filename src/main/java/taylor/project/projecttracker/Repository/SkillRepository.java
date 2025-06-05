package taylor.project.projecttracker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taylor.project.projecttracker.Entity.Skill;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
}
