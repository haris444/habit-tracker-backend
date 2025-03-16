package theo.inc.habit_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import theo.inc.habit_tracker.model.Habit;
import theo.inc.habit_tracker.model.User;
import theo.inc.habit_tracker.repo.UserRepository;
import theo.inc.habit_tracker.security.CustomUserDetailsService;
import theo.inc.habit_tracker.security.JwtUtil;
import theo.inc.habit_tracker.service.HabitService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
@Autowired
private HabitService habitService; // Add this dependency

@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody User user) {
    // Check if username already exists
    if (userRepository.findByUsername(user.getUsername()) != null) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Username already exists");
        return ResponseEntity.badRequest().body(response);
    }

    // Encode the password before saving
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User savedUser = userRepository.save(user);

    // Create default habits for the new user
    createDefaultHabitsForUser(savedUser.getId());

    // Generate JWT token
    final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
    final String jwt = jwtUtil.generateToken(userDetails, savedUser.getId());

    Map<String, Object> response = new HashMap<>();
    response.put("token", jwt);
    response.put("user", Map.of(
            "id", savedUser.getId(),
            "username", savedUser.getUsername()
    ));

    return ResponseEntity.ok(response);
}

// Helper method to create default habits
private void createDefaultHabitsForUser(Long userId) {
    // Create first default habit
    Habit habit1 = new Habit();
    habit1.setName("Drink Water💧");
    habit1.setStreak(0);
    habit1.setXp(0);
    habit1.setLevel(1);
    habitService.createHabit(habit1, userId);
    
    // Create second default habit
    Habit habit2 = new Habit();
    habit2.setName("Delete this and add your habits😃");
    habit2.setStreak(0);
    habit2.setXp(0);
    habit2.setLevel(1);
    habitService.createHabit(habit2, userId);
}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword())
            );
        } catch (BadCredentialsException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginUser.getUsername());
        final User user = userRepository.findByUsername(loginUser.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails, user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername()
        ));

        return ResponseEntity.ok(response);
    }
}