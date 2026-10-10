package com.student360.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student360.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository users) {
        return email -> users.findByEmail(email)
            .filter(u -> u.isEnabled())
            .map(u -> (UserDetails) org.springframework.security.core.userdetails.User.withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRole())
                .build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found or disabled"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .exceptionHandling(e -> e
                .authenticationEntryPoint((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                    body.put("message", "Authentication required");
                    objectMapper.writeValue(res.getOutputStream(), body);
                })
                .accessDeniedHandler((req, res, ex) -> {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("status", HttpServletResponse.SC_FORBIDDEN);
                    body.put("message", "Access denied");
                    objectMapper.writeValue(res.getOutputStream(), body);
                })
            )
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/auth/**").permitAll()

                // Admin User Management
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // Academic setup (Dept, Course, Subject, Teacher)
                .requestMatchers(HttpMethod.GET, "/api/departments/**", "/api/courses/**", "/api/subjects/**", "/api/teachers/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                .requestMatchers("/api/departments/**", "/api/courses/**", "/api/subjects/**", "/api/teachers/**").hasRole("ADMIN")

                // Student Profile & Listing
                .requestMatchers(HttpMethod.GET, "/api/students/*/profile", "/api/students/profile/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/students/**").hasRole("ADMIN")

                // Attendance
                .requestMatchers("/api/attendance/bulk", "/api/attendance/subject/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/attendance/**").authenticated()

                // Marks
                .requestMatchers(HttpMethod.POST, "/api/marks/**").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/marks/**").authenticated()

                // Assignments
                .requestMatchers(HttpMethod.POST, "/api/assignments/*/submit").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/assignments/**").authenticated()
                .requestMatchers("/api/assignments/**").hasAnyRole("ADMIN", "TEACHER")

                // Fees
                .requestMatchers(HttpMethod.GET, "/api/fees/student/**", "/api/fees/my", "/api/fees/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/fees/*/pay").authenticated()
                .requestMatchers("/api/fees/**").hasRole("ADMIN")

                // Notices
                .requestMatchers(HttpMethod.GET, "/api/notices/**").authenticated()
                .requestMatchers("/api/notices/**").hasAnyRole("ADMIN", "TEACHER")

                // Timetable
                .requestMatchers(HttpMethod.GET, "/api/timetable/**").authenticated()
                .requestMatchers("/api/timetable/**").hasRole("ADMIN")

                // Dashboard
                .requestMatchers("/api/dashboard/**").authenticated()

                // Catch-all API and static resources
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
