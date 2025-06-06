package taylor.project.projecttracker;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Sort;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Project;
import taylor.project.projecttracker.Entity.Status;
import taylor.project.projecttracker.Entity.Task;
import taylor.project.projecttracker.Exception.TaskNotFoundException;
import taylor.project.projecttracker.Record.TaskRecords.TaskResponse;
import taylor.project.projecttracker.Record.TaskRecords.UpdateTaskRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;
import taylor.project.projecttracker.Repository.TaskRepository;
import taylor.project.projecttracker.Service.TaskService;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private TaskService taskService;

    private Task existingTask;
    private Developer developer;
    private Project project;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setId(1L);
        project.setName("Project X");

        developer = new Developer();
        developer.setId(1L);
        developer.setName("Alice");

        existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setTitle("Existing Task");
        existingTask.setDescription("Desc");
        existingTask.setStatus(Status.IN_PROGRESS);
        existingTask.setDueDate(LocalDateTime.now().plusDays(5));
        existingTask.setProject(project);
    }



    @Test
    public void testGetSortedTasks_asc() {
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("A Task");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("B Task");

        when(taskRepository.findAll(Sort.by("title").ascending()))
                .thenReturn(List.of(task1, task2));

        List<TaskResponse> tasks = taskService.getSortedTasks("title", "asc");

        assertEquals(2, tasks.size());
        assertEquals("A Task", tasks.get(0).title());
    }
}


