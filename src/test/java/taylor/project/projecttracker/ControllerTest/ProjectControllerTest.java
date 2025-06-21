package taylor.project.projecttracker.ControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import taylor.project.projecttracker.entity.Project;
import taylor.project.projecttracker.entity.Status;
import taylor.project.projecttracker.dto.ProjectRecords.CreateProjectRequest;
import taylor.project.projecttracker.dto.ProjectRecords.ProjectResponse;
import taylor.project.projecttracker.dto.ProjectRecords.ProjectSummary;
import taylor.project.projecttracker.dto.ProjectRecords.UpdateProjectRequest;
import taylor.project.projecttracker.service.ProjectService;


import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectService projectService;

    private ProjectResponse projectResponse;
    private ProjectSummary projectSummary;

    @TestConfiguration
    static class TestConfig {
        @Mock
        private ProjectService projectService;

        @Bean
        ProjectService projectService() {
            return projectService;
        }
    }

    @BeforeEach
    void setUp() {
        projectResponse = new ProjectResponse(1L, "Test Project", "Description", LocalDateTime.now(), Status.IN_PROGRESS);
        projectSummary = new ProjectSummary(1L, "Test Project Summary", LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProject_shouldCreateAndReturn200() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest("Test Project", "Description", LocalDateTime.now(), Status.IN_PROGRESS);

        Mockito.when(projectService.createProject(ArgumentMatchers.any(), ArgumentMatchers.anyString()))
                .thenReturn(projectResponse);

        mockMvc.perform(post("/api/projects")
                        .param("actorName", "admin@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void createProject_shouldForbiddenForNonAdminOrManager() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest("Test Project", "Description", LocalDateTime.now(), Status.IN_PROGRESS);

        mockMvc.perform(post("/api/projects")
                        .param("actorName", "someone@example.com")
                        .with(user("someone@example.com").roles("CONTRACTOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void getProjectById_shouldReturn200() throws Exception {
        Mockito.when(projectService.findProjectById(1L)).thenReturn(new Project());

        mockMvc.perform(get("/api/projects/{id}", 1L))
                .andExpect(status().isOk()); // 200
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateProject_shouldAllowForManagerOrAdmin() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest("New Title", "New Description", LocalDateTime.now(), Status.IN_PROGRESS);

        Mockito.when(projectService.updateProject(1L, request, "manager@example.com"))
                .thenReturn(projectResponse);

        mockMvc.perform(put("/api/projects/{id}", 1L)
                        .param("actorName", "manager@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    void updateProject_shouldForbiddenForContractor() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest("New Title", "New Description", LocalDateTime.now(), Status.IN_PROGRESS);

        mockMvc.perform(put("/api/projects/{id}", 1L)
                        .param("actorName", "contractor@example.com")
                        .with(user("contractor@example.com").roles("CONTRACTOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    void deleteProject_shouldForbiddenForNonAdminOrManager() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", 1L)
                        .param("actorName", "contractor@example.com")
                        .with(user("contractor@example.com").roles("CONTRACTOR")))
                .andExpect(status().isForbidden()); // 403
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProject_shouldAllowForAdminOrManager() throws Exception {
        Mockito.doNothing().when(projectService).deleteProject(1L, "admin@example.com");

        mockMvc.perform(delete("/api/projects/{id}", 1L)
                        .param("actorName", "admin@example.com"))
                .andExpect(status().isOk()) // 200
                .andExpect(content().string("Project deleted successfully"));
    }

    @Test
    void getAllProjects_shouldAllowAll() throws Exception {
        List<ProjectResponse> projects = List.of(projectResponse);
        Mockito.when(projectService.findAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getProjects_without_tasks_shouldAllowAll() throws Exception {
        List<ProjectResponse> projects = List.of(projectResponse);
        Mockito.when(projectService.getProjectsWithoutTasks()).thenReturn(projects);

        mockMvc.perform(get("/api/projects/without-tasks"))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "CONTRACTOR")
    void getProjectSummary_shouldAllowContractor() throws Exception {
        Mockito.when(projectService.findProjectSummaryByProjectId(1L))
                .thenReturn(projectSummary);

        mockMvc.perform(get("/api/projects/{id}/summary", 1L))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getProjectSummary_shouldForbiddenForNonContractor() throws Exception {
        mockMvc.perform(get("/api/projects/{id}/summary", 1L)
                        .with(user("someone@example.com").roles("MANAGER")))
                .andExpect(status().isForbidden()); // 403
    }
}