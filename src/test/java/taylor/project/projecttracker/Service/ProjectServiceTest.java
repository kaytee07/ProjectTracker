package taylor.project.projecttracker.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Project;
import taylor.project.projecttracker.Entity.Status;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project createTestProject() {
        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus(Status.IN_PROGRESS);
        return project;
    }

    @Test
    void createProject_shouldSaveProjectAndLogAction() {
        // Arrange
        Project project = createTestProject();
        String actorName = "testUser";

        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Project result = projectService.createProject(project, actorName);

        // Assert
        assertNotNull(result);
        assertEquals("Test Project", result.getName());

        verify(projectRepository, times(1)).save(project);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void findProjectById_shouldReturnProjectWhenExists() {
        // Arrange
        Project project = createTestProject();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        // Act
        Project result = projectService.findProjectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findProjectById_shouldThrowExceptionWhenNotFound() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            projectService.findProjectById(1L);
        });
    }

    @Test
    void updateProject_shouldUpdateExistingProjectAndLogAction() {
        // Arrange
        Project existingProject = createTestProject();
        Project updatedProject = createTestProject();
        updatedProject.setName("Updated Name");
        updatedProject.setDescription("Updated Description");
        updatedProject.setStatus(Status.COMPLETED);

        String actorName = "testUser";

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Project result = projectService.updateProject(1L, updatedProject, actorName);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(Status.COMPLETED, result.getStatus());

        verify(projectRepository, times(1)).save(existingProject);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void updateProject_shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        Project updatedProject = createTestProject();
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            projectService.updateProject(1L, updatedProject, "testUser");
        });

        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_shouldDeleteAndLogActionWhenProjectExists() {
        // Arrange
        Project project = createTestProject();
        String actorName = "testUser";

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doNothing().when(projectRepository).delete(project);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());


        projectService.deleteProject(1L, actorName);


        verify(projectRepository, times(1)).delete(project);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void deleteProject_shouldDoNothingWhenProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        projectService.deleteProject(1L, "testUser");

        verify(projectRepository, never()).delete(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void logAction_shouldCreateAuditLogWithCorrectDetails() {
        Project project = createTestProject();
        String actionType = "CREATE";
        String entityType = "Project";
        String entityId = "1";
        String actorName = "testUser";

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            assertNotNull(log.getTimestamp());
            assertEquals(actionType, log.getActionType());
            assertEquals(entityType, log.getEntityType());
            assertEquals(entityId, log.getEntityId());
            assertEquals(actorName, log.getActorName());
            assertNotNull(log.getPayload());
            return log;
        });

        projectService.createProject(project, actorName);

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
