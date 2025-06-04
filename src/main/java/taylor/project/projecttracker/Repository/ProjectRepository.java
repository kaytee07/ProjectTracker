package taylor.project.projecttracker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taylor.project.projecttracker.Entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
