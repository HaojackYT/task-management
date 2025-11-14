package ut.edu.task_management.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import ut.edu.task_management.model.User;
import ut.edu.task_management.repository.UserRepository; 

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository; 

    @Autowired
    public JwtAuthFilter(JwtTokenProvider tokenProvider, UserRepository userRepository) { 
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {
        
        String header = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (token != null && tokenProvider.validateToken(token)) {
            String username = tokenProvider.getUsernameFromToken(token);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                User user = userRepository.findByUsername(username).orElse(null);

                if (user != null) {
                    
                    // --- ĐÂY LÀ DÒNG ĐÃ SỬA ---
                    // Bỏ "ROLE_" + đi, vì user.getRole() đã là "ROLE_USER"
                    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
                    // --- HẾT SỬA ---
                    
                    List<GrantedAuthority> authorities = Collections.singletonList(authority);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        authorities 
                    );
                    
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}