package taylor.project.projecttracker.security.util;

import org.springframework.stereotype.Component;
import taylor.project.projecttracker.repository.TaskRepository;

@Component("securityUtil")
public class SecurityUtil {

    private final TaskRepository taskRepository;

    public SecurityUtil(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public boolean isOwner(Long taskId, String username) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getUsername().equals(username))
                .isPresent();
    }
}

