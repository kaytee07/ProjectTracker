package taylor.project.projecttracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.entity.User;
import taylor.project.projecttracker.dto.TokenRecords.TokenRecord;
import taylor.project.projecttracker.dto.TokenRecords.TokenRecordResponse;
import taylor.project.projecttracker.security.service.AuthService;
import taylor.project.projecttracker.service.UserService;

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
