package taylor.project.projecttracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.security.auth.SecurityUser;
import taylor.project.projecttracker.entity.Role;
import taylor.project.projecttracker.entity.User;
import taylor.project.projecttracker.mappers.UserMapper;
import taylor.project.projecttracker.dto.UserRecords.UserResponse;
import taylor.project.projecttracker.repository.UserRepository;
import taylor.project.projecttracker.security.service.CustomUserDetailsService;
import taylor.project.projecttracker.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    SecurityUser securityUser;

    public UserController(UserService userService, UserRepository userRepository, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;

    }


    @PostMapping("/users")
    public ResponseEntity<UserResponse> addUser(@RequestBody User user) {
        UserResponse created = userService.addUser(user);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + auth.getName()));
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> setUserRole(@PathVariable Long id, @RequestParam Role role) {
        User user = userService.getUser(id);
        UserResponse updated = userService.setUserRole(user, role);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/skills")
    public ResponseEntity<User> updateUserSkills(@PathVariable Long id, @RequestBody Set<Long> skillIds) {
        User updated = userService.updateUserSkills(id, skillIds);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> findAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @RequestParam String actorName) {
        userService.deleteUser(id, actorName);
        return ResponseEntity.noContent().build();
    }
}

