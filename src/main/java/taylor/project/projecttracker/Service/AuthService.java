package taylor.project.projecttracker.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.Details.SecurityUser;
import taylor.project.projecttracker.Entity.User;
import taylor.project.projecttracker.Exception.InvalidTokenException;
import taylor.project.projecttracker.Record.TokenRecords.TokenRecord;
import taylor.project.projecttracker.Record.TokenRecords.TokenRecordResponse;
import taylor.project.projecttracker.UtilityClass.JwtTokenUtil;

import java.util.Optional;

@Service
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    AuthService(UserDetailsService userDetailsService,
                JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public TokenRecordResponse refreshToken(TokenRecord tokenRecord) {
        UserDetails user = userDetailsService.loadUserByUsername(tokenRecord.username());
        boolean isValidated = jwtTokenUtil.validateToken(tokenRecord.token(), user);
        if (!isValidated) {
            throw new InvalidTokenException("Refresh token is expired or invalid");
        }

        String newAccessToken = jwtTokenUtil.generateToken(user);
        String newRefreshToken = jwtTokenUtil.refreshToken(user);

        return new TokenRecordResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer"
        );
    }
}
