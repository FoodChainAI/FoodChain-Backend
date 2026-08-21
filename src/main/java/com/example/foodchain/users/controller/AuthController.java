package com.example.foodchain.users.controller;

import com.example.foodchain.common.security.JwtService;
import com.example.foodchain.common.security.SecurityUtils;
import com.example.foodchain.users.dto.AuthResponse;
import com.example.foodchain.users.dto.LoginRequest;
import com.example.foodchain.users.dto.RegisterRequest;
import com.example.foodchain.users.dto.UserResponse;
import com.example.foodchain.users.entity.User;
import com.example.foodchain.users.mapper.UserMapper;
import com.example.foodchain.users.security.AppUserPrincipal;
import com.example.foodchain.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Inscription, connexion et profil courant")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserService userService, UserMapper userMapper,
                          AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Créer un compte (choix du rôle)")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        AuthResponse body = AuthResponse.bearer(token, jwtService.getExpirationMs(), userMapper.toResponse(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(summary = "Se connecter et obtenir un JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.password()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Email ou mot de passe incorrect.");
        }
        AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
        User user = userService.getById(principal.getId());
        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        AuthResponse body = AuthResponse.bearer(token, jwtService.getExpirationMs(), userMapper.toResponse(user));
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Profil de l'utilisateur authentifié")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        User user = userService.getById(SecurityUtils.currentUserId());
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
