package taylor.project.projecttracker.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import taylor.project.projecttracker.Entity.Developer;
import taylor.project.projecttracker.UtilityInterfaces.DeveloperTaskCount;

import java.util.List;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    @Query("""
    SELECT d.id AS developerId, d.name AS developerName, COUNT(t.id) AS taskCount
    FROM Developer d
    JOIN Task t ON t.developer.id = d.id
    GROUP BY d.id, d.name
    ORDER BY COUNT(t.id) DESC
    """)
    List<DeveloperTaskCount> findTop5DevelopersByTaskCount(Pageable pageable);

}
