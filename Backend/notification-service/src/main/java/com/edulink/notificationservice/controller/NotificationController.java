package com.edulink.notificationservice.controller;
import com.edulink.notificationservice.dto.ApiResponse;
import com.edulink.notificationservice.dto.NotificationDto;
import com.edulink.notificationservice.entity.Notification;
import com.edulink.notificationservice.service.NotificationService;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDto>> sendNotification(@Valid @RequestBody NotificationDto request) {
        log.info("Sending notification to: {} (id: {}, role: {})",
                request.getRecipientEmail(), request.getRecipientId(), request.getRecipientRole());
        Notification saved = notificationService.sendNotification(request.toEntity());
        log.info("Notification saved with id: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification sent", NotificationDto.fromEntity(saved)));
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('TEACHER','SCHOOL_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDto>> scheduleNotification(@Valid @RequestBody NotificationDto request) {
        log.info("Scheduling notification for {} at {}", request.getRecipientEmail(), request.getScheduledAt());
        Notification scheduled = notificationService.scheduleNotification(request.toEntity());
        log.info("Notification scheduled with id: {}", scheduled.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Notification scheduled", NotificationDto.fromEntity(scheduled)));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications(Authentication auth) {
        log.info("Fetching notifications for user: {}", auth.getName());
        List<Notification> notifications = notificationService.getNotificationsForUser(auth.getName(), auth.getDetails());
        log.info("Found {} notifications for user {}", notifications.size(), auth.getName());
        return ResponseEntity.ok(ApiResponse.success("Notifications", NotificationDto.fromEntities(notifications)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markRead(@PathVariable Long id) {
        Notification updated = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read", NotificationDto.fromEntity(updated)));
    }
}
