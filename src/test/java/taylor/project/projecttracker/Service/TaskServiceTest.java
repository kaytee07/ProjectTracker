package taylor.project.projecttracker.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taylor.project.projecttracker.Entity.*;
import taylor.project.projecttracker.Repository.*;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

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

    private Task createTestTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus(Status.PENDING);
        task.setDueDate(LocalDateTime.now().plusDays(7));
        return task;
    }

    private Developer createTestDeveloper() {
        Developer developer = new Developer();
        developer.setId(1L);
        developer.setName("John Doe");
        return developer;
    }

    private Project createTestProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        return project;
    }

    @Test
    void createTask_shouldSaveTaskAndLogAction() {
        // Arrange
        Task task = createTestTask();
        String actorName = "testUser";

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Task result = taskService.createTask(task, actorName);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());

        verify(taskRepository, times(1)).save(task);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void updateTask_shouldUpdateExistingTaskAndLogAction() {
        // Arrange
        Task existingTask = createTestTask();
        Task updatedTask = createTestTask();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setStatus(Status.IN_PROGRESS);

        String actorName = "testUser";

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Task result = taskService.updateTask(1L, updatedTask, actorName);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(Status.IN_PROGRESS, result.getStatus());

        verify(taskRepository, times(1)).save(existingTask);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        Task updatedTask = createTestTask();
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTask(1L, updatedTask, "testUser");
        });

        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_shouldDeleteAndLogActionWhenTaskExists() {
        // Arrange
        Task task = createTestTask();
        String actorName = "testUser";

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        taskService.deleteTask(1L, actorName);

        // Assert
        verify(taskRepository, times(1)).delete(task);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void deleteTask_shouldDoNothingWhenTaskNotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        taskService.deleteTask(1L, "testUser");

        // Assert
        verify(taskRepository, never()).delete(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void getTasksByProject_shouldReturnTaskList() {
        // Arrange
        List<Task> tasks = Arrays.asList(createTestTask());
        when(taskRepository.findByProjectId(1L)).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getTasksByProject(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
    }

    @Test
    void getTasksByDeveloper_shouldReturnTaskList() {
        // Arrange
        List<Task> tasks = Arrays.asList(createTestTask());
        when(taskRepository.findByDeveloperId(1L)).thenReturn(tasks);

        // Act
        List<Task> result = taskService.getTasksByDeveloper(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
    }

    @Test
    void assignTaskToDeveloper_shouldAssignDeveloperAndLogAction() {
        // Arrange
        Task task = createTestTask();
        Developer developer = createTestDeveloper();
        String actorName = "testUser";

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Task result = taskService.assignTaskToDeveloper(1L, 1L, actorName);

        // Assert
        assertNotNull(result);
        assertEquals(developer, result.getDeveloper());

        verify(taskRepository, times(1)).save(task);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void assignTaskToDeveloper_shouldThrowExceptionWhenTaskNotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.assignTaskToDeveloper(1L, 1L, "testUser");
        });

        verify(taskRepository, never()).save(any());
    }

    @Test
    void assignTaskToDeveloper_shouldThrowExceptionWhenDeveloperNotFound() {
        // Arrange
        Task task = createTestTask();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.assignTaskToDeveloper(1L, 1L, "testUser");
        });

        verify(taskRepository, never()).save(any());
    }

    @Test
    void logAction_shouldCreateAuditLogWithCorrectDetails() {
        // Arrange
        Task task = createTestTask();
        String actorName = "testUser";

        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            assertEquals("CREATE", log.getActionType());
            assertEquals("Task", log.getEntityType());
            assertEquals("1", log.getEntityId());
            assertEquals(actorName, log.getActorName());
            assertNotNull(log.getPayload());
            return log;
        });

        // Act
        Task result = taskService.createTask(task, actorName);

        // Assert
        assertNotNull(result);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
