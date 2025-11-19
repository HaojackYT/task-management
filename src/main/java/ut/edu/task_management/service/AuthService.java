package ut.edu.task_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ut.edu.task_management.security.JwtTokenProvider;
import ut.edu.task_management.model.User;
import org.springframework.security.crypto.password.PasswordEncoder; 

@Service
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserService userService, 
                       JwtTokenProvider tokenProvider, 
                       PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials")); 

        if (passwordEncoder.matches(password, user.getPassword())) {
            return tokenProvider.generateToken(username);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
}
