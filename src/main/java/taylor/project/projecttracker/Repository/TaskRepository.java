package taylor.project.projecttracker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taylor.project.projecttracker.Entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByDeveloperId(Long developerId);
}
