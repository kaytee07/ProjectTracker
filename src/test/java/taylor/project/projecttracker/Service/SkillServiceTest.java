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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private SkillService skillService;

    private Skill createTestSkill() {
        Skill skill = new Skill();
        skill.setId(1);
        skill.setName("Java");
        return skill;
    }

    private Developer createTestDeveloper() {
        Developer developer = new Developer();
        developer.setId(1L);
        developer.setName("John Doe");
        return developer;
    }

    @Test
    void createSkill_shouldSaveNewSkill() {
        // Arrange
        Skill skill = createTestSkill();
        String actorName = "admin";

        when(skillRepository.findByName("Java")).thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Skill result = skillService.createSkill(skill, actorName);

        // Assert
        assertNotNull(result);
        assertEquals("Java", result.getName());
        verify(skillRepository, times(1)).save(skill);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void createSkill_shouldThrowWhenSkillExists() {
        // Arrange
        Skill skill = createTestSkill();
        when(skillRepository.findByName("Java")).thenReturn(Optional.of(skill));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            skillService.createSkill(skill, "admin");
        });
        verify(skillRepository, never()).save(any());
    }

    @Test
    void updateSkill_shouldUpdateExistingSkill() {
        // Arrange
        Skill existing = createTestSkill();
        Skill updated = createTestSkill();
        updated.setName("Java 17");
        String actorName = "admin";

        when(skillRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(skillRepository.save(any(Skill.class))).thenReturn(updated);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Skill result = skillService.updateSkill(1, updated, actorName);

        // Assert
        assertEquals("Java 17", result.getName());
        verify(skillRepository, times(1)).save(existing);
    }

    @Test
    void deleteSkill_shouldDeleteWhenExists() {
        // Arrange
        Skill skill = createTestSkill();
        String actorName = "admin";

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        doNothing().when(skillRepository).delete(skill);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        skillService.deleteSkill(1, actorName);

        // Assert
        verify(skillRepository, times(1)).delete(skill);
    }

    @Test
    void addSkillToDeveloper_shouldLinkSkillAndDeveloper() {
        Skill skill = createTestSkill();
        Developer developer = createTestDeveloper();
        String actorName = "admin";

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        Skill result = skillService.addSkillToDeveloper(1, 1L, actorName);

        assertTrue(result.getDevelopers().contains(developer));
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void removeSkillFromDeveloper_shouldUnlinkSkillAndDeveloper() {
        Skill skill = createTestSkill();
        Developer developer = createTestDeveloper();
        skill.addDeveloper(developer);
        String actorName = "admin";

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Skill result = skillService.removeSkillFromDeveloper(1, 1L, actorName);

        // Assert
        assertFalse(result.getDevelopers().contains(developer));
        verify(skillRepository, times(1)).save(skill);
    }
}
