package taylor.project.projecttracker.utilityInterfaces;

import taylor.project.projecttracker.entity.Status;

public interface TaskStatusCount {
    Status getStatus();
    Long getCount();
}

