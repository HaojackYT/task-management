package ut.edu.task_management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ut.edu.task_management.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Implement endpoints (login, register) in the future.

    public ResponseEntity<?> placeholder() {
        return ResponseEntity.ok("Auth endpoints go here");
    }
}
