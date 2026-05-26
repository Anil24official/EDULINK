package com.edulink.identityservice.service;

import com.edulink.identityservice.dto.AuditLogDto;
import com.edulink.identityservice.entity.AuditLog;
import com.edulink.identityservice.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(String userEmail, String userRole, String action,
                       String resource, String details, String status) {
        try {
            AuditLog entry = new AuditLog(userEmail, userRole, action, resource,
                    details, status, currentIpAddress());
            auditLogRepository.save(entry);
        } catch (Exception ex) {
            log.warn("Failed to persist audit log entry: {}", ex.getMessage());
        }
    }

    public void recordSuccess(String userEmail, String userRole, String action,
                              String resource, String details) {
        record(userEmail, userRole, action, resource, details, "SUCCESS");
    }

    public void recordFailure(String userEmail, String userRole, String action,
                              String resource, String details) {
        record(userEmail, userRole, action, resource, details, "FAILURE");
    }

    public Page<AuditLogDto> list(int page, int size, String userEmail, String action) {
        PageRequest pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 200));
        Page<AuditLog> result;
        if (userEmail != null && !userEmail.isBlank()) {
            result = auditLogRepository.findByUserEmailOrderByTimestampDesc(userEmail, pageable);
        } else if (action != null && !action.isBlank()) {
            result = auditLogRepository.findByActionOrderByTimestampDesc(action, pageable);
        } else {
            result = auditLogRepository.findAllByOrderByTimestampDesc(pageable);
        }
        return result.map(this::toDto);
    }

    private AuditLogDto toDto(AuditLog e) {
        AuditLogDto d = new AuditLogDto();
        d.setId(e.getId());
        d.setUserEmail(e.getUserEmail());
        d.setUserRole(e.getUserRole());
        d.setAction(e.getAction());
        d.setResource(e.getResource());
        d.setDetails(e.getDetails());
        d.setStatus(e.getStatus());
        d.setIpAddress(e.getIpAddress());
        d.setTimestamp(e.getTimestamp());
        return d;
    }

    private String currentIpAddress() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest req = attrs.getRequest();
            String header = req.getHeader("X-Forwarded-For");
            if (header != null && !header.isBlank()) {
                return header.split(",")[0].trim();
            }
            return req.getRemoteAddr();
        } catch (Exception ex) {
            return null;
        }
    }
}
