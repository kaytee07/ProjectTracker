package taylor.project.projecttracker.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taylor.project.projecttracker.Details.CustomOAuth2User;
import taylor.project.projecttracker.Entity.OTP;
import taylor.project.projecttracker.Entity.User;
import taylor.project.projecttracker.Service.UserService;
import taylor.project.projecttracker.UtilityClass.JwtTokenUtil;

@RequestMapping("/api/auth")
@RestController
public class AuthController {
    private final UserService userService;
    private  final JwtTokenUtil jwtTokenUtil;


    public AuthController(UserService userService, JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody User user) {

        userService.addUser(user);
        return ResponseEntity.ok("saved");
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<String> getOauth2Token(@RequestParam String token) {
        System.out.println(token);
        return ResponseEntity.ok(token);
    }



}
