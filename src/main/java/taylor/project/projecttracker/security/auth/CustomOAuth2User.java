package taylor.project.projecttracker.security.auth;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import taylor.project.projecttracker.entity.User;
import taylor.project.projecttracker.security.util.JwtTokenUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;


public class CustomOAuth2User implements OAuth2User, UserDetails {

    private final User user;
    private final Map<String, Object> attributes;
    private final JwtTokenUtil jwtTokenUtil;

    public CustomOAuth2User(User user, Map<String, Object> attributes, JwtTokenUtil jwtTokenUtil) {
        this.user = user;
        this.attributes = attributes;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
    @Override
    public String getName() {
        return user.getEmail();
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public String getEmail() {
        return user.getEmail();
    }
    public String getToken() {
        return jwtTokenUtil.generateToken(this);
    }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}


