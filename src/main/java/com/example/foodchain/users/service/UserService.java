package com.example.foodchain.users.service;

import com.example.foodchain.common.error.ConflictException;
import com.example.foodchain.common.error.NotFoundException;
import com.example.foodchain.notifications.email.EmailService;
import com.example.foodchain.users.dto.RegisterRequest;
import com.example.foodchain.users.entity.Role;
import com.example.foodchain.users.entity.User;
import com.example.foodchain.users.repository.UserRepository;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Public entry point of the users module. Other modules depend on this service,
 * never on {@link UserRepository} directly.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("EMAIL_TAKEN", "Cet email est déjà utilisé.", null);
        }
        User user = User.create(email, passwordEncoder.encode(request.password()), request.role());
        user = userRepository.save(user);
        // Fire-and-forget welcome email (async, never breaks registration).
        emailService.sendWelcome(user.getEmail(), user.getRole().name());
        return user;
    }

    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable."));
    }

    @Transactional(readOnly = true)
    public boolean isSeller(UUID id) {
        return getById(id).getRole().isSeller();
    }

    @Transactional(readOnly = true)
    public boolean isVerifiedSeller(UUID id) {
        User user = getById(id);
        return user.getRole().isSeller() && user.isVerified();
    }

    @Transactional
    public void setVerified(UUID id, boolean verified) {
        User user = getById(id);
        if (user.getRole() != Role.AGRICULTEUR) {
            throw new ConflictException("Seuls les vendeurs peuvent être vérifiés.");
        }
        user.setVerified(verified);
        userRepository.save(user);
    }
}
