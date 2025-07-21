package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.dto.UserDTO;
import com.netbanking.netbanking_app.model.User;

import java.util.List;

public interface UserService {

    // Existing methods
    User register(User user);

    void updateUser(User user);

    User registerUser(UserDTO userDTO);
    User login(String username, String password);
    User getUserByUsername(String username);

    User getUserById(Long userId);

    boolean changePassword(String username, String oldPassword, String newPassword, String confirmPassword);


    // ✅ New methods for Admin
    List<User> getAllUsers();
    User updateKycStatus(Long userId, String status);

    User findByUsername(String username);

    Long getUserIdByUsername(String name);

    void createUser(User user);
    void toggleUserActiveStatus(Long userId);

    User findById(Long userId);


}
