package taylor.project.projecttracker.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.Details.CustomOAuth2User;
import taylor.project.projecttracker.Entity.OTP;
import taylor.project.projecttracker.Entity.User;
import taylor.project.projecttracker.Record.TokenRecords.TokenRecord;
import taylor.project.projecttracker.Record.TokenRecords.TokenRecordResponse;
import taylor.project.projecttracker.Service.AuthService;
import taylor.project.projecttracker.Service.UserService;
import taylor.project.projecttracker.UtilityClass.JwtTokenUtil;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final UserService userService;
    private  final AuthService authService;


    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody User user) {

        userService.addUser(user);
        return ResponseEntity.ok("saved");
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<String> getOauth2Token(@RequestParam String token) {
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<TokenRecordResponse> refreshToken(@RequestBody TokenRecord body) {
        return ResponseEntity.ok(authService.refreshToken(body));
    }

}
