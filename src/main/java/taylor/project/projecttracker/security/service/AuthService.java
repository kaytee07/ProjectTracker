package taylor.project.projecttracker.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import taylor.project.projecttracker.exception.InvalidTokenException;
import taylor.project.projecttracker.dto.TokenRecords.TokenRecord;
import taylor.project.projecttracker.dto.TokenRecords.TokenRecordResponse;
import taylor.project.projecttracker.security.util.JwtTokenUtil;

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
