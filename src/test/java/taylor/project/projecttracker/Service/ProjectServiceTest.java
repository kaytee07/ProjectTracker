package taylor.project.projecttracker.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Project;
import taylor.project.projecttracker.Entity.Status;
import taylor.project.projecttracker.Mappers.ProjectMapper;
import taylor.project.projecttracker.Record.ProjectRecords.CreateProjectRequest;
import taylor.project.projecttracker.Record.ProjectRecords.ProjectResponse;
import taylor.project.projecttracker.Record.ProjectRecords.UpdateProjectRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;
import taylor.project.projecttracker.Service.ProjectService;

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
        Project savedProject = createTestProject();
        CreateProjectRequest request = new CreateProjectRequest("Test Project", "Test Description", null, Status.IN_PROGRESS);
        String actorName = "testUser";

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        ProjectResponse result = projectService.createProject(request, actorName);

        assertNotNull(result);
        assertEquals("Test Project", result.name());

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void findProjectById_shouldReturnProjectResponseWhenExists() {
        Project project = createTestProject();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponse result = ProjectMapper.toResponse(projectService.findProjectById(1L));

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void findProjectById_shouldThrowExceptionWhenNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            projectService.findProjectById(1L);
        });
    }

    @Test
    void updateProject_shouldUpdateAndLogAction() {
        Project existingProject = createTestProject();
        String actorName = "testUser";
        UpdateProjectRequest updateRequest = new UpdateProjectRequest("Updated Name", "Updated Description", null, Status.COMPLETED);
        Project updatedEntity = createTestProject();
        updatedEntity.setName("Updated Name");
        updatedEntity.setDescription("Updated Description");
        updatedEntity.setStatus(Status.COMPLETED);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedEntity);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        ProjectResponse result = projectService.updateProject(1L, updateRequest, actorName);

        assertNotNull(result);
        assertEquals("Updated Name", result.name());
        assertEquals(Status.COMPLETED, result.status());
    }

    @Test
    void updateProject_shouldThrowExceptionWhenNotFound() {
        UpdateProjectRequest updateRequest = new UpdateProjectRequest("Updated", "Desc", null, Status.IN_PROGRESS);

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            projectService.updateProject(1L, updateRequest, "testUser");
        });

        verify(projectRepository, never()).save(any());
    }

    @Test
    void deleteProject_shouldDeleteAndLogWhenExists() {
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
    void deleteProject_shouldDoNothingWhenNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        projectService.deleteProject(1L, "testUser");

        verify(projectRepository, never()).delete(any());
        verify(auditLogRepository, never()).save(any());
    }
}

