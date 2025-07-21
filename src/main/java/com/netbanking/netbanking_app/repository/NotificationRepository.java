package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Procedure(procedureName = "SEND_NOTIFICATION")
    void triggerNotification(@Param("USER_ID") Long userId, @Param("MESSAGE") String message);

}
