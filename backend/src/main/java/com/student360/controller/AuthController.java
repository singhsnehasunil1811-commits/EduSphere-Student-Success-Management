package com.student360.controller;
import com.student360.dto.LoginRequest;
import com.student360.entity.User;
import com.student360.repository.UserRepository;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager; private final UserRepository users;
    public AuthController(AuthenticationManager authManager, UserRepository users) { this.authManager = authManager; this.users = users; }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest r, HttpServletRequest req, HttpServletResponse res) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(r.email(), r.password()));
            User u = users.findByEmail(r.email()).orElseThrow();
            if (!u.getRole().equalsIgnoreCase(r.role()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Selected role does not match this account"));
            SecurityContext ctx = SecurityContextHolder.createEmptyContext(); ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
            new HttpSessionSecurityContextRepository().saveContext(ctx, req, res);
            return ResponseEntity.ok(Map.of("fullName", u.getFullName(), "email", u.getEmail(), "role", u.getRole()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not logged in"));
        User u = users.findByEmail(auth.getName()).orElseThrow();
        return ResponseEntity.ok(Map.of("fullName", u.getFullName(), "email", u.getEmail(), "role", u.getRole()));
    }
    @PostMapping("/logout")
    public Map<String, String> logout(HttpServletRequest req) {
        HttpSession s = req.getSession(false); if (s != null) s.invalidate();
        SecurityContextHolder.clearContext();
        return Map.of("message", "Logged out");
    }
}
