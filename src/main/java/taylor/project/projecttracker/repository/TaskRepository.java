package taylor.project.projecttracker.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import taylor.project.projecttracker.entity.Task;
import taylor.project.projecttracker.utilityInterfaces.TaskStatusCount;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Task findById(long id);
    List<Task> findByProjectId(Long projectId);
    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);
    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_TIMESTAMP AND t.status <> 'COMPLETED'")
    List<Task> findOverdueTasks();
    @Query("SELECT t.status AS status, COUNT(t) AS count FROM Task t GROUP BY t.status")
    List<TaskStatusCount> countTasksByStatus();
}
