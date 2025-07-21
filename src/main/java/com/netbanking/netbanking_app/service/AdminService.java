package com.netbanking.netbanking_app.service;

import com.netbanking.netbanking_app.model.User;
import java.util.List;

public interface AdminService {
    List<User> getAllUsers();
    void updateKycStatus(Long userId, String status);
}
