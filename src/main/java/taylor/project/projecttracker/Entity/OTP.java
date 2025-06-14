package taylor.project.projecttracker.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Data
@Entity
public class OTP {
    @Id
    @SequenceGenerator(name = "otp_sequence", sequenceName = "otp_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "otp_sequence")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "code")
    private String code;
}
