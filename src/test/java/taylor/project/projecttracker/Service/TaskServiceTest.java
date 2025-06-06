//package taylor.project.projecttracker.Service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import taylor.project.projecttracker.Entity.*;
//import taylor.project.projecttracker.Mappers.TaskMapper;
//import taylor.project.projecttracker.Record.TaskRecords.CreateTaskRequest;
//import taylor.project.projecttracker.Record.TaskRecords.TaskResponse;
//import taylor.project.projecttracker.Record.TaskRecords.UpdateTaskRequest;
//import taylor.project.projecttracker.Repository.*;
//
//import jakarta.persistence.EntityNotFoundException;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TaskServiceTest {
//
//    @Mock private TaskRepository taskRepository;
//    @Mock private DeveloperRepository developerRepository;
//    @Mock private ProjectRepository projectRepository;
//    @Mock private AuditLogRepository auditLogRepository;
//
//    @InjectMocks private TaskService taskService;
//
//    // Test Data Builders
//    private CreateTaskRequest createTestTaskRequest() {
//        return new CreateTaskRequest(
//                "Test Task",
//                "Build an entire Task",
//                LocalDateTime.now().plusDays(7),
//                Status.PENDING,
//                1L,
//                null
//        );
//    }
//
//    private Task createTestTaskEntity() {
//        Task task = new Task();
//        task.setId(1L);
//        task.setTitle("Test Task");
//        task.setDescription("Build an entire Task");
//        task.setStatus(Status.PENDING);
//        task.setDueDate(LocalDateTime.now().plusDays(7));
//        task.setProject(createTestProject());
//        return task;
//    }
//
//    private Project createTestProject() {
//        Project project = new Project();
//        project.setId(1L);
//        project.setName("Test Project");
//        project.setDescription("Test Project Description");
//        project.setStatus(Status.PENDING);
//        project.setDeadline(LocalDateTime.now().plusDays(7));
//        return project;
//    }
//
//    private Developer createTestDeveloper() {
//        Developer developer = new Developer();
//        developer.setId(1L);
//        developer.setName("John Doe");
//        return developer;
//    }
//
//    // Tests
//
//    @Test
//    void createTask_shouldSaveTaskAndLogAction() {
//        CreateTaskRequest request = createTestTaskRequest();
//        Project project = createTestProject();
//        String actorName = "testUser";
//
//        Task taskEntity = TaskMapper.toEntity(request, project);
//        taskEntity.setId(1L);
//
//        when(taskRepository.save(any(Task.class))).thenReturn(taskEntity);
//        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());
//
//        TaskResponse result = taskService.createTask(request, actorName, project);
//
//        assertNotNull(result);
//        assertEquals("Test Task", result.title());
//
//        verify(taskRepository, times(1)).save(any(Task.class));
//        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
//    }
//
//    @Test
//    void updateTask_shouldUpdateExistingTaskAndLogAction() {
//        Task existingTask = createTestTaskEntity();
//
//        UpdateTaskRequest updatedRequest = new UpdateTaskRequest(
//                "Updated Title",
//                "Updated Description",
//                LocalDateTime.now().plusDays(10),
//                Status.IN_PROGRESS,
//                1L
//        );
//
//        String actorName = "testUser";
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
//        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());
//
//        TaskResponse result = taskService.updateTask(1L, updatedRequest, actorName);
//
//        assertNotNull(result);
//        assertEquals("Updated Title", result.title());
//        assertEquals("Updated Description", result.description());
//        assertEquals(Status.IN_PROGRESS, result.status());
//
//        verify(taskRepository, times(1)).save(any(Task.class));
//        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
//    }
//
//    @Test
//    void updateTask_shouldThrowExceptionWhenTaskNotFound() {
//        UpdateTaskRequest updatedRequest = new UpdateTaskRequest(
//                "Updated Title",
//                "Updated Description",
//                LocalDateTime.now().plusDays(10),
//                Status.IN_PROGRESS,
//                1L
//        );;
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> {
//            taskService.updateTask(1L, updatedRequest, "testUser");
//        });
//
//        verify(taskRepository, never()).save(any());
//    }
//
//    @Test
//    void deleteTask_shouldDeleteAndLogActionWhenTaskExists() {
//        Task task = createTestTaskEntity();
//        String actorName = "testUser";
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
//        doNothing().when(taskRepository).delete(task);
//        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());
//
//        taskService.deleteTask(1L, actorName);
//
//        verify(taskRepository, times(1)).delete(task);
//        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
//    }
//
//    @Test
//    void deleteTask_shouldDoNothingWhenTaskNotFound() {
//        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
//
//        taskService.deleteTask(1L, "testUser");
//
//        verify(taskRepository, never()).delete(any());
//        verify(auditLogRepository, never()).save(any());
//    }
//
//    @Test
//    void getTasksByProject_shouldReturnTaskList() {
//        Task task = createTestTaskEntity();
//        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(task));
//
//        List<TaskResponse> result = taskService.getTasksByProject(1L);
//
//        assertEquals(1, result.size());
//        assertEquals("Test Task", result.get(0).title());
//    }
//
//    @Test
//    void getTasksByDeveloper_shouldReturnTaskList() {
//        Task task = createTestTaskEntity();
//        when(taskRepository.findByDeveloperId(1L)).thenReturn(List.of(task));
//
//        List<TaskResponse> result = taskService.getTasksByDeveloper(1L);
//
//        assertEquals(1, result.size());
//        assertEquals("Test Task", result.get(0).title());
//    }
//
//    @Test
//    void assignTaskToDeveloper_shouldAssignDeveloperAndLogAction() {
//        Task task = createTestTaskEntity();
//        Developer developer = createTestDeveloper();
//        String actorName = "testUser";
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
//        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());
//
//        TaskResponse result = taskService.assignTaskToDeveloper(1L, 1L, actorName);
//
//        assertNotNull(result);
//        assertEquals(developer.getId(), result.developerId());
//
//        verify(taskRepository, times(1)).save(any(Task.class));
//        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
//    }
//
//    @Test
//    void assignTaskToDeveloper_shouldThrowExceptionWhenTaskNotFound() {
//        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> {
//            taskService.assignTaskToDeveloper(1L, 1L, "testUser");
//        });
//
//        verify(taskRepository, never()).save(any());
//    }
//
//    @Test
//    void assignTaskToDeveloper_shouldThrowExceptionWhenDeveloperNotFound() {
//        Task task = createTestTaskEntity();
//
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
//        when(developerRepository.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> {
//            taskService.assignTaskToDeveloper(1L, 1L, "testUser");
//        });
//
//        verify(taskRepository, never()).save(any());
//    }
//
//    @Test
//    void logAction_shouldCreateAuditLogWithCorrectDetails() {
//        CreateTaskRequest request = createTestTaskRequest();
//        Project project = createTestProject();
//        String actorName = "testUser";
//
//        Task task = TaskMapper.toEntity(request, project);
//        task.setId(1L);
//
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
//            AuditLog log = invocation.getArgument(0);
//            assertEquals("CREATE", log.getActionType());
//            assertEquals("Task", log.getEntityType());
//            assertEquals("1", log.getEntityId());
//            assertEquals(actorName, log.getActorName());
//            assertNotNull(log.getPayload());
//            return log;
//        });
//
//        TaskResponse result = taskService.createTask(request, actorName, project);
//
//        assertNotNull(result);
//        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
//    }
//}

