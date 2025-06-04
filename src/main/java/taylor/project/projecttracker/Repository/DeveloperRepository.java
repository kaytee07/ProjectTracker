package taylor.project.projecttracker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taylor.project.projecttracker.Entity.Developer;

public interface DeveloperRepository extends JpaRepository<Developer, Integer> {
}
