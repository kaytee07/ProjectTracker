package taylor.project.projecttracker.service;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.entity.*;
import taylor.project.projecttracker.exception.UserNotFoundException;
import taylor.project.projecttracker.mappers.UserMapper;
import taylor.project.projecttracker.dto.UserRecords.UserResponse;
import taylor.project.projecttracker.repository.AuditLogRepository;
import taylor.project.projecttracker.repository.SkillRepository;
import taylor.project.projecttracker.repository.UserRepository;
import taylor.project.projecttracker.security.util.JwtTokenUtil;

import java.time.Instant;
import java.util.*;

@Transactional
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SkillRepository skillRepository;
    private final AuditLogRepository auditLogRepository;
    private final JwtTokenUtil jwtTokenUtil;



    public UserService(UserRepository userRepository,
                       SkillRepository skillRepository,
                       AuditLogRepository auditLogRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;

    }

    public UserResponse addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_CONTRACTOR);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public UserResponse setUserRole(User user, Role role) {
        user.setRole(role);
        return UserMapper.toResponse(userRepository.save(user));
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public void deleteUser(Long id, String actorName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        List<Task> tasks = user.getTasks();

        for (Task task : tasks) {
            task.setUser(null);
        }

        userRepository.delete(user);
        logAction("DELETE", "User", String.valueOf(id), actorName, UserMapper.toResponse(user));
    }

    @Cacheable(value = "users", key = "#id")
    @Transactional()
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }

    @Cacheable(value = "users")
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @CacheEvict(value = "users", key = "#id")
    @Transactional
    public User updateUserSkills(Long id, Set<Long> skillIds) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));

        Set<Skill> newSkills = new HashSet<>(skillRepository.findAllById(skillIds));

        user.setSkills(newSkills);
        User updated = userRepository.save(user);
        logAction("UPDATE", "User", String.valueOf(id), "SYSTEM", UserMapper.toResponse(updated));
        return updated;
    }

    private void logAction(String actionType, String entityType, String entityId, String actorName, Object payload) {
        AuditLog log = new AuditLog();
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setActorName(actorName);
        log.setTimestamp(Instant.now());
        log.setPayload(Map.of("data", payload));
        auditLogRepository.save(log);
    }


}
