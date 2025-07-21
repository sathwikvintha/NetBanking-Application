package com.netbanking.netbanking_app.service.impl;

import com.netbanking.netbanking_app.model.User;
import com.netbanking.netbanking_app.repository.UserRepository;
import com.netbanking.netbanking_app.service.AdminService;
import com.netbanking.netbanking_app.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        auditLogService.logAction(
                "ADMIN_FETCH",
                "admin", // Replace dynamically if admin username available
                null,
                "ALL_USERS",
                "Admin fetched list of " + users.size() + " users",
                false,
                null
        );

        return users;
    }

    @Override
    public void updateKycStatus(Long userId, String status) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setKycStatus(status);
            userRepository.save(user);

            auditLogService.logAction(
                    "KYC_UPDATED",
                    "admin", // Replace dynamically if available
                    user.getUserId(),
                    user.getUsername(),
                    "KYC status updated to " + status + " for user " + user.getUsername(),
                    false,
                    null
            );
        });
    }
}
