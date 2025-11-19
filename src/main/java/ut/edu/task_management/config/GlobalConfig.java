package ut.edu.task_management.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ut.edu.task_management.model.User; 
import ut.edu.task_management.repository.UserRepository;

@Configuration
public class GlobalConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String username = "testuser2"; 
            
            // 1. Chỉ tạo nếu user chưa tồn tại
            if (!userRepository.existsByUsername(username)) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode("123456")); 
                user.setFullName("Test User 2"); 
                
                user.setRole("ROLE_USER"); 
                
                userRepository.save(user);
                System.out.println(">>> Created default user: " + username);
            }
        };
    }
}
