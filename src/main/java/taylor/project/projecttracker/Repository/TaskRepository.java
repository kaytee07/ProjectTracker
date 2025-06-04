package taylor.project.projecttracker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import taylor.project.projecttracker.Entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
