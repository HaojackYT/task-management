package ut.edu.task_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ut.edu.task_management.security.JwtTokenProvider;
import ut.edu.task_management.model.User;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    private final JwtTokenProvider tokenProvider = new JwtTokenProvider();

    public String login(String username, String password) {
        // placeholder: real implementation should verify password and throw exceptions on failure
        User user = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        // password verification would be done here
        return tokenProvider.generateToken(username);
    }
}
