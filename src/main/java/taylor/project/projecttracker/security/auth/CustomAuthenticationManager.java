package taylor.project.projecttracker.security.auth;

import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomAuthenticationManager extends ProviderManager {

    public CustomAuthenticationManager(UserDetailsService userDetailsService,
                                       PasswordEncoder passwordEncoder) {
        super(Collections.singletonList(createDaoProvider(userDetailsService, passwordEncoder)));
    }

    private static DaoAuthenticationProvider createDaoProvider(UserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService);
        dao.setPasswordEncoder(passwordEncoder);
        return dao;
    }
}

