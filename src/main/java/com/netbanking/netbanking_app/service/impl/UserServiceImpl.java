package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.dto.UserDTO;
import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.UserRepository;
import com.netbanking.netbanking_app.service.AuditLogService;
import com.netbanking.netbanking_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuditLogService auditLogService;


    @Override
    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void updateUser(User user) {
        userRepo.save(user);

        auditLogService.logAction(
                "USER_UPDATE",
                user.getUsername(),
                user.getUserId(),
                user.getUsername(),
                "User profile updated: " + user.getUsername(),
                false,
                null
        );

    }

    public boolean changePassword(String username, String oldPassword, String newPassword, String confirmPassword) {
        Optional<User> optionalUser = userRepo.findByUsername(username);
        if (optionalUser.isEmpty()) return false;
        User user = optionalUser.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) return false;
        if (!newPassword.equals(confirmPassword)) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        auditLogService.logAction(
                "PASSWORD_CHANGED",
                user.getUsername(),
                user.getUserId(),
                user.getUsername(),
                "Password changed successfully for user: " + user.getUsername(),
                false,
                null
        );

        return true;
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }



    @Override
    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setName(userDTO.getName());

        // Conditionally set defaults
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : "USER");
        user.setKycStatus(userDTO.getKycStatus() != null ? userDTO.getKycStatus() : "PENDING");
        user.setIsActive(userDTO.getIsActive() != null ? userDTO.getIsActive() : "Y");

        User savedUser = userRepository.save(user);

        auditLogService.logAction(
                "USER_REGISTER_DTO",
                savedUser.getUsername(),
                savedUser.getUserId(),
                savedUser.getUsername(),
                "User registered via DTO: " + savedUser.getUsername(),
                false,
                null
        );
        return savedUser;
    }

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) user.setRole("USER");
        if (user.getKycStatus() == null) user.setKycStatus("PENDING");
        if (user.getIsActive() == null) user.setIsActive("Y");

        User savedUser = userRepository.save(user);

        auditLogService.logAction(
                "USER_REGISTER",
                savedUser.getUsername(),
                savedUser.getUserId(),
                savedUser.getUsername(),
                "New user registered: " + savedUser.getUsername(),
                false,
                null
        );

        return savedUser;
    }


    @Override
    public User login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            } else {
                throw new RuntimeException("Incorrect password");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    public Long getUserIdByUsername(String name) {
        return 0L;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getKycStatus() == null) {
                user.setKycStatus("PENDING");
            }
        }
        return userRepo.findAll();
    }




    @Override
    public void createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive("Y"); // ✅ Default to active status for new user
        user.setRegistrationDate(LocalDate.now());
        auditLogService.logAction(
                "ADMIN_USER_CREATED",
                user.getUsername(),
                user.getUserId(),
                user.getUsername(),
                "Admin created new user account: " + user.getUsername(),
                false,
                null
        );
        userRepo.save(user);
    }


    @Override
    public void toggleUserActiveStatus(Long userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            if ("Y".equalsIgnoreCase(user.getIsActive())) {
                user.setIsActive("N");
            } else {
                user.setIsActive("Y");
            }
            userRepo.save(user);

            String newStatus = user.getIsActive();
            auditLogService.logAction(
                    "USER_STATUS_TOGGLED",
                    user.getUsername(),
                    user.getUserId(),
                    user.getUsername(),
                    "User active status changed to: " + newStatus,
                    false,
                    null
            );
        }
    }


    @Override
    public User updateKycStatus(Long userId, String newStatus) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setKycStatus(newStatus);
            userRepository.save(user);

            auditLogService.logAction(
                    "KYC_UPDATED",
                    user.getUsername(),
                    user.getUserId(),
                    user.getUsername(),
                    "KYC status updated to " + newStatus + " for user " + user.getUsername(),
                    false,
                    null
            );
        }

        return user;
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // ❌ Reject login if user is not active
        if (!"Y".equalsIgnoreCase(user.getIsActive())) {
            throw new DisabledException("Account is not active. Please contact the administrator.");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // Already encoded
                .roles(user.getRole()) // Example: "ADMIN", "CUSTOMER"
                .build();
    }


}
