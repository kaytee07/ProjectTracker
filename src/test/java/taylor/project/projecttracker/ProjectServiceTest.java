package taylor.project.projecttracker;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taylor.project.projecttracker.Entity.Project;
import taylor.project.projecttracker.Entity.Status;
import taylor.project.projecttracker.Exception.ProjectNotFoundExpetion;
import taylor.project.projecttracker.Record.ProjectRecords.CreateProjectRequest;
import taylor.project.projecttracker.Record.ProjectRecords.ProjectResponse;
import taylor.project.projecttracker.Record.ProjectRecords.UpdateProjectRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.ProjectRepository;
import taylor.project.projecttracker.Repository.TaskRepository;
import taylor.project.projecttracker.Service.ProjectService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Alpha");
        project.setDescription("Alpha project");
        project.setDeadline(LocalDateTime.now().plusDays(10));
        project.setStatus(Status.COMPLETED);
    }

    @Test
    void createProject_shouldSaveAndLog() {
        CreateProjectRequest request = new CreateProjectRequest("Alpha", "Alpha project", LocalDateTime.now().plusDays(10), Status.IN_PROGRESS);
        when(projectRepository.save(any())).thenReturn(project);

        ProjectResponse response = projectService.createProject(request, "admin");

        assertEquals("Alpha", response.name());
        verify(auditLogRepository).save(any());
    }

    @Test
    void findProjectById_shouldReturnProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Project result = projectService.findProjectById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findProjectById_shouldThrowIfNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.findProjectById(1L));
    }

    @Test
    void findAllProjects_shouldReturnResponses() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectResponse> responses = projectService.findAllProjects();

        assertEquals(1, responses.size());
    }

    @Test
    void updateProject_shouldUpdateAndLog() {
        UpdateProjectRequest request = new UpdateProjectRequest("Beta", "Beta project", LocalDateTime.now().plusDays(15), Status.COMPLETED);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);

        ProjectResponse response = projectService.updateProject(1L, request, "admin");

        assertEquals("Beta", response.name());
        verify(auditLogRepository).save(any());
    }

    @Test
    void getProjectsWithoutTasks_shouldReturnList() {
        when(projectRepository.findProjectsWithoutTasks()).thenReturn(List.of(project));

        List<ProjectResponse> result = projectService.getProjectsWithoutTasks();

        assertEquals(1, result.size());
    }

    @Test
    void deleteProject_shouldDeleteProjectAndTasksAndLog() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L, "admin");

        verify(taskRepository).deleteByProjectId(1L);
        verify(projectRepository).delete(project);
        verify(auditLogRepository).save(any());
    }

    @Test
    void deleteProject_shouldThrowIfNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundExpetion.class, () -> projectService.deleteProject(1L, "admin"));
    }
}