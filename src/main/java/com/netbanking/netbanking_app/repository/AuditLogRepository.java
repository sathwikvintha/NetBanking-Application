package com.netbanking.netbanking_app.repository;

import com.netbanking.netbanking_app.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
