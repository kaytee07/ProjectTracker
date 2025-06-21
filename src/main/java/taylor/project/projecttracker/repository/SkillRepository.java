package taylor.project.projecttracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taylor.project.projecttracker.entity.Skill;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
}
