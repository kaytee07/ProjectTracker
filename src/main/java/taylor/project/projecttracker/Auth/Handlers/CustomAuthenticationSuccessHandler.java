package taylor.project.projecttracker.Auth.Handlers;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import taylor.project.projecttracker.Details.CustomOAuth2User;
import taylor.project.projecttracker.UtilityClass.JwtTokenUtil;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, java.io.IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String jwt = jwtTokenUtil.generateToken(oAuth2User);
        String redirectURL = "https://localhost:8080/api/auth/oauth2/success?token=" + jwt;
        response.sendRedirect(redirectURL);
    }
}

