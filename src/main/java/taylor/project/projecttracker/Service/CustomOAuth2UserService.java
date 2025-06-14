package taylor.project.projecttracker.Service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Details.CustomOAuth2User;
import taylor.project.projecttracker.Entity.Role;
import taylor.project.projecttracker.Entity.User;
import taylor.project.projecttracker.Repository.UserRepository;
import taylor.project.projecttracker.UtilityClass.JwtTokenUtil;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public CustomOAuth2UserService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String provider = userRequest.getClientRegistration().getRegistrationId();




        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setProvider(provider);
                    newUser.setRole(Role.ROLE_CONTRACTOR);
                    return userRepository.save(newUser);
                });

        return new CustomOAuth2User(user, oAuth2User.getAttributes(), jwtTokenUtil);
    }
}
