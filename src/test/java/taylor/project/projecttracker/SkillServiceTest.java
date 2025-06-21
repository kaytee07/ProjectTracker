package taylor.project.projecttracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taylor.project.projecttracker.entity.Skill;
import taylor.project.projecttracker.entity.User;
import taylor.project.projecttracker.exception.SkillNotFoundException;
import taylor.project.projecttracker.repository.AuditLogRepository;
import taylor.project.projecttracker.repository.SkillRepository;
import taylor.project.projecttracker.repository.UserRepository;
import taylor.project.projecttracker.service.SkillService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private User user;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(1L);
        skill.setName("Java");

        user = new User();
        user.setId(1L);
        user.setUsername("Alice");
    }

    @Test
    void createSkill_shouldSaveAndLog() {
        when(skillRepository.findByName("Java")).thenReturn(Optional.empty());
        when(skillRepository.save(any())).thenReturn(skill);

        Skill result = skillService.createSkill(skill, "admin");

        assertEquals("Java", result.getName());
        verify(auditLogRepository).save(any());
    }

    @Test
    void createSkill_shouldThrowIfDuplicate() {
        when(skillRepository.findByName("Java")).thenReturn(Optional.of(skill));

        assertThrows(IllegalArgumentException.class, () -> skillService.createSkill(skill, "admin"));
    }

    @Test
    void updateSkill_shouldModifyAndLog() {
        Skill updated = new Skill();
        updated.setName("Python");

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(skillRepository.save(any())).thenReturn(skill);

        Skill result = skillService.updateSkill(1L, updated, "admin");

        assertEquals("Python", result.getName());
        verify(auditLogRepository).save(any());
    }

    @Test
    void getSkillById_shouldReturnSkill() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        Skill result = skillService.getSkillById(1L);

        assertEquals("Java", result.getName());
    }

    @Test
    void getSkillById_shouldThrowIfNotFound() {
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class, () -> skillService.getSkillById(1L));
    }

    @Test
    void deleteSkill_shouldDeleteAndLog() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        skillService.deleteSkill(1L, "admin");

        verify(skillRepository).delete(skill);
        verify(auditLogRepository).save(any());
    }

    @Test
    void getAllSkills_shouldReturnList() {
        when(skillRepository.findAll()).thenReturn(List.of(skill));

        List<Skill> result = skillService.getAllSkills();

        assertEquals(1, result.size());
    }

    @Test
    void addSkillToDeveloper_shouldAddAndLog() {
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(skillRepository.save(any())).thenReturn(skill);

        Skill result = skillService.addSkillToDeveloper(1L, 1L, "admin");

        assertNotNull(result);
        verify(skillRepository).save(skill);
        verify(auditLogRepository).save(any());
    }

    @Test
    void removeSkillFromDeveloper_shouldRemoveAndLog() {
        skill.addUser(user);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(skillRepository.save(any())).thenReturn(skill);

        Skill result = skillService.removeSkillFromDeveloper(1L, 1L, "admin");

        assertNotNull(result);
        verify(skillRepository).save(skill);
        verify(auditLogRepository).save(any());
    }
}
