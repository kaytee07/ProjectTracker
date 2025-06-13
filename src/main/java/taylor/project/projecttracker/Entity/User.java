package taylor.project.projecttracker.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity(name = "app_user")
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = SEQUENCE, generator = "user_sequence")
    private Long id;

    @NotNull
    @NotBlank(message = "first name is required")
    @Column(name = "first_name", nullable = false, length = 50, columnDefinition = "TEXT")
    private String firstName;

    @NotNull
    @NotBlank(message = "last name is required")
    @Column(name = "last_name", nullable = false, length = 50, columnDefinition = "TEXT")
    private String lastName;

    @NotNull
    @NotBlank(message = "email is required")
    @Column(name = "email", nullable = false, length = 50, columnDefinition = "TEXT")
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
