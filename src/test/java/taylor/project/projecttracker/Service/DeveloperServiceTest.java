package taylor.project.projecttracker.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taylor.project.projecttracker.Entity.AuditLog;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.Entity.Skill;
import taylor.project.projecttracker.Repository.AuditLogRepository;
import taylor.project.projecttracker.Repository.DeveloperRepository;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceTest {

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private DeveloperService developerService;

    private Developer createTestDeveloper() {
        Developer developer = new Developer();
        developer.setId(1L);
        developer.setName("John Doe");
        developer.setEmail("john.doe@example.com");
        developer.setSkills(Set.of(new Skill("Java"), new Skill("Python")));
        return developer;
    }


    @Test
    void createDeveloper_shouldSaveDeveloperAndLogAction() {
        // Arrange
        Developer developer = createTestDeveloper();
        String actorName = "admin";

        when(developerRepository.save(any(Developer.class))).thenReturn(developer);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        Developer result = developerService.createDeveloper(developer, actorName);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());

        verify(developerRepository, times(1)).save(developer);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void updateDeveloper_shouldUpdateExistingDeveloperAndLogAction() {
        Long developerId = 1L;
        Developer existingDeveloper = createTestDeveloper();
        Developer updatedDeveloper = createTestDeveloper();
        updatedDeveloper.setName("Updated Name");
        updatedDeveloper.setEmail("updated@example.com");
        updatedDeveloper.setSkills(Set.of(new Skill("Java"), new Skill("Spring"), new Skill("Kubernetes")));
        String actorName = "admin";

        when(developerRepository.findById(developerId)).thenReturn(Optional.of(existingDeveloper));
        when(developerRepository.save(any(Developer.class))).thenReturn(updatedDeveloper);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        Developer result = developerService.updateDeveloper(developerId, updatedDeveloper, actorName);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals(3, result.getSkills().size());
        assertTrue(result.getSkills().contains(new Skill("Kubernetes")));

        verify(developerRepository, times(1)).save(existingDeveloper);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void updateDeveloper_shouldThrowExceptionWhenDeveloperNotFound() {
        Long nonExistentId = 99L;
        Developer updatedDeveloper = createTestDeveloper();
        when(developerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            developerService.updateDeveloper(nonExistentId, updatedDeveloper, "admin");
        });

        verify(developerRepository, never()).save(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void deleteDeveloper_shouldDeleteAndLogActionWhenDeveloperExists() {
        // Arrange
        Long developerId = 1L;
        Developer developer = createTestDeveloper();
        String actorName = "admin";

        when(developerRepository.findById(developerId)).thenReturn(Optional.of(developer));
        doNothing().when(developerRepository).delete(developer);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        // Act
        developerService.deleteDeveloper(developerId, actorName);

        // Assert
        verify(developerRepository, times(1)).delete(developer);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void deleteDeveloper_shouldDoNothingWhenDeveloperNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(developerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        developerService.deleteDeveloper(nonExistentId, "admin");

        // Assert
        verify(developerRepository, never()).delete(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void logAction_shouldCreateAuditLogWithCorrectDetails() {
        // Arrange
        Developer developer = createTestDeveloper();
        String actorName = "admin";
        String expectedActionType = "CREATE";
        String expectedEntityType = "Developer";
        String expectedEntityId = "1";

        when(developerRepository.save(any(Developer.class))).thenReturn(developer);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            assertEquals(expectedActionType, log.getActionType());
            assertEquals(expectedEntityType, log.getEntityType());
            assertEquals(expectedEntityId, log.getEntityId());
            assertEquals(actorName, log.getActorName());
            assertNotNull(log.getTimestamp());
            assertNotNull(log.getPayload());
            return log;
        });

        // Act
        Developer result = developerService.createDeveloper(developer, actorName);

        // Assert
        assertNotNull(result);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void logAction_shouldIncludeDeveloperDataInPayload() {
        // Arrange
        Developer developer = createTestDeveloper();
        String actorName = "admin";

        when(developerRepository.save(any(Developer.class))).thenReturn(developer);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog log = invocation.getArgument(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) log.getPayload();
            Developer payloadDeveloper = (Developer) payload.get("data");
            assertEquals(developer.getName(), payloadDeveloper.getName());
            return log;
        });

        // Act
        developerService.createDeveloper(developer, actorName);

        // Assert
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
