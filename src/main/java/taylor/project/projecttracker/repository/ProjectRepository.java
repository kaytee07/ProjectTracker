package taylor.project.projecttracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import taylor.project.projecttracker.entity.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.id NOT IN (SELECT DISTINCT t.project.id FROM Task t)")
    List<Project> findProjectsWithoutTasks();


}
