package taylor.project.projecttracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Skill;
import taylor.project.projecttracker.Entity.Task;
import taylor.project.projecttracker.Exception.DeveloperNotFoundException;
import taylor.project.projecttracker.Record.DeveloperRecords.DeveloperResponse;
import taylor.project.projecttracker.Record.DeveloperRecords.UpdateDeveloperRequest;
import taylor.project.projecttracker.Record.DeveloperRecords.UpdateDeveloperSkillRequest;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;
import taylor.project.projecttracker.Repository.SkillRepository;
import taylor.project.projecttracker.Repository.TaskRepository;
import taylor.project.projecttracker.Service.DeveloperService;
import taylor.project.projecttracker.UtilityInterfaces.DeveloperTaskCount;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceTest {

    @Mock
    private DeveloperRepository developerRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DeveloperService developerService;

    private Developer developer;

    @BeforeEach
    void setUp() {
        developer = new Developer();
        developer.setId(1L);
        developer.setName("John Doe");
        developer.setEmail("john@example.com");
    }

    @Test
    void createDeveloper_shouldSaveDeveloperAndLogAction() {
        when(developerRepository.save(any())).thenReturn(developer);

        Developer result = developerService.createDeveloper(developer, "admin");

        assertEquals(developer.getId(), result.getId());
        verify(auditLogRepository, times(1)).save(any());
    }

    @Test
    void updateDeveloper_shouldUpdateAndLog() {
        UpdateDeveloperRequest request = new UpdateDeveloperRequest("Jane", "jane@example.com");
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(developerRepository.save(any())).thenReturn(developer);

        Developer updated = developerService.updateDeveloper(1L, request, "admin");

        assertEquals("Jane", updated.getName());
        assertEquals("jane@example.com", updated.getEmail());
        verify(auditLogRepository).save(any());
    }

    @Test
    void deleteDeveloper_shouldUnassignTasksAndDeleteDeveloperAndLog() {
        Task task = new Task();
        task.setDeveloper(developer);
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(taskRepository.findByDeveloperId(1L)).thenReturn(List.of(task));

        developerService.deleteDeveloper(1L, "admin");

        verify(taskRepository).saveAll(any());
        verify(developerRepository).delete(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    void getDeveloper_shouldReturnDeveloper() {
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));

        Developer result = developerService.getDeveloper(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getDeveloper_shouldThrowIfNotFound() {
        when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DeveloperNotFoundException.class, () -> developerService.getDeveloper(1L));
    }

    @Test
    void findAllDevelopers_shouldReturnList() {
        when(developerRepository.findAll()).thenReturn(List.of(developer));

        List<Developer> result = developerService.findAllDevelopers();

        assertEquals(1, result.size());
    }

    @Test
    void updateDeveloperSkills_shouldUpdateSkills() {
        Skill skill = new Skill();
        skill.setId(1L);
        UpdateDeveloperSkillRequest request = new UpdateDeveloperSkillRequest(Set.of(1L));
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(skillRepository.findAllById(any())).thenReturn(List.of(skill));
        when(developerRepository.save(any())).thenReturn(developer);

        Developer updated = developerService.updateDeveloperSkills(1L, request);

        assertTrue(updated.getSkills().contains(skill));
    }

    @Test
    void getPaginatedDevelopers_shouldReturnPage() {
        Page<Developer> page = new PageImpl<>(List.of(developer));
        Pageable pageable = PageRequest.of(0, 1, Sort.by("name"));
        when(developerRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Developer> result = developerService.getPaginatedDevelopers(0, 1, "name", "asc");

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllDevelopersSorted_shouldReturnSortedList() {
        when(developerRepository.findAll(any(Sort.class))).thenReturn(List.of(developer));

        List<DeveloperResponse> result = developerService.getAllDevelopersSorted("name", "asc");

        assertEquals(1, result.size());
    }

}
