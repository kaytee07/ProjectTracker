package taylor.project.projecttracker.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import taylor.project.projecttracker.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /*
    * Finds a user by their email address
    * Spring Data JPA automatically generate the query for this method
    * @param email the email address to query for
    * return An optional containing the user if found, or empty otherwise
     */
    Optional<User> findByEmail(String email);
}
