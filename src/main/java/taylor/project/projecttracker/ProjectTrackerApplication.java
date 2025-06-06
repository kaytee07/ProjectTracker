package taylor.project.projecttracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching
public class ProjectTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectTrackerApplication.class, args);

    }

}
