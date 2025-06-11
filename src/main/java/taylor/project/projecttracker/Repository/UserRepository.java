package taylor.project.projecttracker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taylor.project.projecttracker.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
