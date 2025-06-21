package taylor.project.projecttracker.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import taylor.project.projecttracker.entity.Role;
import taylor.project.projecttracker.entity.User;
import taylor.project.projecttracker.repository.UserRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);

            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@example.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRole(Role.ROLE_MANAGER);
            userRepository.save(manager);

            User contractor = new User();
            contractor.setUsername("contractor");
            contractor.setEmail("contractor@example.com");
            contractor.setPassword(passwordEncoder.encode("contractor123"));
            contractor.setRole(Role.ROLE_CONTRACTOR);
            userRepository.save(contractor);

            User developer = new User();
            developer.setUsername("developer");
            developer.setEmail("developer@example.com");
            developer.setPassword(passwordEncoder.encode("developer123"));
            developer.setRole(Role.ROLE_DEVELOPER);
            userRepository.save(developer);
        }
    }
}

